package App;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// TODO work on after MALUser

public class GuestUser implements ActionListener {
    AppGUI app;
    public GuestUser(AppGUI app) {
        this.app = app;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        app.panelBot.removeAll();
        app.title.setText("No Account");

        JButton backButton = new JButton("Back");
        backButton.setFont(app.headingFont);
        backButton.addActionListener(new BackButtonListener(app, 0));
        app.panelBot.add(backButton);

        app.panelBot.revalidate();
        app.panelBot.repaint();
    }
}
