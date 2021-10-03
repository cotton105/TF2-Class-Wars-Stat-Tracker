package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.Start;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GeneralButtonHandler implements ActionListener {
    public static final int BACK = 0, OVERALL = 1;
    private final int action;

    GeneralButtonHandler(int action) {
        this.action = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (action) {
            case BACK -> Start.setActiveContentPane(Start.getMainMenu());
            case OVERALL -> Tracking.viewOverall();
        }
    }
}
