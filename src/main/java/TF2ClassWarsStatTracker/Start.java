package TF2ClassWarsStatTracker;

import TF2ClassWarsStatTracker.gui.MenuItemHandler;
import TF2ClassWarsStatTracker.gui.mainmenu.MainMenu;
import TF2ClassWarsStatTracker.gui.tracking.Tracking;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;

public class Start {
    private static final int WIDTH = 400, HEIGHT = 400;
    private static JFrame frame;
    private static final JComponent mainMenu = new MainMenu();
    private static final JComponent trackerScreen = new Tracking();
    private static ServerDataRetrieval serverDataRetrieval;

    public static void main(String[] args) {
        serverDataRetrieval = new ServerDataRetrieval();
        javax.swing.SwingUtilities.invokeLater(Start::initGUI);
    }

    private static void initGUI() {
        frame = new JFrame("TF2 Class Wars Stat Tracker");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initMenuBar();

        setActiveContentPane(mainMenu);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenuItem menuFileOptions = new JMenuItem("Options");
        menuFileOptions.addActionListener(new MenuItemHandler(MenuItemHandler.OPTIONS));
        JMenuItem menuFileExit = new JMenuItem("Exit");
        menuFileExit.addActionListener(new MenuItemHandler(MenuItemHandler.EXIT));

        JMenu menuEdit = new JMenu("Edit");
        JMenuItem menuEditUndo = new JMenuItem("Undo");
        menuEditUndo.addActionListener(new MenuItemHandler(MenuItemHandler.UNDO));

        JMenu menuServer = new JMenu("Server");
        JMenuItem menuServerNew = new JMenuItem("New server");
        menuServerNew.addActionListener(new MenuItemHandler(MenuItemHandler.NEW_SERVER));

        JMenu menuMaps = new JMenu("Maps");
        JMenuItem menuMapsNew = new JMenuItem("New map");
        menuMapsNew.addActionListener(new MenuItemHandler(MenuItemHandler.NEW_MAP));
        JMenuItem menuMapsRename = new JMenuItem("Rename map");
        menuMapsRename.addActionListener(new MenuItemHandler(MenuItemHandler.RENAME_MAP));
        JMenuItem menuMapsImport = new JMenuItem("Import maps data");
        menuMapsImport.addActionListener(new MenuItemHandler(MenuItemHandler.IMPORT_MAPS_DATA));

        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuServer);
        menuBar.add(menuMaps);

        menuFile.add(menuFileOptions);
        menuFile.add(menuFileExit);

        menuEdit.add(menuEditUndo);

        menuServer.add(menuServerNew);

        menuMaps.add(menuMapsNew);
        menuMaps.add(menuMapsRename);
        menuMaps.add(menuMapsImport);

        frame.setJMenuBar(menuBar);
    }

    public static JFrame getFrame() {
        return frame;
    }

    public static void setActiveContentPane(JComponent panel) {
        panel.setOpaque(true);
        frame.setContentPane(panel);
        frame.revalidate();
        frame.pack();
    }

    public static JComponent getMainMenu() {
        return mainMenu;
    }

    public static JComponent getTrackerScreen() {
        return trackerScreen;
    }

    public static void exit() {
        Print.print("Exiting tracker program...");
        frame.dispose();
    }

    public static ServerDataRetrieval getServerDataRetrieval() {
        return serverDataRetrieval;
    }
}
