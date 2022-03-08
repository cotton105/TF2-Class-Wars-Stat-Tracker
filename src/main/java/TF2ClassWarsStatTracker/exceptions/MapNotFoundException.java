package TF2ClassWarsStatTracker.exceptions;

import java.sql.SQLException;

public class MapNotFoundException extends SQLException {

    public MapNotFoundException() {
        super();
    }

    public MapNotFoundException(String mapName) {
        super(String.format("Map with name \"%s\" was not found", mapName));
    }
}
