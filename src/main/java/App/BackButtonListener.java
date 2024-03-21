package App;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BackButtonListener implements ActionListener {
    AppGUI app;
    int selection;
    public BackButtonListener(AppGUI app, int selection) {
        this.app = app;
        this.selection = selection;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(selection) {
            case 0:
                this.app.homeMenuScreen();
                break;

            case 1:
                this.app.recommendationOptionsScreen();
                break;
        }

    }
}
