package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.game.GameModeGrid;
import TF2ClassWarsStatTracker.util.Calculate;
import TF2ClassWarsStatTracker.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Tracking extends JPanel {
    private static final Color
            BLU_COLOR = new Color(171,203,255),
            RED_COLOR = new Color(255,125,125);
    private static String selectedMap, selectedBluMerc, selectedRedMerc;
    private static int selectedGameMode = -1;
    private static JLabel labSelectedBluMerc, labSelectedRedMerc;
    private static JPanel panMercenaryGrid;
    private static JComboBox mapDropdownSelect;

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

        ArrayList<GameMap> maps = new ArrayList<>(GameMap.gameMapsFromJSON());
        ArrayList<String> mapNames = new ArrayList<>();
        for (GameMap map : maps)
            mapNames.add(map.getMapName());
        selectedMap = mapNames.get(0);  // Set default to the first map
        mapDropdownSelect = new JComboBox(mapNames.toArray());
        mapDropdownSelect.setSelectedIndex(0);
        mapDropdownSelect.addItemListener(new MapDropdownHandler());

        JPanel panBluVsRed = new JPanel(new BorderLayout());

        JPanel panBlu = new JPanel(new BorderLayout());
        JPanel panRed = new JPanel(new BorderLayout());

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
        JLabel labBlu = new JLabel("BLU");
        JLabel labRed = new JLabel("RED");

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
        panBlu.add(labBlu, BorderLayout.NORTH);
        panBlu.add(panBluClassSelect, BorderLayout.CENTER);
        panBlu.add(labSelectedBluMerc, BorderLayout.SOUTH);
        panBluVsRed.add(panRed, BorderLayout.EAST);
        panRed.add(labRed, BorderLayout.NORTH);
        panRed.add(panRedClassSelect, BorderLayout.CENTER);
        panRed.add(labSelectedRedMerc, BorderLayout.SOUTH);
        panRight.add(panGameModeSelect, BorderLayout.NORTH);
        panRight.add(panMercenaryGrid, BorderLayout.CENTER);
        add(panRight, BorderLayout.EAST);

        reloadGrid();
    }

    private static void reloadGrid() {
        panMercenaryGrid.removeAll();
        GameModeGrid grid;
        if (selectedMap.equals("overall") && selectedGameMode == -1)
            grid = GameModeGrid.getOverallGrid();
        else if (selectedGameMode == -1)
            grid = GameModeGrid.getOverallGrid(selectedMap);
        else if (selectedMap.equals("overall"))
            grid = GameModeGrid.getGameModeOverallGrid(selectedGameMode);
        else
            grid = GameMap.gameMapFromJSON(selectedMap).getGameModeGrid(selectedGameMode);
        fillGrid(grid);
        panMercenaryGrid.revalidate();
    }

    private static void fillGrid(GameModeGrid grid) {
        for (int row=0; row<10; row++) {
            for (int column=0; column<10; column++) {
                JComponent gridElement;
                if (row == 0 && column > 0) {  // BLU mercenaries (first row)
                    gridElement = new JLabel(Constants.MERCENARY[column-1]);
                    gridElement.setBackground(BLU_COLOR);
                } else if (row == 0) {
                    gridElement = new JLabel("RED \\ BLU");
                } else if (column == 0) {  // RED mercenaries (first column)
                    gridElement = new JLabel(Constants.MERCENARY[row-1]);
                    gridElement.setBackground(RED_COLOR);
                } else {  // Add button to select relevant match-up on the left panel
                    int[] matchupScores = grid.getMercenaryWins()[column-1][row-1];
                    float ratioBias = Calculate.getRatioBias(matchupScores[0], matchupScores[1]);
                    String buttonStr;
                    if (!Float.isNaN(ratioBias))
                        buttonStr = String.format("%.2f", ratioBias);
                    else
                        buttonStr = "";
                    gridElement = new JButton(buttonStr);
                    gridElement.addMouseListener(new GridMercButtonSelectButtonHandler(column-1, row-1));
                }
                gridElement.setOpaque(true);
                gridElement.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridElement.setPreferredSize(new Dimension(65, 65));
                panMercenaryGrid.add(gridElement);
            }
        }
    }

    static void viewOverall() {
        setSelectedMap("overall");
        // TODO: Can't click back to the map that was previously selected before the "Overall" button was clicked
    }

    static void setSelectedMercenary(int team, int mercenary) {
        if (team == Constants.BLU)
            labSelectedBluMerc.setText(Constants.MERCENARY[mercenary]);
        else if (team == Constants.RED)
            labSelectedRedMerc.setText(Constants.MERCENARY[mercenary]);
    }

    static void setSelectedMap(String mapName) {
        selectedMap = mapName;
        reloadGrid();
    }

    public static void setSelectedGameMode(int gameMode) {
        selectedGameMode = gameMode;
        reloadGrid();
    }
}
