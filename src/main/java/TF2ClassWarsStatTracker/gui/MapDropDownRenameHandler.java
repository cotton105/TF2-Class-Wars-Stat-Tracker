package TF2ClassWarsStatTracker.gui;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MapDropDownRenameHandler implements ItemListener {
    private String selected;

    MapDropDownRenameHandler() {
        selected = null;
    }

    static private String selectedString(ItemSelectable is) {
        Object[] selected = is.getSelectedObjects();
        return ((selected.length == 0) ? "null" : (String) selected[0]);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        ItemSelectable is = e.getItemSelectable();
        selected = selectedString(is);
    }

    public String getSelected() {
        return selected;
    }
}
