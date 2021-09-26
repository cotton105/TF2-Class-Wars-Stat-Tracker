package TF2ClassWarsStatTracker.gui.tracking;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MapDropdownHandler implements ItemListener {
    MapDropdownHandler() {}

    static private String selectedString(ItemSelectable is) {
        Object[] selected = is.getSelectedObjects();
        return ((selected.length == 0) ? "null" : (String) selected[0]);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        ItemSelectable is = e.getItemSelectable();
        Tracking.setSelectedMap(selectedString(is));
    }
}
