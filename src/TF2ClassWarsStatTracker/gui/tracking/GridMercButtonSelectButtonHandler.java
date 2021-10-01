package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.util.Constants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GridMercButtonSelectButtonHandler implements ActionListener {
    private final int blu, red;

    GridMercButtonSelectButtonHandler(int blu, int red) {
        this.blu = blu;
        this.red = red;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Tracking.setSelectedMercenary(Constants.BLU, blu);
        Tracking.setSelectedMercenary(Constants.RED, red);
        Tracking.updateMatchupWinLabels();
        Tracking.reloadGrid();
    }
}
