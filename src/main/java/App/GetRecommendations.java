package App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GetRecommendations implements ActionListener {
    AppGUI app;
    int selection;
    public GetRecommendations(AppGUI app, int optionSelection) {
        this.app = app;
        this.selection = optionSelection;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        app.panelBot.removeAll();

        switch(selection) {
            case 0:
                app.title.setText("Favourites");
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
        backButton.addActionListener(new BackButtonListener(app, 1));
        backButtonPanel.add(backButton);

        app.panelBot.add(backButtonPanel);

        app.panelBot.revalidate();
        app.panelBot.repaint();
    }
}
