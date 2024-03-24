package App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Scanner;

public class GetRecommendations implements ActionListener {
    AppGUI app;
    int selection;
    MALUser user;

    public GetRecommendations(AppGUI app, int optionSelection, MALUser user) {
        this.app = app;
        this.selection = optionSelection;
        this.user = user;
    }

    // Get users favourite animes as JSONArray
    private JSONArray getFavs() {
        // If user's favorite animes are already stored, then there is no need to get them again
        if(user.favouriteAnimesSize() > 0) {
            return null;
        }

        try{
            URL url = new URL("https://api.jikan.moe/v4/users/" + user.getUserName() + "/favorites");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if(responseCode != 200) {
                System.out.println("Username does not exist");
            } else {
                StringBuilder res = new StringBuilder();
                Scanner scan = new Scanner(url.openStream());

                while(scan.hasNext()) {
                    res.append(scan.nextLine());
                }
                scan.close();

                JSONObject obj = new JSONObject(res.toString());
                return obj.getJSONObject("data").getJSONArray("anime");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Randomly selects one anime from the favourites list to get recommendations
    public JSONArray findRecommendations(int randomAnime) {
        try{
            URL url = new URL("https://api.jikan.moe/v4/anime/" + randomAnime + "/recommendations");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if(responseCode != 200) {
                System.out.println(responseCode);
            } else {
                StringBuilder res = new StringBuilder();
                Scanner scan = new Scanner(url.openStream());

                while(scan.hasNext()) {
                    res.append(scan.nextLine());
                }
                scan.close();

                JSONObject obj = new JSONObject(res.toString());
                return obj.getJSONArray("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private JSONObject getRandomAnime(JSONArray arr) {
        int max = arr.length();
        int randomAnime = (int) (Math.random() * max);

        return arr.getJSONObject(randomAnime).getJSONObject("node");
    }

    private int getRandomAnime(ArrayList<Integer> animes) {
        int max = animes.size();
        int randomAnime = (int) (Math.random() * max);

        return animes.get(randomAnime);
    }

    // Get Details of selected Anime
    private JSONObject getAnimeDetails(int malID) {

        try{
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.myanimelist.net/v2/anime/" + malID))
                    .header("X-MAL-CLIENT-ID", System.getenv("MyAnimeListID"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONObject(response.body());
        } catch (Exception e) {
            return null;
        }
    }

    // Retrieves plan to watch list
    public JSONArray getPlanToWatch() {
        try{
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.myanimelist.net/v2/users/" + user.getUserName() + "/animelist?status=plan_to_watch&limit=1000"))
                    .header("X-MAL-CLIENT-ID", System.getenv("MyAnimeListID"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject o = new JSONObject(response.body());
            return o.getJSONArray("data");
        } catch (Exception e) {
            return null;
        }
    }

    // Display random anime from plan to watch list
    public void chooseFromPlanToWatch() {
        app.panelBot.removeAll();
        app.panelBot.revalidate();
        app.panelBot.repaint();

        // Get Users plan to watch list
        JSONArray planToWatchList = getPlanToWatch();
        JPanel chosenAnime = new JPanel();
        chosenAnime.setLayout(new BoxLayout(chosenAnime, BoxLayout.Y_AXIS));

        if(planToWatchList == null) {
            JLabel error = new JLabel("Error getting plan to watch list. Make sure environment variable is correct and try again.");
            error.setFont(app.headingFont);
            error.setAlignmentX(Component.CENTER_ALIGNMENT);
            chosenAnime.add(error);
        } else {
            // If users plan to watch list is empty
            if(planToWatchList.length() == 0) {
                // Display error message
                JLabel error = new JLabel("You do not have any anime in plan to watch. Add some anime in MyAnimeList or choose another option for recommendations.");
                error.setFont(app.headingFont);
                error.setAlignmentX(Component.CENTER_ALIGNMENT);
                chosenAnime.add(error);
            } else {
                // Choose random Anime from the list
                JSONObject animeToDisplay =  getRandomAnime(planToWatchList);

                // Display the anime
                JLabel title = new JLabel(animeToDisplay.getString("title"));
                title.setFont(app.headingFont);

                JLabel url = new JLabel("https://myanimelist.net/anime/" + animeToDisplay.getInt("id"));
                url.setFont(app.headingFont);

                url.setForeground(Color.BLUE.darker());
                url.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                url.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().browse(new java.net.URI(url.getText()));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });

                JLabel imgForDisplay = new JLabel();

                try {
                    URL urlImg = new URL(animeToDisplay.getJSONObject("main_picture").getString("medium"));
                    BufferedImage img = ImageIO.read(urlImg);
                    ImageIcon image = new ImageIcon(new ImageIcon(img)
                            .getImage());
                    imgForDisplay.setIcon(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                chosenAnime.add(title);
                chosenAnime.add(url);
                chosenAnime.add(imgForDisplay);
                title.setAlignmentX(Component.CENTER_ALIGNMENT);
                url.setAlignmentX(Component.CENTER_ALIGNMENT);
                imgForDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        }

        app.panelBot.add(chosenAnime);
        app.panelBot.revalidate();
        app.panelBot.repaint();
    }

    // Display recommendations for favourite anime
    public void displayFavRecommendation() {
        app.panelBot.removeAll();

        app.panelBot.revalidate();
        app.panelBot.repaint();

        // retrieves favourite anime for the first time
        JSONArray favouriteAnimes = getFavs();

        // user's favourite animes have already been retrieved
        if(favouriteAnimes != null) {
            user.addFavouriteAnimes(favouriteAnimes);
        }

        if(user.favouriteAnimesSize() == 0) {

            JLabel errorMsg = new JLabel("You have not selected any favourite animes. Add some on MyAnimeList or use another option for getting recommendations.");
            errorMsg.setFont(app.headingFont);
            errorMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
            app.panelBot.add(errorMsg);

        } else {
            // randomly select an anime from teh arraylist and generate recommendations
            int randomAnime = getRandomAnime(user.getFavouriteAnimesID());
            JSONArray recommendations = findRecommendations(randomAnime);

            // Displays the favourite anime selected
            JSONObject animeDetails = getAnimeDetails(randomAnime);
            JPanel chosenAnime = new JPanel();
            JLabel chosenAnimeText = new JLabel();
            JLabel chosenAnimeImage = new JLabel();
            if(animeDetails != null) {
                try {
                    chosenAnimeText.setText("Chosen Anime: \"" + animeDetails.getString("title") + "\"");

                    BufferedImage animeImg;
                    try {
                        URL url = new URL(animeDetails.getJSONObject("main_picture").getString("medium"));
                        animeImg = ImageIO.read(url);

                        ImageIcon image = new ImageIcon(new ImageIcon(animeImg)
                                .getImage());
                        chosenAnimeImage.setIcon(image);
                    } catch (Exception f) {
                        f.printStackTrace();
                        chosenAnimeImage.setIcon(new ImageIcon(new ImageIcon("src/main/resources/no-img.png").getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH)));
                    }

                    // Stores fields of recommendations to display
                    ArrayList<String> titles = new ArrayList<>();
                    ArrayList<String> urls = new ArrayList<>();
                    ArrayList<String> imgUrls = new ArrayList<>();
                    int recommendationLimit = 10;
                    for(int i = 0; i < recommendations.length() && i < recommendationLimit; i++) {
                        JSONObject o = recommendations.getJSONObject(i);
                        JSONObject entry = o.getJSONObject("entry");
                        titles.add(entry.getString("title"));
                        urls.add(entry.getString("url"));
                        imgUrls.add(entry.getJSONObject("images").getJSONObject("jpg").getString("image_url"));
                    }

                    // Display recommended anime
                    JPanel holdRecommendedAnimes = new JPanel();
                    holdRecommendedAnimes.setLayout(new BoxLayout(holdRecommendedAnimes, BoxLayout.Y_AXIS));

                    for(int i = 0; i < titles.size(); i++) {
                        JLabel animeTitle = new JLabel(titles.get(i));
                        JLabel animeURL = new JLabel(urls.get(i));
                        animeTitle.setFont(app.headingFont);
                        animeURL.setFont(app.headingFont);

                        JLabel animeImage = new JLabel();

                        try {
                            URL url = new URL(imgUrls.get(i));
                            BufferedImage img =ImageIO.read(url);
                            ImageIcon finalImg = new ImageIcon(new ImageIcon(img)
                                    .getImage());
                            animeImage.setIcon(finalImg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        animeURL.setForeground(Color.BLUE.darker());
                        animeURL.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        animeURL.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (Desktop.isDesktopSupported()) {
                                    try {
                                        Desktop.getDesktop().browse(new java.net.URI(animeURL.getText()));
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        });

                        JScrollPane displayArrayListAnimes = new JScrollPane(holdRecommendedAnimes);
                        displayArrayListAnimes.getVerticalScrollBar().setUnitIncrement(100);

                        chosenAnimeText.setFont(app.headingFont);
                        chosenAnime.add(chosenAnimeText);
                        chosenAnime.add(chosenAnimeImage);

                        chosenAnime.setLayout(new BoxLayout(chosenAnime, BoxLayout.Y_AXIS));
                        chosenAnimeImage.setAlignmentX(Component.CENTER_ALIGNMENT);
                        chosenAnimeText.setAlignmentX(Component.CENTER_ALIGNMENT);

                        app.panelBot.add(chosenAnime);
                        app.panelBot.add(Box.createVerticalStrut(20));
                        app.panelBot.add(displayArrayListAnimes);
                        app.panelBot.add(Box.createVerticalStrut(20));

                        holdRecommendedAnimes.add(animeTitle);
                        holdRecommendedAnimes.add(animeURL);
                        holdRecommendedAnimes.add(animeImage);
                        holdRecommendedAnimes.add(Box.createVerticalStrut(20));

                        animeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                        animeURL.setAlignmentX(Component.CENTER_ALIGNMENT);
                        animeImage.setAlignmentX(Component.CENTER_ALIGNMENT);
                    }

                } catch (JSONException g) {
                    chosenAnimeText.setText("Error when retrieving anime details, Click Refresh or check environment variable if that doesn't work.");
                    ImageIcon image = new ImageIcon(new ImageIcon("src/main/resources/error.png")
                            .getImage());
                    chosenAnimeImage.setIcon(image);

                    chosenAnimeText.setFont(app.headingFont);
                    chosenAnime.add(chosenAnimeText);
                    chosenAnime.add(chosenAnimeImage);

                    chosenAnime.setLayout(new BoxLayout(chosenAnime, BoxLayout.Y_AXIS));
                    chosenAnimeImage.setAlignmentX(Component.CENTER_ALIGNMENT);
                    chosenAnimeText.setAlignmentX(Component.CENTER_ALIGNMENT);

                    app.panelBot.add(chosenAnime);
                }
            }

            app.panelBot.revalidate();
            app.panelBot.repaint();
        }
    }

    // For Displaying buttons
    private void displayBackRefresh() {
        JPanel backButtonPanel = new JPanel(new GridBagLayout());
        JButton refreshButton = getRefreshButton();

        JButton backButton = new JButton("Back");
        backButton.setFont(app.headingFont);
        backButton.addActionListener(new BackButtonListener(app, 1, user));
        backButtonPanel.add(backButton);
        backButtonPanel.add(refreshButton);

        app.panelBot.add(backButtonPanel);

        app.panelBot.revalidate();
        app.panelBot.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        app.panelBot.removeAll();
        JLabel loading = new JLabel("Please Wait...");
        loading.setFont(app.headingFont);
        app.panelBot.add(loading);
        loading.setAlignmentX(Component.CENTER_ALIGNMENT);

        app.panelBot.revalidate();
        app.panelBot.repaint();

        switch(selection) {
            case 0:
                app.title.setText("Favourites");
                SwingUtilities.invokeLater(() -> {
                    displayFavRecommendation();
                    displayBackRefresh();
                });
                break;

            case 1:
                app.title.setText("Plan To Watch");
                SwingUtilities.invokeLater(()-> {
                    chooseFromPlanToWatch();
                    displayBackRefresh();
                });
                break;

            case 2:
                app.title.setText("Completed");
                break;

            case 3:
                app.title.setText("Currently Watching");
                break;

            case 4:
                app.title.setText("Random");
                break;
        }
    }

    // Refresh the chosen anime and recommendations
    private JButton getRefreshButton() {
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(app.headingFont);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.panelBot.removeAll();
                JLabel loading = new JLabel("Please Wait...");
                loading.setFont(app.headingFont);
                app.panelBot.add(loading);
                loading.setAlignmentX(Component.CENTER_ALIGNMENT);

                app.panelBot.revalidate();
                app.panelBot.repaint();

                switch(selection) {
                    case 0:
                        app.title.setText("Favourites");
                        app.panelBot.revalidate();
                        app.panelBot.repaint();
                        SwingUtilities.invokeLater(() -> {
                            displayFavRecommendation();
                            displayBackRefresh();
                        });
                        break;

                    case 1:
                        app.title.setText("Plan To Watch");
                        app.panelBot.revalidate();
                        app.panelBot.repaint();
                        SwingUtilities.invokeLater(()-> {
                            chooseFromPlanToWatch();
                            displayBackRefresh();
                        });
                        break;
                }
            }
        });
        return refreshButton;
    }
}
