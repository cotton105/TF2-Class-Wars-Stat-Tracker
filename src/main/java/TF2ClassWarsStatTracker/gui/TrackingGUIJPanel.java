package TF2ClassWarsStatTracker.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TrackingGUIJPanel extends JPanel {
    protected static Font TF2secondary;
    static {
        try {
            TF2secondary = Font.createFont(Font.TRUETYPE_FONT, new File("res\\fonts\\TF2secondary.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(TF2secondary);
        } catch (IOException | FontFormatException ex) {
            ex.printStackTrace();
        }
    }

    public TrackingGUIJPanel(LayoutManager layout) {
        super(layout);
    }

    protected void setDefaultFont(JComponent component, Font font) {
        for (Component c : component.getComponents())
            if (c instanceof JComponent) {
                c.setFont(font);
                setDefaultFont((JComponent)c, font);
            }
    }
}
