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
        final int height = 720;
        final int width = 1280;
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
