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
import java.sql.SQLException;

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
    private final int[] selectedMercenary = new int[] {-1, -1}, highlightedMatchup = new int[] {-1, -1};
    private final JPanel[] mercenarySelectPanels = new JPanel[] {panBluClassSelectGrid, panRedClassSelectGrid};
    private final JButton[] winButtons = new JButton[] {butBluWin, butRedWin};
    private boolean viewMapsCombined = true, viewGameModesCombined = true;
    private int selectedTeam, selectedGameMode, selectedStageNumber;
    private String selectedMap;

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

        fillBiasGrid(ConfigurationGrid.getEmptyGrid());
    }

    public JPanel getPanMain() {
        return panMain;
    }

    private void setBiasGridBorders() {
    }

    private void setSelectedMercenary(int team, int mercenary) throws InvalidTeamNumberException {
        if (team != BLU && team != RED) throw new InvalidTeamNumberException();
        selectedMercenary[team] = mercenary;
        refreshMercenarySelectGrids();
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
                grid = DBHandler.Retrieve.getOverallGameModeGrid(GAME_MODES[selectedGameMode]);
            }
            else {
                if (selectedMercenary[BLU] > -1 && selectedMercenary[RED] != -1)
                    setWinButtonEnabled(true);
                grid = DBHandler.Retrieve.getMatchupGrid(selectedMap, selectedStageNumber, GAME_MODES[selectedGameMode]);
            }
        } catch (SQLException e) {
            Print.error(e.getMessage());
            e.printStackTrace();
            grid = ConfigurationGrid.getEmptyGrid();
        }
        AppDataHandler.setLoadedConfiguration(grid);
        fillBiasGrid(grid);

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
//        refreshBiasGrid();
    }

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

}
