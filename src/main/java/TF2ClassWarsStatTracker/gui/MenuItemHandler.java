package TF2ClassWarsStatTracker.gui;

import TF2ClassWarsStatTracker.AppDataHandler;
import TF2ClassWarsStatTracker.Start;
import TF2ClassWarsStatTracker.exceptions.*;
import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.gui.tracking.Tracking;
import TF2ClassWarsStatTracker.util.FileHandler;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static TF2ClassWarsStatTracker.AppDataHandler.updateActionHistory;

public class MenuItemHandler implements ActionListener {
    public static final int
            EXIT = 0, OPTIONS = 1, UNDO = 2, NEW_SERVER = 3, NEW_MAP = 4, RENAME_MAP = 5, IMPORT_MAPS_DATA = 6;
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
            case NEW_SERVER -> addServer();
            case NEW_MAP -> addMap();
            case RENAME_MAP -> renameMap();
            case IMPORT_MAPS_DATA -> importMaps();
        }
    }

    private void undo() {
        try {
            AppDataHandler.undoLastAction();
            FileHandler.writeToJSONFile(AppDataHandler.getMaps(), FileHandler.DEFAULT_MAPS_JSON);
            Tracking.refreshAll();
        } catch (MapNotFoundException ex) {
            JOptionPane.showMessageDialog(Start.getFrame(), ex.getMessage(), "Undo failure", JOptionPane.ERROR_MESSAGE);
            Print.error(ex.getMessage());
        } catch (ActionHistoryEmptyException ex) {
            Print.error(ex.getMessage());
        }
    }

    private void addServer() {
        Object[] newServerComponents = getUserInputDialogBoxComponents();
        JPanel panServerName = (JPanel)newServerComponents[0];
        JTextField textNewServerName = (JTextField)newServerComponents[1];

        int chooseMapResult = JOptionPane.showConfirmDialog(
                Start.getFrame(), panServerName, "Add new server",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (chooseMapResult == JOptionPane.CANCEL_OPTION) {
            showCancelledDialog();
        }
        else if (chooseMapResult == JOptionPane.OK_OPTION) {
            String newServerName = textNewServerName.getText();
            if (newServerName != null) {
                try {
                    FileHandler.newServerJsonFile(newServerName);
                } catch (InvalidFileNameException ex) {
                    Print.error(ex.getMessage());
                    JOptionPane.showMessageDialog(
                            Start.getFrame(), ex.getMessage(), "Server add failure", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                showCancelledDialog();
            }
        }
    }

    private void addMap() {
        String mapName = JOptionPane.showInputDialog(
                Start.getFrame(), "Enter the new map name", "New map", JOptionPane.QUESTION_MESSAGE);
        try {
            if (GameMap.validMapName(mapName)) {
                AppDataHandler.addMap(mapName);
                FileHandler.writeToJSONFile(AppDataHandler.getMaps(), FileHandler.DEFAULT_MAPS_JSON);
                updateActionHistory(String.format("%s-%s", AppDataHandler.NEW_MAP, mapName));
                Tracking.setSelectedMap(mapName);
                Tracking.refreshMapList();
            } else if (mapName != null) {
                throw new InvalidMapNameException(mapName);
            }
        } catch (MapAlreadyExistsException | InvalidMapNameException ex) {
            JOptionPane.showMessageDialog(
                    Start.getFrame(), ex.getMessage(), "Map add failure", JOptionPane.ERROR_MESSAGE);
        }
    }

    // TODO: Migrate this functionality to new class, as it is messy here in the Handler
    private void renameMap() {
        Object[] chooseMapComponents = getChooseMapComponents();
        JPanel panChooseMap = (JPanel)chooseMapComponents[0];
        MapDropDownRenameHandler mapDropDownRenameHandler = (MapDropDownRenameHandler)chooseMapComponents[1];
        int chooseMapResult = JOptionPane.showConfirmDialog(
                Start.getFrame(), panChooseMap, "Rename map",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (chooseMapResult == JOptionPane.CANCEL_OPTION) {
            showCancelledDialog();
        }
        else if (chooseMapResult == JOptionPane.OK_OPTION) {
            String selectedMap = mapDropDownRenameHandler.getSelected();
            if (selectedMap != null) {
                Object[] renameMapComponents = getUserInputDialogBoxComponents();
                JPanel panNewName = (JPanel)renameMapComponents[0];
                JTextField fieldNewName = (JTextField)renameMapComponents[1];
                int renameMapResult = JOptionPane.showConfirmDialog(
                        Start.getFrame(), panNewName, selectedMap,
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (renameMapResult == JOptionPane.CANCEL_OPTION) {
                    showCancelledDialog();
                }
                else if (renameMapResult == JOptionPane.OK_OPTION) {
                    try {
                        String newName = fieldNewName.getText();
                        AppDataHandler.renameMap(selectedMap, newName);
                        FileHandler.writeToJSONFile(AppDataHandler.getMaps(), FileHandler.DEFAULT_MAPS_JSON);
                        Tracking.refreshMapList();
                        Tracking.setSelectedMap(newName);
                        JOptionPane.showMessageDialog(
                                Start.getFrame(),
                                String.format("Successfully renamed map \"%s\" to \"%s\"", selectedMap, newName),
                                "Successful map rename", JOptionPane.INFORMATION_MESSAGE);
                    } catch (InvalidMapNameException | MapNotFoundException ex) {
                        Print.error(ex.getMessage());
                        JOptionPane.showMessageDialog(
                                Start.getFrame(), ex.getMessage(), "Map rename failure", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                showCancelledDialog();
            }
        }
    }

    private void showCancelledDialog() {
        JOptionPane.showMessageDialog(
                Start.getFrame(), "Action cancelled", "Action cancelled", JOptionPane.INFORMATION_MESSAGE);
    }

    private Object[] getChooseMapComponents() {
        JPanel panChooseMap = new JPanel(new GridLayout(2, 1));

        JLabel labChooseMap = new JLabel("Choose the map to rename");

        JComboBox<String> mapDropDownSelect = new JComboBox<>();
        mapDropDownSelect.addItem(null);
        for (String mapName : AppDataHandler.getMapNames())
            mapDropDownSelect.addItem(mapName);
        MapDropDownRenameHandler mapDropDownRenameHandler = new MapDropDownRenameHandler();
        mapDropDownSelect.addItemListener(mapDropDownRenameHandler);

        panChooseMap.add(labChooseMap);
        panChooseMap.add(mapDropDownSelect);

        TrackingGUIJPanel.setDefaultFont(panChooseMap, TrackingGUIJPanel.TF2secondary.deriveFont(16f));

        return new Object[] {panChooseMap, mapDropDownRenameHandler};
    }

    /**
     * Gets a tuple of the required components for choosing a new name for the chosen map.
     * @return (1) The {@code JPanel} containing {@code JLabel} & {@code JTextField}, and (2) the {@code JTextField}
     */
    private Object[] getUserInputDialogBoxComponents() {
        JPanel panNewName = new JPanel(new GridLayout(2, 1));

        JLabel labNewName = new JLabel("Enter new name");

        JTextField fieldNewName = new JTextField();

        panNewName.add(labNewName);
        panNewName.add(fieldNewName);

        TrackingGUIJPanel.setDefaultFont(panNewName, TrackingGUIJPanel.TF2secondary.deriveFont(16f));

        return new Object[] {panNewName, fieldNewName};
    }

    private void importMaps() {
        Print.error("Feature not implemented");
    }
}
