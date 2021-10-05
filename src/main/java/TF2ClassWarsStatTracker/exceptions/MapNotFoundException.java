package TF2ClassWarsStatTracker.exceptions;

public class MapNotFoundException extends Exception {

    public MapNotFoundException() {
        super();
    }

    public MapNotFoundException(String mapName) {
        super(String.format("Map with name \"%s\" was not found", mapName));
    }
}
