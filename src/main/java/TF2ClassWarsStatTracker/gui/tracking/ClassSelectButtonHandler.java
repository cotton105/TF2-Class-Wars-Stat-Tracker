package TF2ClassWarsStatTracker.gui.tracking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClassSelectButtonHandler implements ActionListener {
    private final int team, mercenary;

    ClassSelectButtonHandler(int team, int mercenary) {
        this.team = team;
        this.mercenary = mercenary;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Tracking.setSelectedMercenary(team, mercenary);
        Tracking.refreshGamesPlayedLabels();
        Tracking.refreshGrid();
    }
}
