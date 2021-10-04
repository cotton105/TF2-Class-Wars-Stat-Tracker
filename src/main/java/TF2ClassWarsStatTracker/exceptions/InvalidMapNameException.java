package TF2ClassWarsStatTracker.exceptions;

public class InvalidMapNameException extends Exception {
    public InvalidMapNameException(String mapName) {
        super(String.format("\"%s\" is not a valid map name", mapName));
    }
}
