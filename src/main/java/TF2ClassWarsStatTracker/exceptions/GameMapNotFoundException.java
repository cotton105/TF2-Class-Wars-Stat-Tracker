package TF2ClassWarsStatTracker.exceptions;

public class GameMapNotFoundException extends Exception {

    public GameMapNotFoundException() {
        super();
    }

    public GameMapNotFoundException(String mapName) {
        super(String.format("Map with name \"%s\" was not found", mapName));
    }
}
