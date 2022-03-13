package TF2ClassWarsStatTracker.gui.tracking;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class GameModeSelectHandler implements ItemListener {
    private final int gameMode;
    GameModeSelectHandler(int gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED)
            TrackerWindow.instance.setSelectedGameMode(gameMode);
    }
}
