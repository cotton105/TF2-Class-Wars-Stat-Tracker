package TF2ClassWarsStatTracker.exceptions;

public class InvalidTeamNumberException extends IllegalArgumentException {
    public InvalidTeamNumberException() {
        super("Team must be 0 or 1 (BLU/RED)");
    }
}
