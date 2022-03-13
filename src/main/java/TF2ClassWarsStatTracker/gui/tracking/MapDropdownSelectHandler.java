package TF2ClassWarsStatTracker.gui.tracking;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MapDropdownSelectHandler implements ItemListener {
    private static boolean mapBeingAdded;

    MapDropdownSelectHandler() {
        mapBeingAdded = false;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            ItemSelectable is = e.getItemSelectable();
            TrackerWindow.instance.setSelectedMap(selectedString(is));
            TrackerWindow.instance.setDisplayOverallMap(false);
            TrackerWindow.instance.refreshGamesPlayedLabels();
        }
    }

    static private String selectedString(ItemSelectable is) {
        Object[] selected = is.getSelectedObjects();
        return ((selected.length == 0) ? "null" : (String) selected[0]);
    }

    public static void setMapBeingAdded(boolean mapBeingAdded) {
        MapDropdownSelectHandler.mapBeingAdded = mapBeingAdded;
    }
}
