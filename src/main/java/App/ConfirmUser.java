package App;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfirmUser implements ActionListener {
    AppGUI app;
    MALUser user;
    public ConfirmUser(AppGUI app, MALUser user) {
        this.app = app;
        this.user = user;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        app.panelBot.removeAll();
        this.app.recommendationOptionsScreen(user);
    }
}
