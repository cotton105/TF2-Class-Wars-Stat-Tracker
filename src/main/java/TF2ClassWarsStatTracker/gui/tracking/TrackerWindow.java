package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.AppDataHandler;
import TF2ClassWarsStatTracker.ServerDataRetrieval;
import TF2ClassWarsStatTracker.exceptions.MapNotFoundException;
import TF2ClassWarsStatTracker.game.GameModeGrid;
import TF2ClassWarsStatTracker.gui.BiasGridElement;
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

public class TrackerWindow extends TrackingGUIJPanel {
    public static TrackerWindow instance;
    public static final String
            VIEW_OVERALL_TEXT = "View Overall",
            REVERT_TO_MAP_TEXT = "Revert to Map";
    private static final int SERVER_BANNER_WIDTH = 263;
    private final int[] selectedMercenary = new int[] {-1, -1};
    private final JLabel[] labGamesWon = new JLabel[2];
    private final JPanel[]
            panSelectedTeamInfo = new JPanel[2],
            mercenarySelectPanels = new JPanel[2];
    private final JButton[] butWin = new JButton[2];
    private JLabel labGamesPlayedTotal;
    private JPanel panMercenaryBiasGrid;
    private JComboBox<String> mapDropdownSelect;
    private String selectedServer, selectedMap;
    private int selectedGameMode;
    private JComponent[][] mercenaryBiasGrid;
    private JEditorPane panServerBannerHTML;
    private boolean displayOverallMap, displayOverallGameMode;
    public JButton butToggleOverallMap;

    private TrackerWindow() {
        super(new BorderLayout());

        JPanel panMenuBar = createMenuBar();
        JPanel panLeft = createPanLeft();
        JPanel panRight = createPanRight();

        displayOverallMap = true;       // Start by viewing overall map
        displayOverallGameMode = true;  // Start by viewing overall game modes
        selectedGameMode = OVERALL;

        setBorder(BorderFactory.createEmptyBorder(0,20,20,20));

        add(panMenuBar, BorderLayout.NORTH);
        add(panLeft, BorderLayout.WEST);
        add(panRight, BorderLayout.CENTER);

        refreshBiasGrid();
        refreshGamesPlayedLabels();
        refreshServerBanner();
    }

    private JPanel createMenuBar() {
        JPanel panMenuBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton butBack = new JButton("Back");
        butBack.addActionListener(new GeneralButtonHandler(GeneralButtonHandler.BACK));

        panMenuBar.add(butBack);
        setDefaultFont(panMenuBar, TF2secondary.deriveFont(16f));
        return panMenuBar;
    }

    private JPanel createPanLeft() {
        JPanel panLeft = new JPanel(new BorderLayout());
        panLeft.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        panLeft.add(createPanServerBannerScroll(), BorderLayout.CENTER);

        JPanel panBluVsRed = createPanBluVsRed();
        panLeft.add(panBluVsRed, BorderLayout.SOUTH);

        return panLeft;
    }

    private JScrollPane createPanServerBannerScroll() {
        initialiseServerBanner();
        return new JScrollPane(panServerBannerHTML);
    }

    private JPanel createPanBluVsRed() {
        JPanel panBluVsRed = new JPanel(new BorderLayout());
        panBluVsRed.add(createClassSelectGrid(BLU), BorderLayout.NORTH);
        panBluVsRed.add(createClassSelectGrid(RED), BorderLayout.SOUTH);
        setDefaultFont(panBluVsRed, TF2secondary.deriveFont(16f));

        return panBluVsRed;
    }

    // TODO: Continue refactoring this method - it's too big.
    private JPanel createClassSelectGrid(int team) {
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
        setDefaultFont(panSelectedTeamInfo[team], TF2secondary.deriveFont(20f));
        return panTeam;
    }

    private JPanel createPanRight() {
        JPanel panRight = new JPanel(new BorderLayout());
        panRight.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        panRight.add(createPanSelectedGameInfo(), BorderLayout.NORTH);

        panMercenaryBiasGrid = new JPanel(new GridLayout(10, 10, -1, -1));
        initialiseBiasGrid();
        panRight.add(panMercenaryBiasGrid, BorderLayout.CENTER);

        return panRight;
    }

    private JPanel createPanSelectedGameInfo() {
        JPanel panSelectedGameInfo = new JPanel(new GridLayout(3, 1));

        panSelectedGameInfo.add(createPanSelectedMapInfo());
        panSelectedGameInfo.add(createPanGameModeSelect());
        labGamesPlayedTotal = new JLabel();
        panSelectedGameInfo.add(labGamesPlayedTotal);

        setDefaultFont(panSelectedGameInfo, TF2secondary.deriveFont(16f));
        return panSelectedGameInfo;
    }

    // TODO: This method can probably be refactored to make it more readable.
    private JPanel createPanSelectedMapInfo() {
        JPanel panSelectedMapInfo = new JPanel(new FlowLayout());

        selectedMap = AppDataHandler.getMaps().get(0).getMapName();
        mapDropdownSelect = new JComboBox<>();
        refreshMapList();
        mapDropdownSelect.addItemListener(new MapDropdownSelectHandler());

        panSelectedMapInfo.add(mapDropdownSelect);

        butToggleOverallMap = new JButton(REVERT_TO_MAP_TEXT);
        try {
            butToggleOverallMap.addActionListener(new ToggleOverallMapButtonHandler(ToggleOverallMapButtonHandler.VIEW_OVERALL));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        panSelectedMapInfo.add(butToggleOverallMap);

        return panSelectedMapInfo;
    }

    /* TODO:
     *  When clicking a new game mode, the itemStateChanged event is called twice - the first time
     *  for the "overall" grid, and once more for the actual selected grid.
     */
    private JPanel createPanGameModeSelect() {
        JPanel panGameModeSelect = new JPanel(new GridLayout(1, 5));
        ButtonGroup gameModeSelectGroup = new ButtonGroup();
        for (int i=0; i<6; i++) {
            JRadioButton radGameMode = new JRadioButton(String.format("<html>%s</html>", GAME_MODES[i]), i == selectedGameMode);
            radGameMode.addItemListener(new GameModeSelectHandler(i - 1));
            radGameMode.setPreferredSize(new Dimension(150, 50));
            gameModeSelectGroup.add(radGameMode);
            panGameModeSelect.add(radGameMode);
        }
        return panGameModeSelect;
    }

    public static void initialise() {
        instance = new TrackerWindow();
    }

    private void initialiseServerBanner() {
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

    private void setWinButtonAvailability(boolean available) {
        for (JButton but : butWin)
            but.setEnabled(available);
    }

    private void initialiseBiasGrid() {
        mercenaryBiasGrid = new JComponent[10][10];
        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 10; column++) {
                JComponent gridElement = getBiasGridElement(row, column);
                mercenaryBiasGrid[column][row] = gridElement;
                panMercenaryBiasGrid.add(gridElement);
            }
        }
    }

    private JComponent getBiasGridElement(int row, int column) {
        JComponent gridElement;
        float fontSize = 16f;
        if (row == 0 || column == 0) {
            gridElement = new JLabel();
            ((JLabel) gridElement).setHorizontalAlignment(JLabel.CENTER);
            if (row == 0 && column > 0) {
                ((JLabel) gridElement).setText(MERCENARY[column - 1]);
                gridElement.setBackground(BLU_COLOUR);
            }
            else if (row > 0) {
                ((JLabel) gridElement).setText(MERCENARY[row - 1]);
                gridElement.setBackground(RED_COLOUR);
            }
        }
        else {
            gridElement = new BiasGridElement();
            gridElement.setBackground(Color.WHITE);
            ((JButton) gridElement).addActionListener(new GridMercButtonSelectButtonHandler(column - 1, row - 1));
            fontSize = 20f;
        }
        gridElement.setFont(TF2secondary.deriveFont(fontSize));
        gridElement.setOpaque(true);
        gridElement.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        gridElement.setPreferredSize(new Dimension(65, 65));
        return gridElement;
    }

    private void fillBiasGrid(GameModeGrid gridData) {
        unhighlightBiasGridElements();
        for (int row = 1; row <= 9; row++) {
            for (int column = 1; column <= 9; column++) {
                int[] matchupScores = gridData.getMatchupWins(column - 1, row - 1);
                updateBiasGridElement(matchupScores, row, column);
            }
        }
    }

    private void updateBiasGridElement(int[] matchupScores, int row, int column) {
        BiasGridElement gridElement = (BiasGridElement) mercenaryBiasGrid[column][row];
        float ratioBias = Calculate.getRatioBias(matchupScores[0], matchupScores[1]);
        String buttonStr;
        Color buttonColour = Color.WHITE;
        if (!Float.isNaN(ratioBias)) {
            buttonStr = String.format("%.2f", ratioBias);
            buttonColour = Calculate.getColourScaledFromWhite(ratioBias, BLU_COLOUR, RED_COLOUR);
        } else
            buttonStr = "";
        gridElement.setText(buttonStr);
        gridElement.setBackground(buttonColour);
        if (column - 1 == selectedMercenary[BLU] && row - 1 == selectedMercenary[RED])
            gridElement.setHighlighted(true);
    }

    private void unhighlightBiasGridElements() {
        for (int row = 1; row <= 9; row++)
            for (int column = 1; column <= 9; column++)
                if (((BiasGridElement) mercenaryBiasGrid[column][row]).isHighlighted())
                    ((BiasGridElement) mercenaryBiasGrid[column][row]).setHighlighted(false);
    }

    void setSelectedMercenary(int team, int mercenary) {
        if (team == BLU)
            selectedMercenary[BLU] = mercenary;
        else if (team == RED)
            selectedMercenary[RED] = mercenary;
        refreshMercenarySelectGrid();
    }

    void setSelectedMercenaries(int bluMercenary, int redMercenary) {
        selectedMercenary[BLU] = bluMercenary;
        selectedMercenary[RED] = redMercenary;
        refreshMercenarySelectGrid();
    }

    void viewOverall() {
        setSelectedMap(null);
    }

    void setDisplayOverallMap(boolean displayOverallMap) {
        this.displayOverallMap = displayOverallMap;
        ToggleOverallMapButtonHandler.setDisplayOverall(displayOverallMap);
        refreshGamesPlayedLabels();
        refreshMercenarySelectGrid();
        refreshGamesPlayedLabels();
        if (displayOverallMap)
            butToggleOverallMap.setText(REVERT_TO_MAP_TEXT);
        else
            butToggleOverallMap.setText(VIEW_OVERALL_TEXT);
    }

    public void setDisplayOverallGameMode(boolean displayOverallGameMode) {
        this.displayOverallGameMode = displayOverallGameMode;
    }

    public void setSelectedMap(String mapName) {
        if (mapName != null){
            displayOverallMap = false;
            selectedMap = mapName;
            butToggleOverallMap.setText("");
        }
        else
            displayOverallMap = true;
        refreshBiasGrid();
    }

    String getSelectedMap() {
        return selectedMap;
    }

    void setSelectedGameMode(int gameMode) {
        if (gameMode != -1) {
            displayOverallGameMode = false;
            selectedGameMode = gameMode;
        }
        else
            displayOverallGameMode = true;
        refreshBiasGrid();
        refreshGamesPlayedLabels();
    }

    int getSelectedGameMode() {
        return selectedGameMode;
    }

    int getSelectedBluMercenary() {
        return selectedMercenary[BLU];
    }

    int getSelectedRedMercenary() {
        return selectedMercenary[RED];
    }

    public void refreshAll() {
        refreshBiasGrid();
        refreshMapList();
        refreshGamesPlayedLabels();
        refreshMercenarySelectGrid();
        refreshServerBanner();
    }

    public void refreshBiasGrid() {
        GameModeGrid grid;
        try {
            if (displayOverallMap && displayOverallGameMode) {
                setWinButtonAvailability(false);
                grid = AppDataHandler.getOverallGrid();
            }
            else if (displayOverallGameMode) {
                setWinButtonAvailability(false);
                grid = AppDataHandler.getOverallGrid(selectedMap);
            }
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
        fillBiasGrid(grid);
        panMercenaryBiasGrid.revalidate();
    }

    public void refreshMapList() {
        MapDropdownSelectHandler.setMapBeingAdded(true);
        mapDropdownSelect.removeAllItems();
        List<String> mapNames = AppDataHandler.getMapNames();
        for (Object mapName : mapNames.toArray())
            mapDropdownSelect.addItem(mapName.toString());
        MapDropdownSelectHandler.setMapBeingAdded(false);
        mapDropdownSelect.setSelectedItem(selectedMap);
    }

    public void refreshGamesPlayedLabels() {
        if (0 <= selectedMercenary[BLU] && selectedMercenary[BLU] < 9 && 0 <= selectedMercenary[RED] && selectedMercenary[RED] < 9) {
            try {
                int[] totalWins;
                if (displayOverallMap && displayOverallGameMode)
                    totalWins = AppDataHandler.getMatchupWins(selectedMercenary[BLU], selectedMercenary[RED]);
                else if (displayOverallMap)
                    totalWins = AppDataHandler.getMatchupWins(selectedGameMode, selectedMercenary[BLU], selectedMercenary[RED]);
                else if (displayOverallGameMode)
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
            else if (displayOverallMap) totalGames = AppDataHandler.getTotalGames(selectedGameMode);
            else if (displayOverallGameMode) totalGames = AppDataHandler.getTotalGames(selectedMap);
            else totalGames = AppDataHandler.getTotalGames(selectedMap, selectedGameMode);
            labGamesPlayedTotal.setText(String.format("Total games recorded in this configuration: %d", totalGames));
        } catch (MapNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshMercenarySelectGrid() {
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

    // TODO: retain scroll position after refreshing
    public void refreshServerBanner() {
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
}
