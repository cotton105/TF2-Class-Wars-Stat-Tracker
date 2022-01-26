package TF2ClassWarsStatTracker.gui;

import TF2ClassWarsStatTracker.util.Calculate;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class BiasGridElement extends JButton {
    private Color unhighlightedBgColor;
    private Border unhighlightedBorder;
    private boolean highlighted;

    public BiasGridElement() {
        highlighted = false;
        unhighlightedBgColor = getBackground();
        unhighlightedBorder = getBorder();
    }

    @Override
    public void setBackground(Color bg) {
        unhighlightedBgColor = bg;
        super.setBackground(bg);
    }

    @Override
    public void setBorder(Border border) {
        unhighlightedBorder = border;
        super.setBorder(border);
    }

    public void setHighlighted(boolean highlight) {
        highlighted = highlight;
        if (highlighted) {
            super.setBackground(Calculate.getColourHighlight(unhighlightedBgColor, Color.YELLOW));
            super.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        }
        else {
            super.setBackground(unhighlightedBgColor);
            super.setBorder(unhighlightedBorder);
        }
    }

    public boolean isHighlighted() {
        return highlighted;
    }
}
