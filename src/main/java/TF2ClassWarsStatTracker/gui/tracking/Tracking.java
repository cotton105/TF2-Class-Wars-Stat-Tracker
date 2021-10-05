package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.AppDataHandler;
import TF2ClassWarsStatTracker.exceptions.MapNotFoundException;
import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.game.GameModeGrid;
import TF2ClassWarsStatTracker.gui.TrackingGUIJPanel;
import TF2ClassWarsStatTracker.util.Calculate;
import TF2ClassWarsStatTracker.util.Constants;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.*;

public class Tracking extends TrackingGUIJPanel {
    public static final String OVERALL_MAP = "Overall scores";
    private static String selectedMap;
    private static int selectedGameMode = -1;
    private static final int[] selectedMercenary = new int[] {-1, -1};
    private static JLabel labGamesPlayedTotal, labBluGamesWon, labRedGamesWon;
    private static JPanel panMercenaryGrid;
    private static List<JPanel> mercenarySelectPanels;
    private static JButton butBluWin, butRedWin;
    private static JComboBox<String> mapDropdownSelect;

    public Tracking() {
        super(new BorderLayout());

        JPanel panMenuBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton butBack = new JButton("Back");
        butBack.addActionListener(new GeneralButtonHandler(GeneralButtonHandler.BACK));

        JPanel panLeft = new JPanel(new BorderLayout());
        JPanel panRight = new JPanel(new BorderLayout());

        selectedMap = OVERALL_MAP;  // Set default to the overall scores
        mapDropdownSelect = new JComboBox<>();
        refreshMapList();
        mapDropdownSelect.addItemListener(new MapDropdownHandler());

        JPanel panBluVsRed = new JPanel(new BorderLayout());

        JPanel panBlu = new JPanel(new BorderLayout());
        JPanel panBluHeader = new JPanel(new FlowLayout());
        JLabel labBlu = new JLabel("BLU");
        butBluWin = new JButton("WIN");
        butBluWin.addActionListener(new RecordWinButtonHandler(BLU));

        mercenarySelectPanels = new ArrayList<>();
        JPanel panBluMercenarySelect = new JPanel(new GridLayout(3,3));
        mercenarySelectPanels.add(panBluMercenarySelect);
        for (int i=0; i<9; i++) {
            JButton butClassSelect = new JButton(Constants.MERCENARY[i]);
            butClassSelect.addActionListener(new ClassSelectButtonHandler(BLU, i));
            butClassSelect.setBackground(Color.WHITE);
            butClassSelect.setPreferredSize(new Dimension(120, 40));
            panBluMercenarySelect.add(butClassSelect);
        }
        JPanel panSelectedBluInfo = new JPanel(new GridLayout(2, 1));
        labBluGamesWon = new JLabel("Won: N/A");

        JPanel panRed = new JPanel(new BorderLayout());
        JPanel panRedHeader = new JPanel(new FlowLayout());
        JLabel labRed = new JLabel("RED");
        butRedWin = new JButton("WIN");
        butRedWin.addActionListener(new RecordWinButtonHandler(Constants.RED));

        JPanel panRedMercenarySelect = new JPanel(new GridLayout(3,3));
        mercenarySelectPanels.add(panRedMercenarySelect);
        for (int i=0; i<9; i++) {
            JButton butClassSelect = new JButton(Constants.MERCENARY[i]);
            butClassSelect.addActionListener(new ClassSelectButtonHandler(Constants.RED, i));
            butClassSelect.setBackground(Color.WHITE);
            butClassSelect.setPreferredSize(new Dimension(120, 40));
            panRedMercenarySelect.add(butClassSelect);
        }
        JPanel panSelectedRedInfo = new JPanel(new GridLayout(2, 1));
        labRedGamesWon = new JLabel("Won: N/A");

        JButton butViewOverallMap = new JButton("View Overall");
        butViewOverallMap.addActionListener(new GeneralButtonHandler(GeneralButtonHandler.OVERALL));
        JPanel panSelectedGameInfo = new JPanel(new GridLayout(3, 1));
        JPanel panSelectedMapInfo = new JPanel(new FlowLayout());
        labGamesPlayedTotal = new JLabel();
        JPanel panGameModeSelect = new JPanel(new GridLayout(1, 5));
        ButtonGroup gameModeSelectGroup = new ButtonGroup();
        JRadioButton radOverall = new JRadioButton("Overall", true);
        radOverall.addItemListener(new GameModeSelectHandler(-1));
        gameModeSelectGroup.add(radOverall);
        panGameModeSelect.add(radOverall);
        for (int i=0; i<5; i++) {
            JRadioButton radGameMode = new JRadioButton(String.format("<html>%s</html>", GameModeGrid.GAME_MODES[i]));
            radGameMode.addItemListener(new GameModeSelectHandler(i));
            radGameMode.setPreferredSize(new Dimension(150, 50));
            gameModeSelectGroup.add(radGameMode);
            panGameModeSelect.add(radGameMode);
        }
        panMercenaryGrid = new JPanel(new GridLayout(10, 10, -1, -1));

        // Set Borders everywhere
        setBorder(BorderFactory.createEmptyBorder(0,20,20,20));
        panLeft.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panRight.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panBlu.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        panRed.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        // JComponent parent-child structure
        add(panMenuBar, BorderLayout.NORTH);
        add(panLeft, BorderLayout.WEST);
        add(panRight, BorderLayout.CENTER);

        panMenuBar.add(butBack);

        panLeft.add(panBluVsRed, BorderLayout.SOUTH);

        panBluVsRed.add(panBlu, BorderLayout.NORTH);
        panBluVsRed.add(panRed, BorderLayout.SOUTH);

        panBlu.add(panBluHeader, BorderLayout.NORTH);
        panBlu.add(panBluMercenarySelect, BorderLayout.CENTER);
        panBlu.add(panSelectedBluInfo, BorderLayout.SOUTH);

        panBluHeader.add(labBlu);
        panBluHeader.add(butBluWin);

        panSelectedBluInfo.add(labBluGamesWon);

        panRed.add(panRedHeader, BorderLayout.NORTH);
        panRed.add(panRedMercenarySelect, BorderLayout.CENTER);
        panRed.add(panSelectedRedInfo, BorderLayout.SOUTH);

        panRedHeader.add(labRed);
        panRedHeader.add(butRedWin);

        panSelectedRedInfo.add(labRedGamesWon);

        panRight.add(panSelectedGameInfo, BorderLayout.NORTH);
        panRight.add(panMercenaryGrid, BorderLayout.CENTER);

        panSelectedGameInfo.add(panSelectedMapInfo);
        panSelectedGameInfo.add(panGameModeSelect);
        panSelectedGameInfo.add(labGamesPlayedTotal);

        panSelectedMapInfo.add(mapDropdownSelect);
        panSelectedMapInfo.add(butViewOverallMap);

        setDefaultFont(this, TF2secondary.deriveFont(16f));
        setDefaultFont(panSelectedBluInfo, TF2secondary.deriveFont(20f));
        setDefaultFont(panSelectedRedInfo, TF2secondary.deriveFont(20f));
        refreshGrid();
        refreshGamesPlayedLabels();
    }

    public static void refreshMapList() {
        MapDropdownHandler.setMapBeingAdded(true);
        mapDropdownSelect.removeAllItems();
        List<GameMap> maps = AppDataHandler.getMaps();
        ArrayList<String> mapNames = new ArrayList<>();
        for (GameMap map : maps)
            mapNames.add(map.getMapName());
        mapDropdownSelect.addItem(OVERALL_MAP);
        for (Object mapName : mapNames.toArray())
            mapDropdownSelect.addItem(mapName.toString());
        MapDropdownHandler.setMapBeingAdded(false);
        mapDropdownSelect.setSelectedItem(selectedMap);
    }

    static void refreshGrid() {
        panMercenaryGrid.removeAll();
        GameModeGrid grid;
        try {
            if (selectedMap.equals(OVERALL_MAP) && selectedGameMode == -1) {
                setWinButtonAvailability(false);
                grid = AppDataHandler.getOverallGrid();
            }
            else if (selectedGameMode == -1) {
                setWinButtonAvailability(false);
                grid = AppDataHandler.getOverallGrid(selectedMap);
            }
            else if (selectedMap.equals(OVERALL_MAP)) {
                setWinButtonAvailability(false);
                grid = AppDataHandler.getGameModeOverallGrid(selectedGameMode);
            }
            else {
                if (selectedMercenary[BLU] != -1 && selectedMercenary[Constants.RED] != -1)
                    setWinButtonAvailability(true);
                grid = AppDataHandler.getMap(selectedMap).getGameModeGrid(selectedGameMode);
            }
        } catch (MapNotFoundException ex) {
            Print.error(ex.getMessage());
            ex.printStackTrace();
            grid = GameModeGrid.getEmptyGrid();
        }
        fillGrid(grid);
        panMercenaryGrid.revalidate();
    }

    private static void setWinButtonAvailability(boolean available) {
        butBluWin.setEnabled(available);
        butRedWin.setEnabled(available);
    }

    private static void fillGrid(GameModeGrid grid) {
        for (int row=0; row<10; row++) {
            for (int column=0; column<10; column++) {
                Color borderColour = Color.BLACK;
                JComponent gridElement;
                float fontSize = 16f;
                if (row == 0 && column > 0) {  // BLU mercenaries (first row)
                    gridElement = new JLabel(Constants.MERCENARY[column-1]);
                    gridElement.setBackground(BLU_COLOUR);
                    ((JLabel)gridElement).setHorizontalAlignment(JLabel.CENTER);
                } else if (row == 0) {
                    gridElement = new JLabel("RED \\ BLU");
                    ((JLabel)gridElement).setHorizontalAlignment(JLabel.CENTER);
                } else if (column == 0) {  // RED mercenaries (first column)
                    gridElement = new JLabel(Constants.MERCENARY[row-1]);
                    gridElement.setBackground(RED_COLOUR);
                    ((JLabel)gridElement).setHorizontalAlignment(JLabel.CENTER);
                } else {  // Add button to select relevant match-up on the left panel
                    int[] matchupScores = grid.getMatchupWins()[column-1][row-1];
                    float ratioBias = Calculate.getRatioBias(matchupScores[0], matchupScores[1]);
                    String buttonStr;
                    Color buttonColour = Color.WHITE;
                    if (!Float.isNaN(ratioBias)) {
                        buttonStr = String.format("%.2f", ratioBias);
                        buttonColour = Calculate.getColourScaledFromWhite(ratioBias, BLU_COLOUR, RED_COLOUR);
                    } else
                        buttonStr = "";
                    fontSize = 20f;
                    gridElement = new JButton(buttonStr);
                    ((JButton)gridElement).addActionListener(new GridMercButtonSelectButtonHandler(column-1, row-1));
                    if (column-1 == selectedMercenary[BLU] && row-1 == selectedMercenary[RED]) {
                        borderColour = Color.YELLOW;
                        buttonColour = Calculate.getColourHighlight(buttonColour, Color.YELLOW);
                    }
                    gridElement.setBackground(buttonColour);
                }
                gridElement.setOpaque(true);
                gridElement.setBorder(BorderFactory.createLineBorder(borderColour, 2));
                gridElement.setPreferredSize(new Dimension(65, 65));
                gridElement.setFont(TF2secondary.deriveFont(fontSize));
                panMercenaryGrid.add(gridElement);
            }
        }
    }

    static void setSelectedMercenary(int team, int mercenary) {
        if (team == BLU)
            selectedMercenary[BLU] = mercenary;
        else if (team == RED)
            selectedMercenary[RED] = mercenary;
        refreshMercenarySelectGrid();
    }

    static void setSelectedMercenaries(int bluMercenary, int redMercenary) {
        selectedMercenary[BLU] = bluMercenary;
        selectedMercenary[RED] = redMercenary;
        refreshMercenarySelectGrid();
    }

    private static void refreshMercenarySelectGrid() {
        for (int panel=0; panel<mercenarySelectPanels.size(); panel++) {
            int i = 0;
            for (Component component : mercenarySelectPanels.get(panel).getComponents())
                if (component instanceof JButton) {
                    JButton but = (JButton)component;
                    if (i != selectedMercenary[panel])
                        but.setBackground(Color.WHITE);
                    else if (panel == BLU)
                        but.setBackground(Calculate.getColourHighlight(Color.WHITE, BLU_COLOUR, 0.5f));
                    else
                        but.setBackground(Calculate.getColourHighlight(Color.WHITE, RED_COLOUR, 0.5f));
                    i++;
                }
        }
    }

    static void viewOverall() {
        setSelectedMap(OVERALL_MAP);
        mapDropdownSelect.setSelectedItem(OVERALL_MAP);
    }

    public static void setSelectedMap(String mapName) {
        selectedMap = mapName;
        refreshGrid();
    }

    static String getSelectedMap() {
        return selectedMap;
    }

    static void setSelectedGameMode(int gameMode) {
        selectedGameMode = gameMode;
        refreshGrid();
        refreshGamesPlayedLabels();
    }

    static int getSelectedGameMode() {
        return selectedGameMode;
    }

    static int getSelectedBluMercenary() {
        return selectedMercenary[BLU];
    }

    static int getSelectedRedMercenary() {
        return selectedMercenary[RED];
    }

    static void refreshGamesPlayedLabels() {
        if (0 <= selectedMercenary[BLU] && selectedMercenary[BLU] < 9 && 0 <= selectedMercenary[RED] && selectedMercenary[RED] < 9) {
            try {
                int[] totalWins = AppDataHandler.getMatchupWins(selectedMap, selectedGameMode, selectedMercenary[BLU], selectedMercenary[RED]);
                String bluGamesWonText = String.format("Won: %d", totalWins[0]);
                String redGamesWonText = String.format("Won: %d", totalWins[1]);
                labBluGamesWon.setText(bluGamesWonText);
                labRedGamesWon.setText(redGamesWonText);
            } catch (MapNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        try {
            int totalGames = AppDataHandler.getTotalGames(selectedMap, selectedGameMode);
            labGamesPlayedTotal.setText(String.format("Total games recorded in this configuration: %d", totalGames));
        } catch (MapNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void refreshAll() {
        refreshGrid();
        refreshMapList();
        refreshGamesPlayedLabels();
        refreshMercenarySelectGrid();
    }
}
