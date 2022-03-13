package TF2ClassWarsStatTracker.exceptions;

import java.sql.SQLException;

public class GameModeNotFoundException extends SQLException {
    public GameModeNotFoundException(String gameModeName) {
        super(gameModeName);
    }
}
