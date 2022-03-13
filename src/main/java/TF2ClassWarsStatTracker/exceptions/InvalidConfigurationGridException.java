package TF2ClassWarsStatTracker.exceptions;

import TF2ClassWarsStatTracker.game.ConfigurationGrid;

public class InvalidConfigurationGridException extends Exception {
    public InvalidConfigurationGridException(ConfigurationGrid grid) {
        System.out.printf("Map Name: %s Stage Number: %d Game Mode: %s",
                grid.getMapName(), grid.getStageNumber(), grid.getGameModeName());
    }
}
