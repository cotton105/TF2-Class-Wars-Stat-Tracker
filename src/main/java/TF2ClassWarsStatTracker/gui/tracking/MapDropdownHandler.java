package TF2ClassWarsStatTracker.gui.tracking;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MapDropdownHandler implements ItemListener {
    private static boolean mapBeingAdded;

    MapDropdownHandler() {
        mapBeingAdded = false;
    }

    static private String selectedString(ItemSelectable is) {
        Object[] selected = is.getSelectedObjects();
        return ((selected.length == 0) ? "null" : (String) selected[0]);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (!mapBeingAdded) {
            ItemSelectable is = e.getItemSelectable();
            Tracking.setSelectedMap(selectedString(is));
            Tracking.refreshGamesPlayedLabels();
        }
    }

    public static void setMapBeingAdded(boolean mapBeingAdded) {
        MapDropdownHandler.mapBeingAdded = mapBeingAdded;
    }
}
