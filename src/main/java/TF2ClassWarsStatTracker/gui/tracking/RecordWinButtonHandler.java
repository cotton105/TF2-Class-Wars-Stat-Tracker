package TF2ClassWarsStatTracker.gui.tracking;

import TF2ClassWarsStatTracker.AppDataHandler;
import TF2ClassWarsStatTracker.exceptions.MapNotFoundException;
import TF2ClassWarsStatTracker.util.FileHandler;
import TF2ClassWarsStatTracker.util.Print;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static TF2ClassWarsStatTracker.AppDataHandler.updateActionHistory;

public class RecordWinButtonHandler implements ActionListener {
    private final int team;

    RecordWinButtonHandler(int team) {
        this.team = team;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String mapName = TrackerWindow.instance.getSelectedMap();
            int gameMode = TrackerWindow.instance.getSelectedGameMode();
            int bluMercenary = TrackerWindow.instance.getSelectedBluMercenary();
            int redMercenary = TrackerWindow.instance.getSelectedRedMercenary();
            AppDataHandler.incrementWins(mapName, gameMode, bluMercenary, redMercenary, team);
            FileHandler.writeToJSONFile(AppDataHandler.getMaps(), FileHandler.DEFAULT_MAPS_JSON);
            updateActionHistory(String.format("%s-%s-%d-%d-%d-%d",
                    AppDataHandler.RECORD_WIN, mapName, gameMode, bluMercenary, redMercenary, team));
        } catch (MapNotFoundException | IndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(TrackerWindow.instance, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            Print.error(ex.getMessage());
        }
        TrackerWindow.instance.refreshGamesPlayedLabels();
        TrackerWindow.instance.refreshBiasGrid();
    }
}
