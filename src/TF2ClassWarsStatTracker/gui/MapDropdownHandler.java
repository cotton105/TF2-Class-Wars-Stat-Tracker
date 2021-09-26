package TF2ClassWarsStatTracker.gui;

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
        int state = e.getStateChange();
        System.out.println((state == ItemEvent.SELECTED) ? "Selected" : "Deselected");
        System.out.println("Item: " + e.getItem());
        ItemSelectable is = e.getItemSelectable();
        System.out.println("Selected: " + selectedString(is));
        Tracking.setSelectedMap(selectedString(is));
    }
}
