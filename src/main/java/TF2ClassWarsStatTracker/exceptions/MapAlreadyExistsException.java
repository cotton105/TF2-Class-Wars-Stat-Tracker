package TF2ClassWarsStatTracker.exceptions;

import java.sql.SQLException;

public class MapAlreadyExistsException extends SQLException {
    public MapAlreadyExistsException(String mapName) {
        super(String.format("Map with name \"%s\" already exists", mapName));
    }
}
