package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.AppDataHandler;
import TF2ClassWarsStatTracker.exceptions.InvalidTeamNumberException;
import TF2ClassWarsStatTracker.game.ConfigurationGrid;
import TF2ClassWarsStatTracker.util.Calculate;
import TF2ClassWarsStatTracker.util.DBHandler;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.*;

public class StatWindow {
    private JPanel panBiasGrid;
    private JPanel panBluClassSelect;
    private JButton butBluWin, butRedWin;
    private JPanel panBluClassSelectGrid;
    private JPanel panRedClassSelect;
    private JComboBox comboMapSelect;
    private JButton butToggleOverallMap;
    private JPanel panMain;
    private JPanel panLeft;
    private JPanel panRight;
    private JPanel panGridHeader;
    private JPanel panGameModeSelect;
    private JPanel panMapSelect;
    private JPanel panRedClassSelectGrid;
    private JPanel panBiasGridColumnHeaders;
    private JPanel panBiasGridRowHeaders;
    private JPanel panBiasGridContent;
    private JLabel labRedGamesWon;
    private JLabel labBluGamesWon;
    private JComboBox comboGameModeSelect;
    private JButton butToggleOverallGameMode;
    private JLabel labTotalGamesPlayed;
    private final int[] selectedMercenary = new int[] {-1, -1};
    private final int[] highlightedMatchup = new int[] {-1, -1};
    private final JPanel[] mercenarySelectPanels = new JPanel[] {panBluClassSelectGrid, panRedClassSelectGrid};
    private final JButton[] winButtons = new JButton[] {butBluWin, butRedWin};
    private boolean viewMapsCombined = true;
    private boolean viewGameModesCombined = true;
    private int selectedTeam;
    private int selectedStageNumber = 1;
    private String selectedMap;
    private String selectedGameMode = NORMAL_GAME_MODE;

    public StatWindow() {
        // Add action listeners to class-select grid
        for (int team = 0; team <= 1; team++) {
            for (int mercenaryIndex = 0; mercenaryIndex < MERCENARY.length; mercenaryIndex++) {
                JButton but = (JButton) mercenarySelectPanels[team].getComponent(mercenaryIndex);
                but.addActionListener(new ClassSelectButtonHandler(team, mercenaryIndex));
            }
        }

        // Add action listeners to bias grid
        for (int redMercenaryIndex = 0; redMercenaryIndex < MERCENARY.length; redMercenaryIndex++) {
            for (int bluMercenaryIndex = 0; bluMercenaryIndex < MERCENARY.length; bluMercenaryIndex++) {
                JButton but = (JButton) panBiasGridContent.getComponent(bluMercenaryIndex + (redMercenaryIndex * MERCENARY.length));
                but.addActionListener(new BiasGridButtonHandler(bluMercenaryIndex, redMercenaryIndex));
            }
        }

        // Set bias grid borders
        for (Component c : panBiasGrid.getComponents()) {
            if (c instanceof JComponent) {
                ((JComponent) c).setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            }
        }

        comboMapSelect.addItemListener(new MapDropdownSelectHandler());
        comboGameModeSelect.addItemListener(new GameModeDropdownSelectHandler());
        butToggleOverallMap.addActionListener(new ToggleOverallMapButtonHandler());
        butToggleOverallGameMode.addActionListener(new ToggleOverallGameModeButtonHandler());
        refreshMapList();
        refreshGameModeList();
        refreshBiasGrid();
    }

    public JPanel getPanMain() {
        return panMain;
    }

    private void refreshMapList() {
        try {
            MapDropdownSelectHandler handler = (MapDropdownSelectHandler) comboMapSelect.getItemListeners()[0];
            handler.setIgnoreStateChanges(true);
            comboMapSelect.removeAllItems();
            List<String> mapNames = DBHandler.Retrieve.getMapNames();
            for (Object map : mapNames.toArray())
                comboMapSelect.addItem(map);
            handler.setIgnoreStateChanges(false);
            comboMapSelect.setSelectedItem(selectedMap);
        }
        catch (SQLException ex) {
            Print.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void refreshGameModeList() {
        try {
            GameModeDropdownSelectHandler handler = (GameModeDropdownSelectHandler) comboGameModeSelect.getItemListeners()[0];
            handler.setIgnoreStateChanges(true);
            comboGameModeSelect.removeAllItems();
            List<String> gameModeNames = DBHandler.Retrieve.getGameModeNames();
            for (Object gameMode : gameModeNames.toArray())
                comboGameModeSelect.addItem(gameMode);
            handler.setIgnoreStateChanges(false);
            comboGameModeSelect.setSelectedItem(selectedGameMode);
        }
        catch (SQLException ex) {
            Print.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void setSelectedMercenary(int team, int mercenary) throws InvalidTeamNumberException {
        if (team != BLU && team != RED) throw new InvalidTeamNumberException();
        selectedMercenary[team] = mercenary;
        refreshMercenarySelectGrids();
    }

    private void fillBiasGrid(ConfigurationGrid grid) {
        for (int redMercenaryIndex = 0; redMercenaryIndex < MERCENARY.length; redMercenaryIndex++) {
            for (int bluMercenaryIndex = 0; bluMercenaryIndex < MERCENARY.length; bluMercenaryIndex++) {
                int[] matchupScores = grid.getMatchupWins(bluMercenaryIndex, redMercenaryIndex);
                JButton but = (JButton) panBiasGridContent.getComponent(bluMercenaryIndex + (redMercenaryIndex * MERCENARY.length));
                float biasRatio = Calculate.getRatioBias(matchupScores[0], matchupScores[1]);
                String buttonStr = "";
                Color buttonColor = Color.WHITE;
                if (!Float.isNaN(biasRatio)) {
                    buttonStr = String.format("%.2f", biasRatio);
                    buttonColor = Calculate.getColourScaledFromWhite(biasRatio, BLU_COLOUR, RED_COLOUR);
                }
                but.setText(buttonStr);
                but.setBackground(buttonColor);
            }
        }
    }

    private void setWinButtonEnabled(boolean enabled) {
        for (JButton but : winButtons) {
            but.setEnabled(enabled);
            but.setToolTipText(enabled ? null : "Invalid configuration");
        }
    }

    private void setSelectedMercenaries(int bluMercenary, int redMercenary) {
        selectedMercenary[BLU] = bluMercenary;
        selectedMercenary[RED] = redMercenary;
    }

    private void setSelectedMap(String mapName) {
        if (mapName != null){
            viewMapsCombined = false;
            selectedMap = mapName;
            butToggleOverallMap.setText("View Overall");
        }
        else {
            viewMapsCombined = true;
        }
        refreshBiasGrid();
    }

    private void setSelectedGameMode(String gameMode) {
        if (gameMode != null) {
            viewGameModesCombined = false;
            selectedGameMode = gameMode;
            butToggleOverallGameMode.setText("View Overall");
        }
        else {
            viewGameModesCombined = true;
        }
        refreshBiasGrid();
    }

    void setViewMapsCombined(boolean viewMapsCombined) {
        this.viewMapsCombined = viewMapsCombined;
        refreshGamesPlayedLabels();
        refreshMercenarySelectGrids();
//        refreshGamesPlayedLabels();
        butToggleOverallMap.setText(viewMapsCombined ? "View Selected" : "View Overall");
    }

    void setViewGameModesCombined(boolean viewGameModesCombined) {
        this.viewGameModesCombined = viewGameModesCombined;
        refreshGamesPlayedLabels();
        refreshMercenarySelectGrids();
        butToggleOverallGameMode.setText(viewGameModesCombined ? "View Selected" : "View Overall");
    }

    private void highlightSelectedMercenaries() {
        if (highlightedMatchup[BLU] >= 0 || highlightedMatchup[RED] >= 0) {
            JButton currentHighlighted = (JButton) panBiasGridContent.getComponent(highlightedMatchup[BLU] + (highlightedMatchup[RED] * MERCENARY.length));
            float weight = currentHighlighted.getText().equals("") ? 0.0f : Float.parseFloat(currentHighlighted.getText());
            Color originalColor = Calculate.getColourScaledFromWhite(weight, BLU_COLOUR, RED_COLOUR);
            currentHighlighted.setBackground(originalColor);
            currentHighlighted.setBorder(UIManager.getBorder("Button.border"));  // Reset border to default
        }
        JComponent selected = (JComponent) panBiasGridContent.getComponent(selectedMercenary[BLU] + (selectedMercenary[RED] * MERCENARY.length));
        selected.setBackground(Calculate.getColourHighlight(selected.getBackground(), Color.YELLOW));
        selected.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));

        highlightedMatchup[BLU] = selectedMercenary[BLU];
        highlightedMatchup[RED] = selectedMercenary[RED];
    }

    private void refreshMercenarySelectGrids() {
        for (int team = 0; team < mercenarySelectPanels.length; team++) {
            JPanel panel = mercenarySelectPanels[team];
            int mercenaryIndex = 0;
            for (Component component : panel.getComponents()) {
                if (component instanceof JButton) {
                    JButton but = (JButton) component;
                    if (mercenaryIndex != selectedMercenary[team])
                        but.setBackground(Color.WHITE);
                    else {
                        Color highlightColor = team == BLU ? BLU_COLOUR : RED_COLOUR;
                        but.setBackground(Calculate.getColourHighlight(Color.WHITE, highlightColor, 0.5f));
                    }
                    mercenaryIndex++;
                }
            }
        }
    }

    private void refreshBiasGrid() {
        ConfigurationGrid grid;
        try {
            if (viewMapsCombined && viewGameModesCombined) {
                setWinButtonEnabled(false);
                grid = DBHandler.Retrieve.getOverallGrid();
            }
            else if (viewGameModesCombined) {
                setWinButtonEnabled(false);
                grid = DBHandler.Retrieve.getOverallGrid(selectedMap, selectedStageNumber);
            }
            else if (viewMapsCombined) {
                setWinButtonEnabled(false);
                grid = DBHandler.Retrieve.getOverallGameModeGrid(selectedGameMode);
            }
            else {
                if (selectedMercenary[BLU] > -1 && selectedMercenary[RED] != -1)
                    setWinButtonEnabled(true);
                grid = DBHandler.Retrieve.getMatchupGrid(selectedMap, selectedStageNumber, selectedGameMode);
            }
        } catch (SQLException e) {
            Print.error(e.getMessage());
            e.printStackTrace();
            grid = ConfigurationGrid.getEmptyGrid();
        }
        AppDataHandler.setLoadedConfiguration(grid);
        fillBiasGrid(grid);
    }

    private void refreshGamesPlayedLabels() {
        if (0 <= selectedMercenary[BLU] && selectedMercenary[BLU] < 9 && 0 <= selectedMercenary[RED] && selectedMercenary[RED] < 9) {
            refreshGamesWonLabels();
            refreshTotalGamesLabel();
        }
    }

    private void refreshTotalGamesLabel() {
        try {
            int totalGames;
            if (viewMapsCombined && viewGameModesCombined)
                totalGames = DBHandler.Retrieve.getTotalGameCount();
            else if (viewMapsCombined)
                totalGames = DBHandler.Retrieve.getTotalGameCount(selectedGameMode);
            else if (viewGameModesCombined)
                totalGames = DBHandler.Retrieve.getTotalGameCount(selectedMap, selectedStageNumber);
            else
                totalGames = DBHandler.Retrieve.getTotalGameCount(selectedMap, selectedStageNumber, selectedGameMode);
            labTotalGamesPlayed.setText(String.format("Total games recorded in this configuration: %d", totalGames));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    // TODO: not refreshing when new matchup is selected on bias grid
    private void refreshGamesWonLabels() {
        try {
            int[] totalWins;
            if (viewMapsCombined && viewGameModesCombined)
                totalWins = DBHandler.Retrieve.getMatchupWins(MERCENARY[selectedMercenary[BLU]], MERCENARY[selectedMercenary[RED]]);
            else if (viewMapsCombined)
                totalWins = DBHandler.Retrieve.getMatchupWins(MERCENARY[selectedMercenary[BLU]], MERCENARY[selectedMercenary[RED]], selectedGameMode);
            else if (viewGameModesCombined)
                totalWins = DBHandler.Retrieve.getMatchupWins(MERCENARY[selectedMercenary[BLU]], MERCENARY[selectedMercenary[RED]], selectedMap, selectedStageNumber);
            else
                totalWins = DBHandler.Retrieve.getMatchupWins(MERCENARY[selectedMercenary[BLU]], MERCENARY[selectedMercenary[RED]], selectedMap, selectedStageNumber, selectedGameMode);
            labBluGamesWon.setText(String.format("Won: %d", totalWins[BLU]));
            labRedGamesWon.setText(String.format("Won: %d", totalWins[RED]));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
//    private void refreshMercenarySelectGrid() {
//        for (int panel=0; panel<mercenarySelectPanels.length; panel++) {
//            int i = 0;
//            for (Component component : mercenarySelectPanels[panel].getComponents())
//                if (component instanceof JButton) {
//                    JButton but = (JButton)component;
//                    if (i != selectedMercenary[panel])
//                        but.setBackground(Color.WHITE);
//                    else if (panel == BLU)
//                        but.setBackground(Calculate.getColourHighlight(Color.WHITE, BLU_COLOUR, 0.5f));
//                    else
//                        but.setBackground(Calculate.getColourHighlight(Color.WHITE, RED_COLOUR, 0.5f));
//                    i++;
//                }
//        }

//    }
    private class ClassSelectButtonHandler implements ActionListener {
        private final int team, mercenary;
        ClassSelectButtonHandler(int team, int mercenary) {
            this.team = team;
            this.mercenary = mercenary;
        }
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            setSelectedMercenary(team, mercenary);
            refreshMercenarySelectGrids();
        }

    }
    private class BiasGridButtonHandler implements ActionListener {
        private final int blu, red;
        BiasGridButtonHandler(int blu, int red) {
            this.blu = blu;
            this.red = red;
        }
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            setSelectedMercenaries(blu, red);
            highlightSelectedMercenaries();
//            refreshBiasGrid();
        }

    }
    private abstract class DropdownSelectHandler implements ItemListener {
        protected boolean ignoreStateChanges;
        DropdownSelectHandler() {
            ignoreStateChanges = false;
        }
        protected String selectedString(ItemSelectable is) {
            Object[] selected = is.getSelectedObjects();
            return ((selected.length == 0) ? "null" : (String) selected[0]);
        }
        void setIgnoreStateChanges(boolean ignoreStateChanges) {
            this.ignoreStateChanges = ignoreStateChanges;
        }

    }
    private class MapDropdownSelectHandler extends DropdownSelectHandler {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED && !ignoreStateChanges) {
                ItemSelectable is = e.getItemSelectable();
                setSelectedMap(selectedString(is));
                refreshGamesPlayedLabels();
            }
        }

    }
    private class GameModeDropdownSelectHandler extends DropdownSelectHandler {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED && !ignoreStateChanges) {
                ItemSelectable is = e.getItemSelectable();
                setSelectedGameMode(selectedString(is));
                refreshGamesPlayedLabels();
            }
        }

    }
    public class ToggleOverallMapButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            viewMapsCombined = !viewMapsCombined;
            setViewMapsCombined(viewMapsCombined);
            refreshBiasGrid();
        }

    }
    private class ToggleOverallGameModeButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            viewGameModesCombined = !viewGameModesCombined;
            setViewGameModesCombined(viewGameModesCombined);
            refreshBiasGrid();
        }
    }
}
