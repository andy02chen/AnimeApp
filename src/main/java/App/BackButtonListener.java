package App;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BackButtonListener implements ActionListener {
    AppGUI app;
    int selection;
    MALUser user;
    public BackButtonListener(AppGUI app, int selection) {
        this.app = app;
        this.selection = selection;
    }

    public BackButtonListener(AppGUI app, int selection, MALUser user) {
        this.app = app;
        this.selection = selection;
        this.user = user;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(selection) {
            case 0:
                this.app.homeMenuScreen();
                break;

            case 1:
                this.app.recommendationOptionsScreen(user);
                break;
        }

    }
}
