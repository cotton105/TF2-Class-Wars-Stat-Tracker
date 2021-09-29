package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.exceptions.GameMapNotFoundException;
import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.util.FileHandler;
import TF2ClassWarsStatTracker.util.Print;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RecordWinButtonHandler implements ActionListener {
    private final int team;

    RecordWinButtonHandler(int team) {
        this.team = team;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            GameMap.incrementWins(Tracking.getSelectedMap(), Tracking.getSelectedGameMode(),
                    Tracking.getSelectedRedMercenary(), Tracking.getSelectedBluMercenary(),
                    team);
            FileHandler.writeToJSONFile(GameMap.getMaps(), "res/maps.json");
        } catch (GameMapNotFoundException | IndexOutOfBoundsException ex) {
            Print.error(ex.getMessage());
        }
        Tracking.reloadGrid();
    }
}