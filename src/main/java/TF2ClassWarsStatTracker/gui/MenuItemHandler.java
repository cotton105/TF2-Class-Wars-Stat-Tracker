package TF2ClassWarsStatTracker.gui;

import TF2ClassWarsStatTracker.AppDataHandler;
import TF2ClassWarsStatTracker.Start;
import TF2ClassWarsStatTracker.exceptions.ActionHistoryEmptyException;
import TF2ClassWarsStatTracker.exceptions.InvalidMapNameException;
import TF2ClassWarsStatTracker.exceptions.MapAlreadyExistsException;
import TF2ClassWarsStatTracker.exceptions.MapNotFoundException;
import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.gui.tracking.Tracking;
import TF2ClassWarsStatTracker.util.FileHandler;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static TF2ClassWarsStatTracker.AppDataHandler.updateActionHistory;

public class MenuItemHandler implements ActionListener {
    public static final int
            EXIT = 0, OPTIONS = 1, UNDO = 2, NEW_MAP = 3, IMPORT_MAPS_DATA = 4;
    private final int action;

    public MenuItemHandler(int action) {
        this.action = action;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (action) {
            case EXIT -> Start.exit();
            case OPTIONS -> Print.print("Feature not implemented");
            case UNDO -> undo();
            case NEW_MAP -> addMap();
            case IMPORT_MAPS_DATA -> importMaps();
        }
    }

    private void addMap() {
        String mapName = JOptionPane.showInputDialog(Start.getFrame(), "Enter the new map name");
        try {
            if (GameMap.validMapName(mapName)) {
                AppDataHandler.addMap(mapName);
                FileHandler.writeToJSONFile(AppDataHandler.getMaps(), FileHandler.MAPS_JSON);
                updateActionHistory(String.format("%s-%s", AppDataHandler.NEW_MAP, mapName));
                Tracking.setSelectedMap(mapName);
                Tracking.refreshMapList();
            } else if (mapName != null) {
                throw new InvalidMapNameException(mapName);
            }
        } catch (MapAlreadyExistsException | InvalidMapNameException ex) {
            JOptionPane.showMessageDialog(Start.getFrame(), ex.getMessage(), "Map add failure", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void undo() {
        try {
            AppDataHandler.undoLastAction();
            FileHandler.writeToJSONFile(AppDataHandler.getMaps(), FileHandler.MAPS_JSON);
            Tracking.refreshAll();
        } catch (MapNotFoundException ex) {
            JOptionPane.showMessageDialog(Start.getFrame(), ex.getMessage(), "Undo failure", JOptionPane.ERROR_MESSAGE);
            Print.error(ex.getMessage());
        } catch (ActionHistoryEmptyException ex) {
            Print.error(ex.getMessage());
        }
    }

    private void importMaps() {
        Print.error("Feature not implemented");
    }
}
