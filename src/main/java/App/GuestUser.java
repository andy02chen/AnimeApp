package App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GuestUser implements ActionListener {
    AppGUI app;
    public GuestUser(AppGUI app) {
        this.app = app;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        app.panelBot.removeAll();
        app.title.setText("No Account");

        JLabel lmaoMsg = new JLabel("Go make MAL account and use it. I cba cause no one is gonna see this anyways.");
        lmaoMsg.setFont(app.headingFont);
        lmaoMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel link = new JLabel("https://myanimelist.net/register.php?from=%2F");
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

        link.setAlignmentX(Component.CENTER_ALIGNMENT);
        app.panelBot.add(link);

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(app.headingFont);
        backButton.addActionListener(new BackButtonListener(app, 0));

        app.panelBot.add(lmaoMsg);
        app.panelBot.add(backButton);
        app.panelBot.revalidate();
        app.panelBot.repaint();
    }
}
