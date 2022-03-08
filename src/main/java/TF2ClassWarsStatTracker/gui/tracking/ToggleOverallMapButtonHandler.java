package TF2ClassWarsStatTracker.gui.tracking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToggleOverallMapButtonHandler implements ActionListener {
    public static final int VIEW_OVERALL = 0, VIEW_MAP = 1;
    private static boolean displayOverall;

    public ToggleOverallMapButtonHandler(int startingState) throws Exception {
        if (startingState == VIEW_OVERALL) displayOverall = true;
        else if (startingState == VIEW_MAP) displayOverall = false;
        else throw new Exception("Invalid starting state.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        displayOverall = !displayOverall;
        TrackerWindow.instance.setDisplayOverallMap(displayOverall);
        TrackerWindow.instance.refreshBiasGrid();
    }

    public static void setDisplayOverall(boolean displayOverall) {
        ToggleOverallMapButtonHandler.displayOverall = displayOverall;
    }
}
