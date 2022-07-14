package TF2ClassWarsStatTracker;

import TF2ClassWarsStatTracker.gui.MenuItemHandler;
import TF2ClassWarsStatTracker.gui.mainmenu.MainMenu;
import TF2ClassWarsStatTracker.gui.tracking.StatWindow;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;

public class Start {
    private static final int WIDTH = 400, HEIGHT = 400;
    private static JFrame frame;
    private static final JComponent mainMenu = new MainMenu();
    private static final StatWindow trackerWindow = new StatWindow();
    private static ServerDataRetrieval serverDataRetrieval;

    public static void main(String[] args) {
//        serverDataRetrieval = new ServerDataRetrieval();
        javax.swing.SwingUtilities.invokeLater(Start::initGUI);
    }

    public static void viewTrackerWindow() {
        setActiveContentPane(trackerWindow.getPanMain());
    }

    private static void initGUI() {
        frame = new JFrame("TF2 Class Wars Stat Tracker");
        frame.setJMenuBar(generateMenuBar());
        generateMenuBar();
//        TrackerWindow.initialise();
        setActiveContentPane(mainMenu);

        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(getFileMenu());
        menuBar.add(getEditMenu());
        menuBar.add(getServerMenu());
        menuBar.add(getMapsMenu());

//        frame.setJMenuBar(menuBar);
        return menuBar;
    }

    private static JMenu getFileMenu() {
        JMenu menuFile = new JMenu("File");
        JMenuItem menuFileOptions = new JMenuItem("Options");
        menuFileOptions.addActionListener(new MenuItemHandler(MenuItemHandler.OPTIONS));
        JMenuItem menuFileExit = new JMenuItem("Exit");
        menuFileExit.addActionListener(new MenuItemHandler(MenuItemHandler.EXIT));

        menuFile.add(menuFileOptions);
        menuFile.add(menuFileExit);
        return menuFile;
    }

    private static JMenu getEditMenu() {
        JMenu menuEdit = new JMenu("Edit");
        JMenuItem menuEditUndo = new JMenuItem("Undo");
        menuEditUndo.addActionListener(new MenuItemHandler(MenuItemHandler.UNDO));

        menuEdit.add(menuEditUndo);
        return menuEdit;
    }

    private static JMenu getServerMenu() {
        JMenu menuServer = new JMenu("Server");
        JMenuItem menuServerNew = new JMenuItem("New server");
        menuServerNew.addActionListener(new MenuItemHandler(MenuItemHandler.NEW_SERVER));

        menuServer.add(menuServerNew);
        return menuServer;
    }

    private static JMenu getMapsMenu() {
        JMenu menuMaps = new JMenu("Maps");
        JMenuItem menuMapsNew = new JMenuItem("New map");
        menuMapsNew.addActionListener(new MenuItemHandler(MenuItemHandler.NEW_MAP));
        JMenuItem menuMapsRename = new JMenuItem("Rename map");
        menuMapsRename.addActionListener(new MenuItemHandler(MenuItemHandler.RENAME_MAP));
        JMenuItem menuMapsImport = new JMenuItem("Import maps data");
        menuMapsImport.addActionListener(new MenuItemHandler(MenuItemHandler.IMPORT_MAPS_DATA));

        menuMaps.add(menuMapsNew);
        menuMaps.add(menuMapsRename);
        menuMaps.add(menuMapsImport);
        return menuMaps;
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

    public static ServerDataRetrieval getServerDataRetrieval() {
        return serverDataRetrieval;
    }

    public static void exit() {
        Print.print("Exiting tracker program...");
        frame.dispose();
    }
}
