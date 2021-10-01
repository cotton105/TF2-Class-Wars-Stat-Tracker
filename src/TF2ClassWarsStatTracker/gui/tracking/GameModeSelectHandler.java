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
        Tracking.setSelectedGameMode(gameMode);
        Tracking.updateGamesPlayedLabels();
    }
}
