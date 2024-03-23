package App;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;

public class MALUser implements ActionListener {
    AppGUI app;
    private JTextField username;
    private JLabel userPFP;
    private JPanel confirmPanel;
    private JSONObject userData;
    private JLabel confirmUserText;
    private final ArrayList<Integer> favouriteAnimesId;

    public MALUser(AppGUI app) {
        this.app = app;
        favouriteAnimesId = new ArrayList<>();
    }

    public void setConfirmUserText(String text) {
        confirmUserText.setText(text);
    }

    public int favouriteAnimesSize() {
        return favouriteAnimesId.size();
    }

    // Add IDs to favourite animes arraylist
    public void addFavouriteAnimes(JSONArray favouriteAnimes) {
        for(int i = 0; i < favouriteAnimes.length(); i++) {
            JSONObject anime = favouriteAnimes.getJSONObject(i);
            favouriteAnimesId.add(anime.getInt("mal_id"));
        }
    }

    public ArrayList<Integer> getFavouriteAnimesID() {
        return favouriteAnimesId;
    }

    public void enterUserName() {
        username.setText("Enter MAL Username");
        confirmUserText.setText("Please Enter a Username");
        userPFP.setIcon(new ImageIcon(new ImageIcon("src/main/resources/mal-logo.png").getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH)));
        confirmPanel.setVisible(false);
        app.panelBot.revalidate();
        app.panelBot.repaint();
    }

    // When username does not exist
    public void doesNotExist() {
        username.setText("Enter MAL Username");
        confirmUserText.setText("That Username does not exist!");
        userPFP.setIcon(new ImageIcon(new ImageIcon("src/main/resources/mal-logo.png").getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH)));
        confirmPanel.setVisible(false);

        app.panelBot.revalidate();
        app.panelBot.repaint();
    }

    //Sets users data
    public void setUserData(JSONObject profileData) {
        this.userData = profileData;
        Object imgPath = userData.getJSONObject("images").getJSONObject("jpg").get("image_url");

        if(imgPath.equals(null)) {
            userPFP.setIcon(new ImageIcon(new ImageIcon("src/main/resources/no-img.png").getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH)));
            confirmUserText.setText("Is this your profile picture?");
            displayConfirmation();
        } else {
            setUserImage((String)imgPath);
        }

    }

    public void displayConfirmation() {
        this.confirmPanel.setVisible(true);
    }

    // Changes Image to User's MAL Profile Picture
    public void setUserImage(String path) {
        try {
            URL url = new URL(path);
            BufferedImage image = ImageIO.read(url);
            ImageIcon malUserImage = new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH));
            userPFP.setIcon(malUserImage);
            confirmUserText.setText("Is this your profile picture?");
            displayConfirmation();
        } catch (Exception e) {
            e.printStackTrace();
        }

        app.panelBot.revalidate();
        app.panelBot.repaint();
    }

    public String getUserName() {
        return username.getText();
    }

    // Changes Screen to Allow user to enter username
    private void MALUserScreen() {
        // Change Screen Being Displayed

        // TODO maybe change the layout for the login screen
        app.panelBot.setLayout(new GridLayout(4,1));
        app.title.setText("Continue with MAL Username");

        // Create back button
        JPanel backButtonPanel = new JPanel(new GridBagLayout());
        JButton backButton = new JButton("Back");
        backButton.setFont(app.headingFont);
        backButton.addActionListener(new BackButtonListener(app, 0));
        backButtonPanel.add(backButton);

        // Panel for username input s
        JPanel usernamePanel = getUserPanel();

        // Panel for confirming user profile
        JPanel userProfilePanel = new JPanel();
        userProfilePanel.setLayout(new BoxLayout(userProfilePanel, BoxLayout.PAGE_AXIS));

        confirmUserText = new JLabel();
        confirmUserText.setFont(app.headingFont);
        confirmUserText.setText("Your Profile Picture will appear below");

        ImageIcon malUserImage = new ImageIcon(new ImageIcon("src/main/resources/mal-logo.png").getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH));
        userPFP = new JLabel();
        userPFP.setIcon(malUserImage);

        userProfilePanel.add(confirmUserText);
        userProfilePanel.add(userPFP);

        // Panel for confirm buttons
        confirmPanel = new JPanel();

        JButton yesButton = new JButton("Yes");
        yesButton.addActionListener(new ConfirmUser(app, this));
        JButton noButton = new JButton("No");
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterUserName();
            }
        });
        yesButton.setFont(app.headingFont);
        noButton.setFont(app.headingFont);

        confirmPanel.add(yesButton);
        confirmPanel.add(noButton);

        // Add to main panel
        app.panelBot.add(usernamePanel);
        app.panelBot.add(userProfilePanel);
        app.panelBot.add(confirmPanel);
        app.panelBot.add(backButtonPanel);

        confirmPanel.setVisible(false);

        confirmUserText.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPFP.setAlignmentX(Component.CENTER_ALIGNMENT);

        app.panelBot.revalidate();
        app.panelBot.repaint();
    }

    private JPanel getUserPanel() {
        JPanel usernamePanel = new JPanel(new GridBagLayout());
        username = new JTextField("Enter MAL Username", 16);
        username.setFont(app.headingFont);
        username.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if(username.getText().equals("Enter MAL Username")) {
                    username.setText("");
                }
            }
        });

        JButton submitUser = new JButton("Submit");
        submitUser.addActionListener(new submitMALUser(this));
        submitUser.setFont(app.headingFont);

        usernamePanel.add(username);
        usernamePanel.add(submitUser);
        return usernamePanel;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // Clear panel and set title
        app.panelBot.removeAll();
        MALUserScreen();
    }
}
