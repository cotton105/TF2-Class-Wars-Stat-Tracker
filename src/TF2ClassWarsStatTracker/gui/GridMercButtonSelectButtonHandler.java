package TF2ClassWarsStatTracker.gui;

import TF2ClassWarsStatTracker.util.Constants;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GridMercButtonSelectButtonHandler implements MouseListener {
    private final int blu, red;

    GridMercButtonSelectButtonHandler(int blu, int red) {
        this.blu = blu;
        this.red = red;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Tracking.setSelectedMercenary(Constants.BLU, blu);
        Tracking.setSelectedMercenary(Constants.RED, red);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
