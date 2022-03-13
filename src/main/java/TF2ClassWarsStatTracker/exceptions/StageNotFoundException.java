package TF2ClassWarsStatTracker.exceptions;

import java.sql.SQLException;

public class StageNotFoundException extends SQLException {
    public StageNotFoundException() {
        super();
    }

    public StageNotFoundException(int mapID, int stageNumber) {
        super(String.format("Stage with MapID \"%d\" and StageNumber \"%d\" was not found", mapID, stageNumber));
    }
}
