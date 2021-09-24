package TF2ClassWarsStatTracker.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainMenu extends JPanel {
    public MainMenu() {
        super(new GridLayout(4, 1));
        setBorder(new EmptyBorder(20,20,20,20));
        JLabel labMenu = new JLabel("Menu");

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
    }
}
