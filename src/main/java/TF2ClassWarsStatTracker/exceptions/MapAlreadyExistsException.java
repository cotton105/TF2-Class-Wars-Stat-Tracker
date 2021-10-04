package TF2ClassWarsStatTracker.exceptions;

public class MapAlreadyExistsException extends Exception {
    public MapAlreadyExistsException(String mapName) {
        super(String.format("Map with name \"%s\" already exists", mapName));
    }
}
