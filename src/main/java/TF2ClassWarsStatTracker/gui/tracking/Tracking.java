package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.exceptions.GameMapNotFoundException;
import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.game.GameModeGrid;
import TF2ClassWarsStatTracker.gui.TrackingGUIJPanel;
import TF2ClassWarsStatTracker.util.Calculate;
import TF2ClassWarsStatTracker.util.Constants;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.BLU_COLOUR;
import static TF2ClassWarsStatTracker.util.Constants.RED_COLOUR;

public class Tracking extends TrackingGUIJPanel {
    public static final String OVERALL_MAP = "Overall scores";
    private static String selectedMap;
    private static int selectedGameMode = -1, selectedBluMercenary = -1, selectedRedMercenary = -1;
    private static JLabel labSelectedBluMerc, labSelectedRedMerc, labGamesPlayedTotal, labBluGamesWon, labRedGamesWon;
    private static JPanel panMercenaryGrid;
    private static JButton butBluWin, butRedWin;
    private static JComboBox<String> mapDropdownSelect;
    private static JTextField fieldNewMap;

    public Tracking() {
        super(new BorderLayout());

        JPanel panMenuBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton butBack = new JButton("Back");
        butBack.addActionListener(new GeneralButtonHandler(GeneralButtonHandler.BACK));


        JPanel panLeft = new JPanel(new BorderLayout());
        JPanel panRight = new JPanel(new BorderLayout());

        List<GameMap> maps = GameMap.getMaps();
        ArrayList<String> mapNames = new ArrayList<>();
        for (GameMap map : maps)
            mapNames.add(map.getMapName());
        selectedMap = OVERALL_MAP;  // Set default to the overall scores
        mapDropdownSelect = new JComboBox<>();
        mapDropdownSelect.addItem(OVERALL_MAP);
        for (Object mapName : mapNames.toArray())
            mapDropdownSelect.addItem(mapName.toString());
        mapDropdownSelect.addItemListener(new MapDropdownHandler());

        JPanel panBluVsRed = new JPanel(new BorderLayout());

        JPanel panBlu = new JPanel(new BorderLayout());
        JPanel panBluHeader = new JPanel(new FlowLayout());
        JLabel labBlu = new JLabel("BLU");
        butBluWin = new JButton("WIN");
        butBluWin.addActionListener(new RecordWinButtonHandler(Constants.BLU));
        JPanel panBluClassSelect = new JPanel(new GridLayout(3,3));
        for (int i=0; i<9; i++) {
            JButton butClassSelect = new JButton(Constants.MERCENARY[i]);
            butClassSelect.addActionListener(new ClassSelectButtonHandler(Constants.BLU, i));
            panBluClassSelect.add(butClassSelect);
        }
        JPanel panSelectedBluInfo = new JPanel(new GridLayout(2, 1));
        labSelectedBluMerc = new JLabel("Selected:");
        labBluGamesWon = new JLabel();

        JPanel panRed = new JPanel(new BorderLayout());
        JPanel panRedHeader = new JPanel(new FlowLayout());
        JLabel labRed = new JLabel("RED");
        butRedWin = new JButton("WIN");
        butRedWin.addActionListener(new RecordWinButtonHandler(Constants.RED));
        JPanel panRedClassSelect = new JPanel(new GridLayout(3,3));
        for (int i=0; i<9; i++) {
            JButton butClassSelect = new JButton(Constants.MERCENARY[i]);
            butClassSelect.addActionListener(new ClassSelectButtonHandler(Constants.RED, i));
            panRedClassSelect.add(butClassSelect);
        }
        JPanel panSelectedRedInfo = new JPanel(new GridLayout(2, 1));
        labSelectedRedMerc = new JLabel("Selected: ");
        labRedGamesWon = new JLabel();

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
        panBlu.add(panBluClassSelect, BorderLayout.CENTER);
        panBlu.add(panSelectedBluInfo, BorderLayout.SOUTH);

        panBluHeader.add(labBlu);
        panBluHeader.add(butBluWin);

        panSelectedBluInfo.add(labSelectedBluMerc);
        panSelectedBluInfo.add(labBluGamesWon);

        panRed.add(panRedHeader, BorderLayout.NORTH);
        panRed.add(panRedClassSelect, BorderLayout.CENTER);
        panRed.add(panSelectedRedInfo, BorderLayout.SOUTH);

        panRedHeader.add(labRed);
        panRedHeader.add(butRedWin);

        panSelectedRedInfo.add(labSelectedRedMerc);
        panSelectedRedInfo.add(labRedGamesWon);

        panRight.add(panSelectedGameInfo, BorderLayout.NORTH);
        panRight.add(panMercenaryGrid, BorderLayout.CENTER);

        panSelectedGameInfo.add(panSelectedMapInfo);
        panSelectedGameInfo.add(panGameModeSelect);
        panSelectedGameInfo.add(labGamesPlayedTotal);

        panSelectedMapInfo.add(mapDropdownSelect);
        panSelectedMapInfo.add(butViewOverallMap);

        // TODO: Add button to create a new map

        setDefaultFont(this, TF2secondary);
        reloadGrid();
        updateGamesPlayedLabels();
    }

    static void reloadGrid() {
        panMercenaryGrid.removeAll();
        GameModeGrid grid;
        try {
            if (selectedMap.equals(OVERALL_MAP) && selectedGameMode == -1) {
                setWinButtonAvailability(false);
                grid = GameModeGrid.getOverallGrid();
            }
            else if (selectedGameMode == -1) {
                setWinButtonAvailability(false);
                grid = GameModeGrid.getOverallGrid(selectedMap);
            }
            else if (selectedMap.equals(OVERALL_MAP)) {
                setWinButtonAvailability(false);
                grid = GameModeGrid.getGameModeOverallGrid(selectedGameMode);
            }
            else {
                setWinButtonAvailability(true);
                grid = GameMap.getMap(selectedMap).getGameModeGrid(selectedGameMode);
            }
        } catch (GameMapNotFoundException ex) {
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
                    gridElement = new JButton(buttonStr);
                    ((JButton)gridElement).addActionListener(new GridMercButtonSelectButtonHandler(column-1, row-1));
                    if (column-1 == selectedBluMercenary && row-1 == selectedRedMercenary) {
                        borderColour = Color.YELLOW;
                        buttonColour = Calculate.getColourHighlight(buttonColour, Color.YELLOW);
                    }
                    gridElement.setBackground(buttonColour);
                }
                gridElement.setOpaque(true);
                gridElement.setBorder(BorderFactory.createLineBorder(borderColour, 2));
                gridElement.setPreferredSize(new Dimension(65, 65));
                gridElement.setFont(TF2secondary);
                panMercenaryGrid.add(gridElement);
            }
        }
    }

    static void setSelectedMercenary(int team, int mercenary) {
        if (team == Constants.BLU) {
            selectedBluMercenary = mercenary;
            labSelectedBluMerc.setText(String.format("Selected: %s", Constants.MERCENARY[mercenary]));
        }
        else if (team == Constants.RED) {
            selectedRedMercenary = mercenary;
            labSelectedRedMerc.setText(String.format("Selected: %s", Constants.MERCENARY[mercenary]));
        }
    }

    static void viewOverall() {
        setSelectedMap(OVERALL_MAP);
        mapDropdownSelect.setSelectedItem(OVERALL_MAP);
    }

    static void setSelectedMap(String mapName) {
        selectedMap = mapName;
        reloadGrid();
    }

    static String getSelectedMap() {
        return selectedMap;
    }

    static void setSelectedGameMode(int gameMode) {
        selectedGameMode = gameMode;
        reloadGrid();
    }

    static int getSelectedGameMode() {
        return selectedGameMode;
    }

    static int getSelectedBluMercenary() {
        return selectedBluMercenary;
    }

    static int getSelectedRedMercenary() {
        return selectedRedMercenary;
    }

    static void updateGamesPlayedLabels() {
        if (0 <= selectedBluMercenary && selectedBluMercenary < 9 && 0 <= selectedRedMercenary && selectedRedMercenary < 9) {
            try {
                int[] totalWins = GameMap.getMatchupWins(selectedMap, selectedGameMode, selectedBluMercenary, selectedRedMercenary);
                String bluGamesWonText = String.format("Won: %d", totalWins[0]);
                String redGamesWonText = String.format("Won: %d", totalWins[1]);
                labBluGamesWon.setText(bluGamesWonText);
                labRedGamesWon.setText(redGamesWonText);
            } catch (GameMapNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        try {
            int totalGames = GameMap.getTotalGames(selectedMap, selectedGameMode);
            labGamesPlayedTotal.setText(String.format("Total games recorded in this configuration: %d", totalGames));
        } catch (GameMapNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
