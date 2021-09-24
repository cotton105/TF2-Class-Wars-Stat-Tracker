package TF2ClassWarsStatTracker.gui;

import TF2ClassWarsStatTracker.util.FileHandler;
import TF2ClassWarsStatTracker.util.Print;
import TF2ClassWarsStatTracker.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class Tracking extends JPanel implements ActionListener {
    private static int selectedMap;
    private static JLabel labSelectedBluMerc, labSelectedRedMerc;

    public Tracking() {
        super(new BorderLayout());

        JPanel panMenuBar = new JPanel(new BorderLayout());
        JButton butBack = new JButton("Back");
        butBack.addActionListener(new MenuBarButtonHandler(MenuBarButtonHandler.MENU));

        JPanel panLeft = new JPanel(new BorderLayout());
        JPanel panRight = new JPanel(new BorderLayout());

        JPanel panSelectedMapInfo = new JPanel(new FlowLayout());

        ArrayList<String> maps = getMaps();
        JComboBox mapDropdownSelect = new JComboBox(maps.toArray());
        mapDropdownSelect.setSelectedIndex(0);
        mapDropdownSelect.addActionListener(this);

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

        createMercGrid(panRight);

        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panLeft.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panRight.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panBlu.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        panRed.setBorder(BorderFactory.createLineBorder(Color.RED));

        add(panMenuBar, BorderLayout.PAGE_START);
        panMenuBar.add(butBack, BorderLayout.WEST);
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
        add(panRight, BorderLayout.EAST);
    }

    private void createMercGrid(JComponent parent) {
        JPanel panMercGrid = new JPanel(new GridLayout(10,10));
        for (int i=0; i<10; i++) {
            for (int j=0; j<10; j++) {
                JComponent gridElement;
                if (i == 0 && j > 0) {  // BLU mercenaries (first row)
                    gridElement = new JLabel(Constants.MERCENARY[j-1]);
                } else if (i == 0) {
                    gridElement = new JLabel("RED \\ BLU");
                } else if (j == 0) {  // RED mercenaries (first column)
                    gridElement = new JLabel(Constants.MERCENARY[i-1]);
                } else {  // Add button to select relevant match-up on the left panel
                    gridElement = new JButton("X");
                    gridElement.addMouseListener(new GridMercButtonSelectButtonHandler(j-1, i-1));
                }
                gridElement.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridElement.setPreferredSize(new Dimension(65, 65));
                panMercGrid.add(gridElement);
            }
        }
        parent.add(panMercGrid, BorderLayout.CENTER);
    }

    private ArrayList<String> getMaps() {
        ArrayList<String> mapList = null;
        try {
            mapList = new ArrayList<>(FileHandler.readTextFileLines(FileHandler.MAPS));
            Print.commaSeparated(mapList, true);
        }
        catch (IOException ex) {
            Print.timestamp(String.format("ERR %s is missing: %s", FileHandler.MAPS, ex));
            ex.printStackTrace();
        }
        return mapList;
    }

    static void setSelectedMercenary(int team, int mercenary) {
        if (team == Constants.BLU)
            labSelectedBluMerc.setText(Constants.MERCENARY[mercenary]);
        else if (team == Constants.RED)
            labSelectedRedMerc.setText(Constants.MERCENARY[mercenary]);
    }

    public static void reloadStats() {
        Print.timestamp("Feature not implemented.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
