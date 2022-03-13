package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.AppDataHandler;
import TF2ClassWarsStatTracker.ServerDataRetrieval;
import TF2ClassWarsStatTracker.exceptions.InvalidTeamNumberException;
import TF2ClassWarsStatTracker.game.ConfigurationGrid;
import TF2ClassWarsStatTracker.gui.BiasGridElement;
import TF2ClassWarsStatTracker.gui.TrackingGUIJPanel;
import TF2ClassWarsStatTracker.util.Calculate;
import TF2ClassWarsStatTracker.util.Constants;
import TF2ClassWarsStatTracker.util.DBHandler;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.*;

public class TrackerWindow extends TrackingGUIJPanel {
    public static TrackerWindow instance;
    public static final String VIEW_OVERALL_TEXT = "View Overall";
    public static final String REVERT_TO_MAP_TEXT = "Revert to Map";
    private static final int SERVER_BANNER_WIDTH = 263;
    private final int[] selectedMercenary = new int[] {-1, -1};
    private final JLabel[] labGamesWon = new JLabel[2];
    private final JPanel[] panSelectedTeamInfo = new JPanel[2];
    private final JPanel[] mercenarySelectPanels = new JPanel[2];
    private final JButton[] butWin = new JButton[2];
    private JLabel labGamesPlayedTotal;
    private JPanel panMercenaryBiasGrid;
    private JComboBox<String> mapDropdownSelect;
    private String selectedMap;
    private int selectedGameMode;
    private int selectedStageNumber = 1;
    private BiasGridElement[][] mercenaryBiasGrid;
    private BiasGridElement[][] mercenaryBroadBiasGrid;

    private JEditorPane panServerBannerHTML;
    private boolean displayOverallMap;
    private boolean displayOverallGameMode;
    public JButton butToggleOverallMap;

    private TrackerWindow() {
        super(new BorderLayout());

        displayOverallMap = true;       // Start by viewing overall map
        displayOverallGameMode = true;  // Start by viewing overall game modes
        selectedGameMode = OVERALL;

        setBorder(BorderFactory.createEmptyBorder(0,20,20,20));

        add(createMenuBar(), BorderLayout.NORTH);
        add(createPanLeft(), BorderLayout.WEST);
        add(createPanRight(), BorderLayout.CENTER);

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
        panLeft.add(createPanBluVsRed(), BorderLayout.SOUTH);

        return panLeft;
    }

    private JScrollPane createPanServerBannerScroll() {
        initialiseServerBanner();
        return new JScrollPane(panServerBannerHTML);
    }

    private JPanel createPanBluVsRed() {
        JPanel panBluVsRed = new JPanel(new BorderLayout());

        panBluVsRed.add(createPanClassSelect(BLU), BorderLayout.NORTH);
        panBluVsRed.add(createPanClassSelect(RED), BorderLayout.SOUTH);

        return panBluVsRed;
    }

    private JPanel createPanClassSelect(int team) {
        if (team != 0 && team != 1) throw new InvalidTeamNumberException();
        JPanel panTeam = new JPanel(new BorderLayout());
        panTeam.setBorder(BorderFactory.createLineBorder((team == BLU ? Color.BLUE : Color.RED), 2));

        panTeam.add(createClassSelectTeamHeader(team), BorderLayout.NORTH);
        panTeam.add(createClassSelectGrid(team), BorderLayout.CENTER);
        setDefaultFont(panTeam, TF2secondary.deriveFont(16f));

        panSelectedTeamInfo[team] = createPanSelectedTeamInfo(team);
        panTeam.add(panSelectedTeamInfo[team], BorderLayout.SOUTH);

        return panTeam;
    }

    private JPanel createPanSelectedTeamInfo(int team) {
        JPanel panSelectedTeamInfo = new JPanel(new GridLayout(2, 1));

        labGamesWon[team] = new JLabel("Won: N/A");
        panSelectedTeamInfo.add(labGamesWon[team]);

        setDefaultFont(panSelectedTeamInfo, TF2secondary.deriveFont(20f));
        return panSelectedTeamInfo;
    }

    private JPanel createClassSelectTeamHeader(int team) {
        JPanel panTeamHeader = new JPanel(new FlowLayout());
        JLabel labTeam = new JLabel(TEAM[team]);

        butWin[team] = new JButton("WIN");
        butWin[team].addActionListener(new RecordWinButtonHandler(team));

        panTeamHeader.add(labTeam);
        panTeamHeader.add(butWin[team]);

        return panTeamHeader;
    }

    private JPanel createClassSelectGrid(int team) {
        JPanel panMercenarySelectGrid = new JPanel(new GridLayout(3,3));
        mercenarySelectPanels[team] = panMercenarySelectGrid;
        for (int i=0; i<9; i++) {
            JButton butClassSelect = new JButton(Constants.MERCENARY[i]);
            butClassSelect.addActionListener(new ClassSelectButtonHandler(team, i));
            butClassSelect.setBackground(Color.WHITE);
            butClassSelect.setPreferredSize(new Dimension(120, 40));
            panMercenarySelectGrid.add(butClassSelect);
        }
        return panMercenarySelectGrid;
    }

    private JPanel createPanRight() {
        JPanel panRight = new JPanel(new BorderLayout());
        panRight.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        panRight.add(createPanSelectedGameInfo(), BorderLayout.NORTH);
        panRight.add(createMercenaryBiasGridPan(), BorderLayout.CENTER);
        //panRight.add(createBroadBiasGridPan(), BorderLayout.SOUTH);

        return panRight;
    }

    private JPanel createMercenaryBiasGridPan() {
        panMercenaryBiasGrid = new JPanel(new GridLayout(10, 10, -1, -1));
        initialiseBiasGrid();
        return panMercenaryBiasGrid;
    }

    private JPanel createBroadBiasGridPan() {
        JPanel panBroadBiases = new JPanel(new BorderLayout());

        panBroadBiases.add(createBroadClassBiasPanel(), BorderLayout.WEST);
        //panBroadBiases.add(createBroadTeamBiasPanel(), BorderLayout.EAST);

        return panBroadBiases;
    }

    private JPanel createBroadClassBiasPanel() {
        JPanel panBroadClassBiasGrid = new JPanel(new GridLayout(4, 10));
        panBroadClassBiasGrid.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        mercenaryBroadBiasGrid = new BiasGridElement[9][3];

        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 10; column++) {
                BiasGridElement component = new BiasGridElement();
                component.setHorizontalAlignment(JLabel.CENTER);
                float fontSize = 16f;
                if (column == 0 || row == 0) {
//                    component = new BiasGridElement();
                    if (column != 0) {
                        component.setText(MERCENARY[column - 1]);
                        fontSize = 12f;
                    }
                    else if (row == 1 || row == 2) {
                        component.setText(TEAM[row-1]);
                        component.setBackground(row == 1 ? BLU_COLOUR : RED_COLOUR);
                    }
                    else if (row == 3) {
                        component.setText("Overall");
                    }
                }
                else {
                    mercenaryBroadBiasGrid[column - 1][row - 1] = component;
                }
                component.setFont(TF2secondary.deriveFont(fontSize));
                component.setOpaque(true);
                component.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                component.setPreferredSize(new Dimension(65, 40));
                panBroadClassBiasGrid.add(component);
            }
        }
        return panBroadClassBiasGrid;
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

    private void initialiseBiasGrid() {
        mercenaryBiasGrid = new BiasGridElement[9][9];
        for (int row = 0; row < 10; row++) {
            for (int column = 0; column < 10; column++) {
                JComponent gridElement = createBiasGridElement(row, column);
                if (column != 0 && row != 0)
                    mercenaryBiasGrid[column - 1][row - 1] = (BiasGridElement) gridElement;
                panMercenaryBiasGrid.add(gridElement);
            }
        }
    }

    private JComponent createBiasGridElement(int row, int column) {
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
        gridElement.setPreferredSize(new Dimension(65, 50));
        return gridElement;
    }

    // TODO: This method can probably be refactored to make it more readable.
    private JPanel createPanSelectedMapInfo() {
        JPanel panSelectedMapInfo = new JPanel(new FlowLayout());
////////////////////

//        selectedMap = AppDataHandler.getMaps().get(0).getMapName();
        try {
            selectedMap = DBHandler.Retrieve.getFirstAlphabeticalMapName();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
        JRadioButton radOverall = new JRadioButton("<html>Overall</html>", displayOverallGameMode);
        radOverall.addItemListener(new GameModeSelectHandler(-1));
        radOverall.setPreferredSize(new Dimension(150, 50));
        gameModeSelectGroup.add(radOverall);
        panGameModeSelect.add(radOverall);
        for (int i=0; i<GAME_MODES.length; i++) {
            JRadioButton radGameMode = new JRadioButton(String.format("<html>%s</html>", GAME_MODES[i]), i == selectedGameMode);
            radGameMode.addItemListener(new GameModeSelectHandler(i));
            radGameMode.setPreferredSize(new Dimension(150, 50));
            gameModeSelectGroup.add(radGameMode);
            panGameModeSelect.add(radGameMode);
        }
        return panGameModeSelect;
    }

    public static void initialise() {
        instance = new TrackerWindow();
    }

    // TODO: Fix inconsistent panel size for the server banner (sometimes window is small and others it's bigger)
    private void initialiseServerBanner() {
        panServerBannerHTML = new JEditorPane();
        panServerBannerHTML.setEditable(false);
        panServerBannerHTML.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        // TODO: Make links clickable
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
        for (JButton but : butWin) {
            but.setEnabled(available);
            but.setToolTipText(available ? null: "Invalid configuration");
        }
    }

    private void fillBiasGrid(ConfigurationGrid gridData) {
        unhighlightBiasGridElements();
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                int[] matchupScores = gridData.getMatchupWins(column, row);
                updateBiasGridElement(matchupScores, row, column);
            }
        }
    }

    private void updateBiasGridElement(int[] matchupScores, int row, int column) {
        BiasGridElement gridElement = mercenaryBiasGrid[column][row];
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
        if (column == selectedMercenary[BLU] && row == selectedMercenary[RED])
            gridElement.setHighlighted(true);
    }

//    private void updateBroadBiasGrid() {
//        try {
//            float[][] averages = AppDataHandler.getBroadMercenaryAverages(selectedMap, selectedGameMode);
//        } catch (MapNotFoundException ex) {
//            ex.printStackTrace();
//        }
//    }

    private void unhighlightBiasGridElements() {
        for (int row = 0; row < 9; row++)
            for (int column = 0; column < 9; column++)
                if ((mercenaryBiasGrid[column][row]).isHighlighted())
                    (mercenaryBiasGrid[column][row]).setHighlighted(false);
    }

    void setSelectedMercenary(int team, int mercenary) throws InvalidTeamNumberException {
        if (team != BLU && team != RED) throw new InvalidTeamNumberException();
        selectedMercenary[team] = mercenary;
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
        butToggleOverallMap.setText(displayOverallMap ? REVERT_TO_MAP_TEXT : VIEW_OVERALL_TEXT);
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
        ConfigurationGrid grid;
        try {
            if (displayOverallMap && displayOverallGameMode) {
                setWinButtonAvailability(false);
                grid = DBHandler.Retrieve.getOverallGrid();
            }
            else if (displayOverallGameMode) {
                setWinButtonAvailability(false);
                grid = DBHandler.Retrieve.getOverallGrid(selectedMap, selectedStageNumber);
            }
            else if (displayOverallMap) {
                setWinButtonAvailability(false);
                grid = DBHandler.Retrieve.getOverallGameModeGrid(GAME_MODES[selectedGameMode]);
            }
            else {
                if (selectedMercenary[BLU] != -1 && selectedMercenary[Constants.RED] != -1)
                    setWinButtonAvailability(true);
                grid = DBHandler.Retrieve.getMatchupGrid(selectedMap, selectedStageNumber, GAME_MODES[selectedGameMode]);
            }
        } catch (SQLException ex) {
            Print.error(ex.getMessage());
            ex.printStackTrace();
            grid = ConfigurationGrid.getEmptyGrid();
        }
        AppDataHandler.setLoadedConfiguration(grid);
        fillBiasGrid(grid);
        panMercenaryBiasGrid.revalidate();
    }

    public void refreshMapList() {
        try {
            MapDropdownSelectHandler.setMapBeingAdded(true);
            mapDropdownSelect.removeAllItems();
            List<String> mapNames = AppDataHandler.getMapNames();
            for (Object mapName : mapNames.toArray())
                mapDropdownSelect.addItem(mapName.toString());
            MapDropdownSelectHandler.setMapBeingAdded(false);
            mapDropdownSelect.setSelectedItem(selectedMap);
        }
        catch (SQLException ex) {
            Print.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void refreshGamesPlayedLabels() {
        if (0 <= selectedMercenary[BLU] && selectedMercenary[BLU] < 9 && 0 <= selectedMercenary[RED] && selectedMercenary[RED] < 9) {
            try {
                int[] totalWins;
                if (displayOverallMap && displayOverallGameMode)
                    totalWins = DBHandler.Retrieve.getMatchupWins(MERCENARY[selectedMercenary[BLU]], MERCENARY[selectedMercenary[RED]]);
                else if (displayOverallMap)
                    totalWins = DBHandler.Retrieve.getMatchupWins(MERCENARY[selectedMercenary[BLU]], MERCENARY[selectedMercenary[RED]], GAME_MODES[selectedGameMode]);
                else if (displayOverallGameMode)
                    totalWins = DBHandler.Retrieve.getMatchupWins(MERCENARY[selectedMercenary[BLU]], MERCENARY[selectedMercenary[RED]], selectedMap, selectedStageNumber);
                else
                    totalWins = DBHandler.Retrieve.getMatchupWins(MERCENARY[selectedMercenary[BLU]], MERCENARY[selectedMercenary[RED]], selectedMap, selectedStageNumber, GAME_MODES[selectedGameMode]);
                for (int i = 0; i < labGamesWon.length; i++) {
                    String gamesWonText = String.format("Won: %d", totalWins[i]);
                    labGamesWon[i].setText(gamesWonText);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            int totalGames;
            if (displayOverallMap && displayOverallGameMode)
                totalGames = DBHandler.Retrieve.getTotalGameCount();
            else if (displayOverallMap)
                totalGames = DBHandler.Retrieve.getTotalGameCount(GAME_MODES[selectedGameMode]);
            else if (displayOverallGameMode)
                totalGames = DBHandler.Retrieve.getTotalGameCount(selectedMap, selectedStageNumber);
            else
                totalGames = DBHandler.Retrieve.getTotalGameCount(selectedMap, selectedStageNumber, GAME_MODES[selectedGameMode]);
            labGamesPlayedTotal.setText(String.format("Total games recorded in this configuration: %d", totalGames));
        } catch (SQLException ex) {
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
