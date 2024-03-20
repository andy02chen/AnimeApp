package App;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BackButtonListener implements ActionListener {
    AppGUI app;
    public BackButtonListener(AppGUI app) {
        this.app = app;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.app.homeMenuScreen();
    }
}
