package TF2ClassWarsStatTracker.gui.mainmenu;

import TF2ClassWarsStatTracker.StartGUI;
import TF2ClassWarsStatTracker.util.Print;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuButtonHandler implements ActionListener {
    public static final int EXIT = 0, TRACKER = 1, VIEW_STATS = 2;
    private final int action;

    MainMenuButtonHandler(int action) {
        this.action = action;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (action) {
            case EXIT:
                StartGUI.exit();
                break;
            case TRACKER:
                StartGUI.setActiveContentPane(StartGUI.getTrackerScreen());
                break;
            case VIEW_STATS:
                Print.timestamp("Feature not implemented");
                break;
            default:
                break;
        }
    }
}
