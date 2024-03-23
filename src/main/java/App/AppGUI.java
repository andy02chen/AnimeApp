package App;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AppGUI extends JFrame {

    public JPanel panelTop;
    public JPanel panelBot;
    public JLabel title;
    public Font headingFont;
    public Font titleFont;
    public Font textFont;
    private String username;

    public AppGUI() {
        super("Anime Recommender App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        final int height = 1080;
        final int width = 1980;
        final float titleFontSize = 60f;
        final float headingFontSize = 20f;
        setSize(width, height);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        //Top and Bottom Panels
        panelTop = new JPanel();
        panelTop.setSize(width, height);
        panelBot = new JPanel();
        panelBot.setSize(width, height);
        title = new JLabel();
        panelTop.add(title);

        try {
            titleFont = Font.createFont(Font.TRUETYPE_FONT, new File("src\\main\\resources\\QueensidesMedium-x30zV.ttf")).deriveFont(titleFontSize);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(titleFont);

            headingFont = Font.createFont(Font.TRUETYPE_FONT, new File("src\\main\\resources\\MontserratRegular-BWBEl.ttf")).deriveFont(headingFontSize);
            GraphicsEnvironment ge2 = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge2.registerFont(headingFont);

        } catch(IOException | FontFormatException e) {
            titleFont = new Font("TimesRoman", Font.PLAIN, (int)titleFontSize);
            headingFont = new Font("SansSerif",Font.PLAIN, (int)headingFontSize);
        }

        textFont = new Font("", Font.PLAIN, 20);

        // Display Menu
        homeMenuScreen();
    }

    public void recommendationOptionsScreen(MALUser user) {
        panelBot.removeAll();

        title.setText("Recommendations");
        panelBot.setLayout(new BoxLayout(panelBot, BoxLayout.PAGE_AXIS));

        GridBagConstraints gbc = new GridBagConstraints();
        JLabel text = new JLabel("How would you like the recommendations to be generated?");
        text.setFont(headingFont);

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        JPanel p5 = new JPanel();

        JButton fromFavs = new JButton("Favourites");
        fromFavs.addActionListener(new GetRecommendations(this, 0, user));

        JButton fromPlanToWatch = new JButton("Plan to Watch");
        fromPlanToWatch.addActionListener(new GetRecommendations(this, 1, user));

        JButton fromCompleted = new JButton("Completed");
        fromCompleted.addActionListener(new GetRecommendations(this, 2, user));

        JButton fromWatching = new JButton("Currently Watching");
        fromWatching.addActionListener(new GetRecommendations(this, 3, user));

        JButton random = new JButton("Random");
        random.addActionListener(new GetRecommendations(this, 4, user));

        Dimension buttonSize = new Dimension(300,50);
        fromFavs.setPreferredSize(buttonSize);
        fromPlanToWatch.setPreferredSize(buttonSize);
        fromCompleted.setPreferredSize(buttonSize);
        fromWatching.setPreferredSize(buttonSize);
        random.setPreferredSize(buttonSize);

        fromFavs.setFont(headingFont);
        fromPlanToWatch.setFont(headingFont);
        fromCompleted.setFont(headingFont);
        fromWatching.setFont(headingFont);
        random.setFont(headingFont);

        panelBot.add(text);
        panelBot.add(p1);
        panelBot.add(p2);
        panelBot.add(p3);
        panelBot.add(p4);
        panelBot.add(p5);

        p1.add(fromFavs);
        p2.add(fromPlanToWatch);
        p3.add(fromCompleted);
        p4.add(fromWatching);
        p5.add(random);

        text.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelBot.revalidate();
        panelBot.repaint();
    }

    // For Display Home Menu
    public void homeMenuScreen() {
        // Clear the bottom panel
        panelBot.removeAll();
        panelBot.setLayout(new BoxLayout(panelBot, BoxLayout.PAGE_AXIS));

        // Title Text
        title.setText("My Anime Recommender");
        title.setFont(titleFont);

        JLabel homeScreenTip = new JLabel("I recommend entering MyAnimeList username for a better experience");
        homeScreenTip.setFont(textFont);

        // Login and Register Buttons
        JButton malUserButton = new JButton("Continue with MAL Username");
        ImageIcon malUserImage = new ImageIcon(new ImageIcon("src/main/resources/mal-logo.png").getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH));
        JLabel malUser = new JLabel();
        malUser.setIcon(malUserImage);
        malUserButton.addActionListener(new MALUser(this));
        malUserButton.setFont(headingFont);

        JButton guestUserButton = new JButton("I don't have a MAL Acc or don't use it");
        ImageIcon guestUserImage = new ImageIcon(new ImageIcon("src/main/resources/anon-icon.png").getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH));
        JLabel guestUser = new JLabel();
        guestUser.setIcon(guestUserImage);
        guestUserButton.addActionListener(new GuestUser(this));
        guestUserButton.setFont(headingFont);

        // Add to panel for display
        homeScreenTip.setAlignmentX(Component.CENTER_ALIGNMENT);
        malUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        guestUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        malUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        guestUser.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelBot.add(homeScreenTip);
        panelBot.add(Box.createVerticalStrut(20));
        panelBot.add(malUser);
        panelBot.add(malUserButton);
        panelBot.add(Box.createVerticalStrut(20));
        panelBot.add(guestUser);
        panelBot.add(guestUserButton);

        add(panelBot, BorderLayout.CENTER);
        add(panelTop, BorderLayout.NORTH);

        panelBot.revalidate();
        panelBot.repaint();
    }
}
