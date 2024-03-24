package App;

import org.json.JSONArray;
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
import java.util.Scanner;

public class GetRecommendations implements ActionListener {
    AppGUI app;
    int selection;
    MALUser user;
    private JSONArray favAnimes;
    private JSONArray planToWatchList;
    private JSONArray currWatchingAnime;

    public GetRecommendations(AppGUI app, int optionSelection, MALUser user) {
        this.app = app;
        this.selection = optionSelection;
        this.user = user;
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

    private JSONObject getRandomFavAnime(JSONArray arr) {
        int max = arr.length();
        int randomAnime = (int) (Math.random() * max);

        return arr.getJSONObject(randomAnime);
    }

    private JSONObject getRandomAnime(JSONArray arr) {
        int max = arr.length();
        int randomAnime = (int) (Math.random() * max);

        return arr.getJSONObject(randomAnime).getJSONObject("node");
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
        if(planToWatchList == null) {
            planToWatchList = getPlanToWatch();
        }

        JPanel chosenAnime = new JPanel();
        chosenAnime.setLayout(new BoxLayout(chosenAnime, BoxLayout.Y_AXIS));

        if(planToWatchList == null) {
            JLabel error = new JLabel("Error getting plan to watch list. Make sure environment variable is correct and try again.");
            error.setFont(app.headingFont);
            error.setAlignmentX(Component.CENTER_ALIGNMENT);
            chosenAnime.add(error);
        } else {
            // If users plan to watch list is empty
            if(planToWatchList.isEmpty()) {
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

    // Get user's favourite anime
    public JSONArray getFavAnimes() {
        try {
            URL url = new URL("https://api.jikan.moe/v4/users/" + user.getUserName() + "/favorites");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if(responseCode != 200) {
                System.out.println("Username does not exist");
            } else {
                StringBuilder res = new StringBuilder();
                Scanner scan = new Scanner(url.openStream());

                while (scan.hasNext()) {
                    res.append(scan.nextLine());
                }
                scan.close();

                JSONObject obj = new JSONObject(res.toString());
                return obj.getJSONObject("data").getJSONArray("anime");
            }
        } catch (Exception e) {
            return new JSONArray();

        }
        return new JSONArray();
    }

    public void displayFavRecommendation() {
        app.panelBot.removeAll();

        app.panelBot.revalidate();
        app.panelBot.repaint();

        // Get fav animes
        if(favAnimes == null) {
            favAnimes = getFavAnimes();
        }

        if(favAnimes.isEmpty()) {
            JLabel error = new JLabel("No Favourite anime found. Favourite some anime on MyAnimeList and try again later.");
            error.setFont(app.headingFont);
            error.setAlignmentX(Component.CENTER_ALIGNMENT);

            app.panelBot.add(error);
            app.panelBot.revalidate();
            app.panelBot.repaint();
            return;
        }

        // Displays the favourite anime selected
        JPanel chosenAnime = new JPanel();
        chosenAnime.setLayout(new BoxLayout(chosenAnime, BoxLayout.PAGE_AXIS));
        JLabel chosenAnimeText = new JLabel();
        JLabel chosenAnimeImage = new JLabel();

        JSONObject randomAnime = getRandomFavAnime(favAnimes);
        int randomAnimeID = randomAnime.getInt("mal_id");

        chosenAnimeText.setText("Chosen Anime: \"" + randomAnime.getString("title") + "\"");

        BufferedImage animeImg;
        try {
            URL urlImg = new URL(randomAnime.getJSONObject("images").getJSONObject("jpg").getString("image_url"));
            animeImg = ImageIO.read(urlImg);

            ImageIcon image = new ImageIcon(new ImageIcon(animeImg)
                    .getImage());
            chosenAnimeImage.setIcon(image);
        } catch (Exception f) {
            f.printStackTrace();
            chosenAnimeImage.setIcon(new ImageIcon(new ImageIcon("src/main/resources/no-img.png").getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH)));
        }

        chosenAnime.add(chosenAnimeText);
        chosenAnime.add(chosenAnimeImage);
        chosenAnimeText.setAlignmentX(Component.CENTER_ALIGNMENT);
        chosenAnimeImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        chosenAnimeText.setFont(app.headingFont);

        // Generate Recommendations
        JSONArray recommendations = findRecommendations(randomAnimeID);
        int recommendationLimit = 10;

        // Display Recommendations
        JPanel holdRecommendedAnimes = new JPanel();
        holdRecommendedAnimes.setLayout(new BoxLayout(holdRecommendedAnimes, BoxLayout.Y_AXIS));

        for(int i = 0; i < recommendationLimit && i < recommendations.length(); i++) {
            JSONObject recAnime = recommendations.getJSONObject(i).getJSONObject("entry");
            JLabel animeTitle = new JLabel(recAnime.getString("title"));
            JLabel animeURL = new JLabel(recAnime.getString("url"));
            animeTitle.setFont(app.headingFont);
            animeURL.setFont(app.headingFont);

            JLabel animeImage = new JLabel();

            try {
                URL recAnimeUrl = new URL(recAnime.getJSONObject("images").getJSONObject("jpg").getString("image_url"));
                BufferedImage img =ImageIO.read(recAnimeUrl);
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

            holdRecommendedAnimes.add(animeTitle);
            holdRecommendedAnimes.add(animeURL);
            holdRecommendedAnimes.add(animeImage);
            holdRecommendedAnimes.add(Box.createVerticalStrut(20));

            animeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            animeURL.setAlignmentX(Component.CENTER_ALIGNMENT);
            animeImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        JScrollPane displayAnimes = new JScrollPane(holdRecommendedAnimes);
        displayAnimes.getVerticalScrollBar().setUnitIncrement(100);

        chosenAnimeText.setFont(app.headingFont);
        chosenAnime.add(chosenAnimeText);
        chosenAnime.add(chosenAnimeImage);

        chosenAnime.setLayout(new BoxLayout(chosenAnime, BoxLayout.Y_AXIS));
        chosenAnimeImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        chosenAnimeText.setAlignmentX(Component.CENTER_ALIGNMENT);

        app.panelBot.add(chosenAnime);
        app.panelBot.add(Box.createVerticalStrut(20));
        app.panelBot.add(displayAnimes);
        app.panelBot.add(Box.createVerticalStrut(20));

        app.panelBot.revalidate();
        app.panelBot.repaint();
    }

    // Call API and get random anime
    private JSONObject apiRandomAnime() {
        try{
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.jikan.moe/v4/random/anime"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject o = new JSONObject(response.body());
            return o.getJSONObject("data");
        } catch (Exception e) {
            return null;
        }
    }

    // Generate recommendations that are similar to the ones currently watching
    private JSONArray getCurrWatchingAnime() {
        try{
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.myanimelist.net/v2/users/" + user.getUserName() + "/animelist?status=watching&limit=1000"))
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

    private void generateAnimeFromCurrWatching() {
        app.panelBot.removeAll();
        app.panelBot.revalidate();
        app.panelBot.repaint();

        if(currWatchingAnime == null) {
            currWatchingAnime = getCurrWatchingAnime();
        }

        if(currWatchingAnime.isEmpty()) {
            JLabel error = new JLabel("You are currently not watching any anime. Please select another option for recommendations.");
            error.setFont(app.headingFont);
            error.setAlignmentX(Component.CENTER_ALIGNMENT);

            app.panelBot.add(error);
            app.panelBot.revalidate();
            app.panelBot.repaint();
            return;
        }

        // Displays the anime selected
        JPanel chosenAnime = new JPanel();
        chosenAnime.setLayout(new BoxLayout(chosenAnime, BoxLayout.PAGE_AXIS));
        JLabel chosenAnimeText = new JLabel();
        chosenAnimeText.setFont(app.headingFont);
        JLabel chosenAnimeImage = new JLabel();

        JSONObject selectedAnime = getRandomAnime(currWatchingAnime);
        int id = selectedAnime.getInt("id");
        String title = selectedAnime.getString("title");
        chosenAnimeText.setText("Chosen Anime: \"" + title + "\"");
        String imgURL = selectedAnime.getJSONObject("main_picture").getString("medium");

        JLabel link = new JLabel("https://myanimelist.net/anime/" + id);
        link.setFont(app.headingFont);
        link.setForeground(Color.BLUE.darker());
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new java.net.URI(link.getText()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        BufferedImage animeImg;
        try {
            URL urlImg = new URL(imgURL);
            animeImg = ImageIO.read(urlImg);

            ImageIcon image = new ImageIcon(new ImageIcon(animeImg)
                    .getImage());
            chosenAnimeImage.setIcon(image);
        } catch (Exception f) {
            f.printStackTrace();
            chosenAnimeImage.setIcon(new ImageIcon(new ImageIcon("src/main/resources/no-img.png").getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH)));
        }

        chosenAnime.add(chosenAnimeText);
        chosenAnime.add(link);
        chosenAnime.add(chosenAnimeImage);
        chosenAnimeText.setAlignmentX(Component.CENTER_ALIGNMENT);
        link.setAlignmentX(Component.CENTER_ALIGNMENT);
        chosenAnimeImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSONArray similarAnime = findRecommendations(id);

        int recommendationLimit = 10;

        // Display Recommendations
        JPanel holdRecommendedAnimes = new JPanel();
        holdRecommendedAnimes.setLayout(new BoxLayout(holdRecommendedAnimes, BoxLayout.Y_AXIS));

        for(int i = 0; i < recommendationLimit && i < similarAnime.length(); i++) {
            JSONObject recAnime = similarAnime.getJSONObject(i).getJSONObject("entry");
            JLabel animeTitle = new JLabel(recAnime.getString("title"));
            JLabel animeURL = new JLabel(recAnime.getString("url"));
            animeTitle.setFont(app.headingFont);
            animeURL.setFont(app.headingFont);

            JLabel animeImage = new JLabel();

            try {
                URL recAnimeUrl = new URL(recAnime.getJSONObject("images").getJSONObject("jpg").getString("image_url"));
                BufferedImage img =ImageIO.read(recAnimeUrl);
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

            holdRecommendedAnimes.add(animeTitle);
            holdRecommendedAnimes.add(animeURL);
            holdRecommendedAnimes.add(animeImage);
            holdRecommendedAnimes.add(Box.createVerticalStrut(20));

            animeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            animeURL.setAlignmentX(Component.CENTER_ALIGNMENT);
            animeImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        JScrollPane displayAnimes = new JScrollPane(holdRecommendedAnimes);
        displayAnimes.getVerticalScrollBar().setUnitIncrement(100);

        app.panelBot.add(chosenAnime);
        app.panelBot.add(Box.createVerticalStrut(20));
        app.panelBot.add(displayAnimes);
        app.panelBot.add(Box.createVerticalStrut(20));

        app.panelBot.revalidate();
        app.panelBot.repaint();
    }

    private void generateRandomAnime() {
        app.panelBot.removeAll();
        app.panelBot.revalidate();
        app.panelBot.repaint();

        JSONObject randomAnime = apiRandomAnime();
        JPanel chosenAnime = new JPanel();
        chosenAnime.setLayout(new BoxLayout(chosenAnime, BoxLayout.PAGE_AXIS));
        JLabel title = new JLabel(randomAnime.getString("title"));
        title.setFont(app.headingFont);
        int malID = randomAnime.getInt("mal_id");
        JLabel animeURL = new JLabel("https://myanimelist.net/anime/" + malID);
        animeURL.setFont(app.headingFont);

        JLabel animeImage = new JLabel();
        try {
            URL recAnimeUrl = new URL(randomAnime.getJSONObject("images").getJSONObject("jpg").getString("image_url"));
            BufferedImage img =ImageIO.read(recAnimeUrl);
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

        chosenAnime.add(title);
        chosenAnime.add(animeURL);
        chosenAnime.add(animeImage);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        animeURL.setAlignmentX(Component.CENTER_ALIGNMENT);
        animeImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        app.panelBot.add(chosenAnime);
        app.panelBot.revalidate();
        app.panelBot.repaint();
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
                SwingUtilities.invokeLater(()-> {
                    generateAnimeFromCurrWatching();
                    displayBackRefresh();
                });
                break;

            case 4:
                app.title.setText("Random");
                SwingUtilities.invokeLater(()-> {
                    generateRandomAnime();
                    displayBackRefresh();
                });
                break;
        }
    }

    // Refresh the chosen anime and recommendations
    private JButton getRefreshButton() {
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(app.headingFont);

        refreshButton.addActionListener(e -> {
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

                case 3:
                    app.title.setText("Currently Watching");
                    app.panelBot.revalidate();
                    app.panelBot.repaint();
                    SwingUtilities.invokeLater(()-> {
                        generateAnimeFromCurrWatching();
                        displayBackRefresh();
                    });
                    break;

                case 4:
                    app.title.setText("Random");
                    app.panelBot.revalidate();
                    app.panelBot.repaint();
                    SwingUtilities.invokeLater(()-> {
                        generateRandomAnime();
                        displayBackRefresh();
                    });
                    break;
            }
        });
        return refreshButton;
    }
}
