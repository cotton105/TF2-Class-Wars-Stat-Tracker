package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.StartGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBarButtonHandler implements ActionListener {
    public static final int EXIT = 0, MENU = 1, OVERALL = 2;
    private final int action;

    MenuBarButtonHandler(int action) {
        this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (action) {
            case EXIT:
                StartGUI.exit();
                break;
            case MENU:
                StartGUI.setActiveContentPane(StartGUI.getMainMenu());
                break;
            case OVERALL:
                Tracking.viewOverall();
                break;
        }
    }
}
