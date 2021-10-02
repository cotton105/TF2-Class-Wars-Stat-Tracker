package TF2ClassWarsStatTracker.gui.mainmenu;

import TF2ClassWarsStatTracker.Start;
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
            case EXIT -> Start.exit();
            case TRACKER -> Start.setActiveContentPane(Start.getTrackerScreen());
            case VIEW_STATS -> Print.print("Feature not implemented");
            default -> {
            }
        }
    }
}
