package App;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class submitMALUser implements ActionListener {
    MALUser user;
    public submitMALUser(MALUser user) {
        this.user = user;
    }

    private static JSONObject getUserData(String username) {
        try{
            URL url = new URL("https://api.jikan.moe/v4/users/" + username);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if(responseCode != 200) {
                System.out.println("Username does not exist");
            } else {
                StringBuilder profileString = new StringBuilder();
                Scanner scan = new Scanner(url.openStream());

                while(scan.hasNext()) {
                    profileString.append(scan.nextLine());
                }
                scan.close();

                JSONObject obj = new JSONObject(profileString.toString());
                return obj.getJSONObject("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = user.getUserName();

        if(username.equals("Enter MAL Username") || username.isEmpty()) {
            user.enterUserName();
        } else {
            JSONObject profile = getUserData(username);

            if(profile == null) {
                user.doesNotExist();
            } else {
                user.setUserData(profile);
            }
        }
    }
}
