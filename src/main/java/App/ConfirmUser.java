package App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfirmUser implements ActionListener {
    AppGUI app;
    public ConfirmUser(AppGUI app) {
        this.app = app;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        app.panelBot.removeAll();
        this.app.recommendationOptionsScreen();
    }
}
