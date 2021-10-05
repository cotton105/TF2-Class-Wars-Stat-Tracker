package TF2ClassWarsStatTracker.gui.mainmenu;

import TF2ClassWarsStatTracker.gui.TrackingGUIJPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainMenu extends TrackingGUIJPanel {
    public MainMenu() {
        super(new GridLayout(4, 1));
        setBorder(new EmptyBorder(20,20,20,20));
        JLabel labMenu = new JLabel("Menu");
        labMenu.setHorizontalAlignment(JLabel.CENTER);

        JButton butTracker = new JButton("Tracker");
        butTracker.addActionListener(new MainMenuButtonHandler(MainMenuButtonHandler.TRACKER));
        JButton butStats = new JButton("View stats");
        butStats.addActionListener(new MainMenuButtonHandler(MainMenuButtonHandler.VIEW_STATS));
        JButton butExit = new JButton("Exit");
        butExit.addActionListener(new MainMenuButtonHandler(MainMenuButtonHandler.EXIT));

        add(labMenu);
        add(butTracker);
        add(butStats);
        add(butExit);

        setDefaultFont(this, TF2secondary.deriveFont(20f));
    }
}
