package TF2ClassWarsStatTracker.gui;

import TF2ClassWarsStatTracker.Start;
import TF2ClassWarsStatTracker.util.Print;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuItemHandler implements ActionListener {
    public static final int
            EXIT = 0, OPTIONS = 1, NEW_MAP = 2, IMPORT_MAPS_DATA = 3;
    private final int action;

    public MenuItemHandler(int action) {
        this.action = action;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (action) {
            case EXIT -> Start.exit();
            case OPTIONS -> Print.print("Feature not implemented");
            case NEW_MAP -> Print.print("Feature not implemented");
            case IMPORT_MAPS_DATA -> Print.print("Feature not implemented");
        }
    }
}
