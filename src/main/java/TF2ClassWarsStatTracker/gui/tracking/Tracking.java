package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.AppDataHandler;
import TF2ClassWarsStatTracker.ServerDataRetrieval;
import TF2ClassWarsStatTracker.exceptions.MapNotFoundException;
import TF2ClassWarsStatTracker.game.GameModeGrid;
import TF2ClassWarsStatTracker.gui.TrackingGUIJPanel;
import TF2ClassWarsStatTracker.util.Calculate;
import TF2ClassWarsStatTracker.util.Constants;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.*;

public class Tracking extends TrackingGUIJPanel {
    public static final String OVERALL_MAP = "Overall scores";
    public static final String
            VIEW_OVERALL_TEXT = "View Overall",
            REVERT_TO_MAP_TEXT = "Revert to Map";
    private static final int[] selectedMercenary = new int[] {-1, -1};
    private static final int SERVER_BANNER_WIDTH = 263;
    private static final JLabel[] labGamesWon = new JLabel[2];
    private static final JPanel[]
            panSelectedTeamInfo = new JPanel[2],
            mercenarySelectPanels = new JPanel[2];
    private static final JButton[] butWin = new JButton[2];
    private static String selectedServer, selectedMap;
    private static int selectedGameMode = -1;
    private static JLabel labGamesPlayedTotal;
    private static JPanel panMercenaryGrid;
    private static JComboBox<String> mapDropdownSelect;
    private static JEditorPane panServerBannerHTML;
    private static boolean displayOverallMap, displayOverallGameMode;
    public static JButton butToggleOverallMap;

    public Tracking() {
        super(new BorderLayout());

        JPanel panMenuBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton butBack = new JButton("Back");
        butBack.addActionListener(new GeneralButtonHandler(GeneralButtonHandler.BACK));

        JPanel panLeft = new JPanel(new BorderLayout());
        JPanel panRight = new JPanel(new BorderLayout());

//        selectedMap = OVERALL_MAP;  // Set default to the overall scores
        displayOverallMap = true;
        displayOverallGameMode = true;
        selectedMap = AppDataHandler.getMaps().get(0).getMapName();
        selectedGameMode = NORMAL;

        mapDropdownSelect = new JComboBox<>();
        refreshMapList();
        mapDropdownSelect.addItemListener(new MapDropdownSelectHandler());

        initialiseServerBanner();
        JScrollPane panServerBannerScroll = new JScrollPane(panServerBannerHTML);

        JPanel panBluVsRed = new JPanel(new BorderLayout());

        butToggleOverallMap = new JButton(REVERT_TO_MAP_TEXT);
//        butToggleOverallMap.addActionListener(new GeneralButtonHandler(GeneralButtonHandler.OVERALL));
        try {
            butToggleOverallMap.addActionListener(new ToggleOverallMapButtonHandler(ToggleOverallMapButtonHandler.VIEW_OVERALL));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

        // JComponent parent-child structure
        add(panMenuBar, BorderLayout.NORTH);
        add(panLeft, BorderLayout.WEST);
        add(panRight, BorderLayout.CENTER);

        panMenuBar.add(butBack);

        panLeft.add(panServerBannerScroll, BorderLayout.CENTER);
        panLeft.add(panBluVsRed, BorderLayout.SOUTH);

        panBluVsRed.add(classSelectGrid(BLU), BorderLayout.NORTH);
        panBluVsRed.add(classSelectGrid(RED), BorderLayout.SOUTH);

        panRight.add(panSelectedGameInfo, BorderLayout.NORTH);
        panRight.add(panMercenaryGrid, BorderLayout.CENTER);

        panSelectedGameInfo.add(panSelectedMapInfo);
        panSelectedGameInfo.add(panGameModeSelect);
        panSelectedGameInfo.add(labGamesPlayedTotal);

        panSelectedMapInfo.add(mapDropdownSelect);
        panSelectedMapInfo.add(butToggleOverallMap);

        // Set fonts
        setDefaultFont(this, TF2secondary.deriveFont(16f));
        for (JPanel pan : panSelectedTeamInfo)
            setDefaultFont(pan, TF2secondary.deriveFont(20f));

        refreshGrid();
        refreshGamesPlayedLabels();
        refreshServerBanner();
    }

    private static void initialiseServerBanner() {
        // TODO: Fix inconsistent panel size for the server banner (sometimes window is small and others it's bigger)
        panServerBannerHTML = new JEditorPane();
        panServerBannerHTML.setEditable(false);
        panServerBannerHTML.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        // Make links clickable
        panServerBannerHTML.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
            }
        });
    }

    private JComponent classSelectGrid(int team) {
        if (team < 0 || 1 < team )
            throw new IllegalArgumentException("Team must be 0 or 1 (BLU/RED)");
        JPanel panTeam = new JPanel(new BorderLayout());
        JPanel panTeamHeader = new JPanel(new FlowLayout());
        JLabel labTeam = new JLabel(TEAM[team]);

        butWin[team] = new JButton("WIN");
        butWin[team].addActionListener(new RecordWinButtonHandler(team));

        JPanel panMercenarySelect = new JPanel(new GridLayout(3,3));
        mercenarySelectPanels[team] = panMercenarySelect;
        for (int i=0; i<9; i++) {
            JButton butClassSelect = new JButton(Constants.MERCENARY[i]);
            butClassSelect.addActionListener(new ClassSelectButtonHandler(team, i));
            butClassSelect.setBackground(Color.WHITE);
            butClassSelect.setPreferredSize(new Dimension(120, 40));
            panMercenarySelect.add(butClassSelect);
        }
        panSelectedTeamInfo[team] = new JPanel(new GridLayout(2, 1));
        labGamesWon[team] = new JLabel("Won: N/A");

        panSelectedTeamInfo[team].add(labGamesWon[team]);

        if (team == BLU)
            panTeam.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        else
            panTeam.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        panTeam.add(panTeamHeader, BorderLayout.NORTH);
        panTeam.add(panMercenarySelect, BorderLayout.CENTER);
        panTeam.add(panSelectedTeamInfo[team], BorderLayout.SOUTH);

        panTeamHeader.add(labTeam);
        panTeamHeader.add(butWin[team]);

        panSelectedTeamInfo[team].add(labGamesWon[team]);
        return panTeam;
    }

    public static void refreshMapList() {
        MapDropdownSelectHandler.setMapBeingAdded(true);
        mapDropdownSelect.removeAllItems();
        List<String> mapNames = AppDataHandler.getMapNames();
        //mapDropdownSelect.addItem(OVERALL_MAP);
        for (Object mapName : mapNames.toArray())
            mapDropdownSelect.addItem(mapName.toString());
        MapDropdownSelectHandler.setMapBeingAdded(false);
        mapDropdownSelect.setSelectedItem(selectedMap);
    }

    static void refreshGrid() {
        panMercenaryGrid.removeAll();
        GameModeGrid grid;
        try {
//            if (selectedMap.equals(OVERALL_MAP) && selectedGameMode == -1) {
            if (displayOverallMap && displayOverallGameMode) {
                setWinButtonAvailability(false);
                grid = AppDataHandler.getOverallGrid();
            }
//            else if (selectedGameMode == -1) {
            else if (displayOverallGameMode) {
                setWinButtonAvailability(false);
                grid = AppDataHandler.getOverallGrid(selectedMap);
            }
//            else if (selectedMap.equals(OVERALL_MAP)) {
            else if (displayOverallMap) {
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
        for (JButton but : butWin)
            but.setEnabled(available);
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
        for (int panel=0; panel<mercenarySelectPanels.length; panel++) {
            int i = 0;
            for (Component component : mercenarySelectPanels[panel].getComponents())
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
//        setSelectedMap(OVERALL_MAP);
        setSelectedMap(null);
//        mapDropdownSelect.setSelectedItem(OVERALL_MAP);
    }

    public static void setDisplayOverallMap(boolean displayOverallMap) {
        Tracking.displayOverallMap = displayOverallMap;
        ToggleOverallMapButtonHandler.setDisplayOverall(displayOverallMap);
        refreshGamesPlayedLabels();
        refreshMercenarySelectGrid();
        refreshGamesPlayedLabels();
        if (displayOverallMap)
        {
            Tracking.butToggleOverallMap.setText(REVERT_TO_MAP_TEXT);
        }
        else {
            Tracking.butToggleOverallMap.setText(VIEW_OVERALL_TEXT);
        }
    }

    public static void setDisplayOverallGameMode(boolean displayOverallGameMode) {
        Tracking.displayOverallGameMode = displayOverallGameMode;
    }

    public static void setSelectedMap(String mapName) {
//        selectedMap = mapName;
        if (mapName != null){
            displayOverallMap = false;
            selectedMap = mapName;
            butToggleOverallMap.setText("");
        }
        else
            displayOverallMap = true;
        refreshGrid();
    }

    static String getSelectedMap() {
        return selectedMap;
    }

    static void setSelectedGameMode(int gameMode) {
//        selectedGameMode = gameMode;
        if (gameMode != -1) {
            displayOverallGameMode = false;
            selectedGameMode = gameMode;
        }
        else
            displayOverallGameMode = true;
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

    public static void refreshGamesPlayedLabels() {
        if (0 <= selectedMercenary[BLU] && selectedMercenary[BLU] < 9 && 0 <= selectedMercenary[RED] && selectedMercenary[RED] < 9) {
            try {
                // update AppDataHandler (displayOverallMap)
                int[] totalWins;
                if (displayOverallMap && displayOverallGameMode)
                    totalWins = AppDataHandler.getMatchupWins(selectedMercenary[BLU], selectedMercenary[RED]);
                else if (displayOverallMap && !displayOverallGameMode)
                    totalWins = AppDataHandler.getMatchupWins(selectedGameMode, selectedMercenary[BLU], selectedMercenary[RED]);
                else if (!displayOverallMap && displayOverallGameMode)
                    totalWins = AppDataHandler.getMatchupWins(selectedMap, selectedMercenary[BLU], selectedMercenary[RED]);
                else
                    totalWins = AppDataHandler.getMatchupWins(selectedMap, selectedGameMode, selectedMercenary[BLU], selectedMercenary[RED]);
                for (int i = 0; i < labGamesWon.length; i++) {
                    String gamesWonText = String.format("Won: %d", totalWins[i]);
                    labGamesWon[i].setText(gamesWonText);
                }
            } catch (MapNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        try {
            int totalGames;
            if (displayOverallMap && displayOverallGameMode) totalGames = AppDataHandler.getTotalGames();
            else if (displayOverallMap && !displayOverallGameMode) totalGames = AppDataHandler.getTotalGames(selectedGameMode);
            else if (!displayOverallMap && displayOverallGameMode) totalGames = AppDataHandler.getTotalGames(selectedMap);
            else totalGames = AppDataHandler.getTotalGames(selectedMap, selectedGameMode);
            labGamesPlayedTotal.setText(String.format("Total games recorded in this configuration: %d", totalGames));
        } catch (MapNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    // TODO: retain scroll position after refreshing
    public static void refreshServerBanner() {
        Document document = panServerBannerHTML.getDocument();
        document.putProperty(Document.StreamDescriptionProperty, null);  // Specify that the editor content is null
        try {
            JScrollBar scrollBar = ((JScrollPane)(panServerBannerHTML.getParent().getParent())).getVerticalScrollBar();
            int scrollValue = scrollBar.getValue();
            panServerBannerHTML.setPage(ServerDataRetrieval.getGameTrackerServerBannerIframe(
                    ServerDataRetrieval.SERVER_IP, ServerDataRetrieval.PORT, SERVER_BANNER_WIDTH));
            scrollBar.setValue(scrollValue);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void refreshAll() {
        refreshGrid();
        refreshMapList();
        refreshGamesPlayedLabels();
        refreshMercenarySelectGrid();
        refreshServerBanner();
    }
}
