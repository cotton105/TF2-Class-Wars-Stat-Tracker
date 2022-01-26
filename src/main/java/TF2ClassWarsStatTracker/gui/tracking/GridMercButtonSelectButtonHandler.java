package TF2ClassWarsStatTracker.gui.tracking;

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
        TrackerWindow.instance.setSelectedMercenaries(blu, red);
        TrackerWindow.instance.refreshGamesPlayedLabels();
        TrackerWindow.instance.refreshBiasGrid();
    }
}
