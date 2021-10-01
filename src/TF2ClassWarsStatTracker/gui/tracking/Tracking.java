package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.exceptions.GameMapNotFoundException;
import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.game.GameModeGrid;
import TF2ClassWarsStatTracker.util.Calculate;
import TF2ClassWarsStatTracker.util.Constants;
import TF2ClassWarsStatTracker.util.JSONHandler;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.BLU_COLOUR;
import static TF2ClassWarsStatTracker.util.Constants.RED_COLOUR;

public class Tracking extends JPanel {
    private static final String OVERALL_MAP = "OVERALL";
    private static String selectedMap;
    private static int selectedGameMode = -1, selectedBluMercenary = -1, selectedRedMercenary = -1;
    private static JLabel labSelectedBluMerc, labSelectedRedMerc;
    private static JPanel panMercenaryGrid;

    public Tracking() {
        super(new BorderLayout());

        JPanel panMenuBar = new JPanel(new BorderLayout());
        JButton butBack = new JButton("Back");
        butBack.addActionListener(new MenuBarButtonHandler(MenuBarButtonHandler.MENU));
        JButton butOverall = new JButton("View Overall");
        butOverall.addActionListener(new MenuBarButtonHandler(MenuBarButtonHandler.OVERALL));

        JPanel panLeft = new JPanel(new BorderLayout());
        JPanel panRight = new JPanel(new BorderLayout());

        JPanel panSelectedMapInfo = new JPanel(new FlowLayout());

        List<GameMap> maps = GameMap.getMaps();
        ArrayList<String> mapNames = new ArrayList<>();
        for (GameMap map : maps)
            mapNames.add(map.getMapName());
        selectedMap = OVERALL_MAP;  // Set default to the overall scores
        JComboBox<String> mapDropdownSelect = new JComboBox<>();
        for (Object mapName : mapNames.toArray())
            mapDropdownSelect.addItem(mapName.toString());
        mapDropdownSelect.setSelectedIndex(0);
        mapDropdownSelect.addItemListener(new MapDropdownHandler());

        JPanel panBluVsRed = new JPanel(new BorderLayout());

        JPanel panBlu = new JPanel(new BorderLayout());
        JPanel panBluHeader = new JPanel(new FlowLayout());
        JLabel labBlu = new JLabel("BLU");
        JButton butBluWin = new JButton("WIN");
        butBluWin.addActionListener(new RecordWinButtonHandler(Constants.BLU));

        JPanel panRed = new JPanel(new BorderLayout());
        JPanel panRedHeader = new JPanel(new FlowLayout());
        JLabel labRed = new JLabel("RED");
        JButton butRedWin = new JButton("WIN");
        butRedWin.addActionListener(new RecordWinButtonHandler(Constants.RED));

        JPanel panBluClassSelect = new JPanel(new GridLayout(3,3));
        for (int i=0; i<9; i++) {
            JButton butClassSelect = new JButton(Constants.MERCENARY[i]);
            butClassSelect.addActionListener(new ClassSelectButtonHandler(Constants.BLU, i));
            panBluClassSelect.add(butClassSelect);
        }
        labSelectedBluMerc = new JLabel("No selected mercenary");
        JPanel panRedClassSelect = new JPanel(new GridLayout(3,3));
        for (int i=0; i<9; i++) {
            JButton butClassSelect = new JButton(Constants.MERCENARY[i]);
            butClassSelect.addActionListener(new ClassSelectButtonHandler(Constants.RED, i));
            panRedClassSelect.add(butClassSelect);
        }
        labSelectedRedMerc = new JLabel("No selected mercenary");

        JPanel panGameModeSelect = new JPanel(new GridLayout(1, 5));
        ButtonGroup gameModeSelectGroup = new ButtonGroup();
        for (int i=0; i<6; i++) {
            JRadioButton radGameMode;
            int gameMode;
            if (i != 0) {
                radGameMode = new JRadioButton(GameModeGrid.GAME_MODE[i-1]);
                gameMode = i-1;
                radGameMode.addItemListener(new GameModeSelectHandler(i-1));
            } else {
                radGameMode = new JRadioButton("Overall", true);
                gameMode = -1;
            }
            radGameMode.addItemListener(new GameModeSelectHandler(gameMode));
            gameModeSelectGroup.add(radGameMode);
            panGameModeSelect.add(radGameMode);
        }

        panMercenaryGrid = new JPanel(new GridLayout(10, 10));

        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panLeft.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panRight.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panBlu.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        panRed.setBorder(BorderFactory.createLineBorder(Color.RED));

        add(panMenuBar, BorderLayout.PAGE_START);
        panMenuBar.add(butBack, BorderLayout.WEST);
        panMenuBar.add(butOverall, BorderLayout.EAST);
        add(panLeft, BorderLayout.WEST);
        panLeft.add(panSelectedMapInfo, BorderLayout.LINE_START);
        panSelectedMapInfo.add(mapDropdownSelect);
        panLeft.add(panBluVsRed, BorderLayout.PAGE_END);
        panBluVsRed.add(panBlu, BorderLayout.WEST);
        panBlu.add(panBluHeader, BorderLayout.NORTH);
        panBluHeader.add(labBlu);
        panBluHeader.add(butBluWin);
        panBlu.add(panBluClassSelect, BorderLayout.CENTER);
        panBlu.add(labSelectedBluMerc, BorderLayout.SOUTH);
        panBluVsRed.add(panRed, BorderLayout.EAST);
        panRed.add(panRedHeader, BorderLayout.NORTH);
        panRedHeader.add(labRed);
        panRedHeader.add(butRedWin);
        panRed.add(panRedClassSelect, BorderLayout.CENTER);
        panRed.add(labSelectedRedMerc, BorderLayout.SOUTH);
        panRight.add(panGameModeSelect, BorderLayout.NORTH);
        panRight.add(panMercenaryGrid, BorderLayout.CENTER);
        add(panRight, BorderLayout.EAST);

        reloadGrid();
    }

    static void reloadGrid() {
        panMercenaryGrid.removeAll();
        GameModeGrid grid;
        try {
            if (selectedMap.equals(OVERALL_MAP) && selectedGameMode == -1)
                grid = GameModeGrid.getOverallGrid();
            else if (selectedGameMode == -1)
                    grid = GameModeGrid.getOverallGrid(selectedMap);
            else if (selectedMap.equals(OVERALL_MAP))
                grid = GameModeGrid.getGameModeOverallGrid(selectedGameMode);
            else
                grid = GameMap.getMap(selectedMap).getGameModeGrid(selectedGameMode);
        } catch (GameMapNotFoundException ex) {
            Print.error(ex.getMessage());
            ex.printStackTrace();
            grid = GameModeGrid.getEmptyGrid();
        }
        fillGrid(grid);
        panMercenaryGrid.revalidate();
    }

    private static void fillGrid(GameModeGrid grid) {
        for (int row=0; row<10; row++) {
            for (int column=0; column<10; column++) {
                Color buttonBorderColour = Color.BLACK;
                JComponent gridElement;
                if (row == 0 && column > 0) {  // BLU mercenaries (first row)
                    gridElement = new JLabel(Constants.MERCENARY[column-1]);
                    gridElement.setBackground(BLU_COLOUR);
                } else if (row == 0) {
                    gridElement = new JLabel("RED \\ BLU");
                } else if (column == 0) {  // RED mercenaries (first column)
                    gridElement = new JLabel(Constants.MERCENARY[row-1]);
                    gridElement.setBackground(RED_COLOUR);
                } else {  // Add button to select relevant match-up on the left panel
                    int[] matchupScores = grid.getMercenaryWins()[column-1][row-1];
                    float ratioBias = Calculate.getRatioBias(matchupScores[0], matchupScores[1]);
                    String buttonStr;
                    Color buttonColour = Color.WHITE;
                    if (!Float.isNaN(ratioBias)) {
                        buttonStr = String.format("%.2f", ratioBias);
                        buttonColour = Calculate.getColourScaledFromWhite(ratioBias, BLU_COLOUR, RED_COLOUR);
                    } else
                        buttonStr = "";
                    gridElement = new JButton(buttonStr);
                    gridElement.setBackground(buttonColour);
                    ((JButton)gridElement).addActionListener(new GridMercButtonSelectButtonHandler(column-1, row-1));
                    if (column-1 == selectedBluMercenary && row-1 == selectedRedMercenary)
                        buttonBorderColour = Color.YELLOW;
                }
                gridElement.setOpaque(true);
                gridElement.setBorder(BorderFactory.createLineBorder(buttonBorderColour));
                gridElement.setPreferredSize(new Dimension(65, 65));
                panMercenaryGrid.add(gridElement);
            }
        }
    }

    static void viewOverall() {
        setSelectedMap(OVERALL_MAP);
        // TODO: Can't click back to the map that was previously selected before the "Overall" button was clicked
    }

    static void setSelectedMercenary(int team, int mercenary) {
        if (team == Constants.BLU) {
            selectedBluMercenary = mercenary;
            labSelectedBluMerc.setText(Constants.MERCENARY[mercenary]);
        }
        else if (team == Constants.RED) {
            selectedRedMercenary = mercenary;
            labSelectedRedMerc.setText(Constants.MERCENARY[mercenary]);
        }
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
}
