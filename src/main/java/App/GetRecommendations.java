package App;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

            // Uncomment to test error
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("https://api.myanimelist.net/v2/anime/" + malID))
//                    .header("X-MAL-CLIENT-ID", "a")
//                    .GET()
//                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONObject(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Display recommendations for favourite anime
    public void displayFavRecommendation() {
        // retrieves favourite anime for the first time
        JSONArray favouriteAnimes = getFavs();

        // user's favourite animes have already been retrieved
        if(favouriteAnimes != null) {
            user.addFavouriteAnimes(favouriteAnimes);
        }

        // randomly select an anime from teh arraylist and generate recommedations
        int randomAnime = getRandomAnime(user.getFavouriteAnimesID());
        JSONArray recommendations = findRecommendations(randomAnime);

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
//                            .getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH));
                    chosenAnimeImage.setIcon(image);
                } catch (Exception f) {
                    f.printStackTrace();
                    chosenAnimeImage.setIcon(new ImageIcon(new ImageIcon("src/main/resources/no-img.png").getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH)));
                }

                System.out.println(recommendations);

            } catch (JSONException g) {
                chosenAnimeText.setText("Error when retrieving anime details, Click Refresh or check environment variable if that doesn't work.");
                ImageIcon image = new ImageIcon(new ImageIcon("src/main/resources/error.png")
                        .getImage());
                chosenAnimeImage.setIcon(image);
            }
        }

        chosenAnimeText.setFont(app.headingFont);
        chosenAnime.add(chosenAnimeText);
        chosenAnime.add(chosenAnimeImage);

        chosenAnime.setLayout(new BoxLayout(chosenAnime, BoxLayout.Y_AXIS));
        chosenAnimeImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        chosenAnimeText.setAlignmentX(Component.CENTER_ALIGNMENT);

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

    // TODO maybe try to make it so that the screen changes first then displays the results
    // TODO maybe change size of anime image
    @Override
    public void actionPerformed(ActionEvent e) {
        app.panelBot.removeAll();
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
                switch(selection) {
                    case 0:
                        app.title.setText("Favourites");
                        app.panelBot.revalidate();
                        app.panelBot.repaint();
                        displayFavRecommendation();
                        break;
                }
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
        });
        return refreshButton;
    }
}
