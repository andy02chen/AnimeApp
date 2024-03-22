package App;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.HttpURLConnection;
import java.net.URL;
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

    // Randomly selects one anime from the favourites list to generate recommendations
    public JSONArray findRecommendations(ArrayList<Integer> animes) {
        int max = animes.size();
        int randomAnime = (int) (Math.random() * max);

        System.out.println(animes.get(randomAnime));

        try{
            URL url = new URL("https://api.jikan.moe/v4/anime/" + animes.get(randomAnime) + "/recommendations");
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
                return obj.getJSONArray("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // TODO maybe try to make it so that the screen changes first then displays the results
    // TODO add a refresh button for favourites
    @Override
    public void actionPerformed(ActionEvent e) {
        app.panelBot.removeAll();

        switch(selection) {
            case 0:
                app.title.setText("Favourites");
                JSONArray favouriteAnimes = getFavs();

                // user's favourite animes have already been retrieved
                if(favouriteAnimes != null) {
                    user.addFavouriteAnimes(favouriteAnimes);
                }

                JSONArray recommendations = findRecommendations(user.getFavouriteAnimesID());

                for(int i = 0; i < recommendations.length(); i++) {
                    System.out.println(recommendations.get(i));
                }

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

        JPanel backButtonPanel = new JPanel(new GridBagLayout());
        JButton backButton = new JButton("Back");
        backButton.setFont(app.headingFont);
        backButton.addActionListener(new BackButtonListener(app, 1, user));
        backButtonPanel.add(backButton);

        app.panelBot.add(backButtonPanel);

        app.panelBot.revalidate();
        app.panelBot.repaint();
    }
}
