package TF2ClassWarsStatTracker;

import TF2ClassWarsStatTracker.gui.MainMenu;
import TF2ClassWarsStatTracker.gui.Tracking;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;

public class Start {
    private static final int WIDTH = 400, HEIGHT = 400;
    private static JFrame frame;
    private static final JComponent mainMenu = new MainMenu();
    private static final JComponent trackerScreen = new Tracking();

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(Start::initGUI);
    }

    private static void initGUI() {
        frame = new JFrame("TF2 Class Wars Stat Tracker");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

//        JComponent trackerWindow = new Tracking();
//        JComponent window = new MainMenu();
        setActiveContentPane(mainMenu);
        frame.setResizable(false);
        frame.setVisible(true);
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
        Print.timestamp("Exiting program...");
        frame.dispose();
    }
}
