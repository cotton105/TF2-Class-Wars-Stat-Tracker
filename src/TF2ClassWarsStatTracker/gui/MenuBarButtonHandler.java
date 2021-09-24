package TF2ClassWarsStatTracker.gui;

import TF2ClassWarsStatTracker.Start;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBarButtonHandler implements ActionListener {
    public static final int EXIT = 0, MENU = 1;
    private final int action;

    MenuBarButtonHandler(int action) {
        this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (action) {
            case EXIT:
                Start.exit();
                break;
            case MENU:
                Start.setActiveContentPane(Start.getMainMenu());
        }
    }
}
