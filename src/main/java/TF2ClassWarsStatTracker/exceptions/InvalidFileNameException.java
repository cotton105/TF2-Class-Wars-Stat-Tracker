package TF2ClassWarsStatTracker.exceptions;

public class InvalidFileNameException extends Exception {
    public InvalidFileNameException(String serverName) {
        super(String.format("\"%s\" is not a valid server name", serverName));
    }
}
