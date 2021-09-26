package TF2ClassWarsStatTracker.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClassSelectButtonHandler implements ActionListener {
    private final int action, team;

    ClassSelectButtonHandler(int team, int action) {
        this.action = action;
        this.team = team;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Tracking.setSelectedMercenary(team, action);
//        Tracking.reloadGrid();
    }
}
