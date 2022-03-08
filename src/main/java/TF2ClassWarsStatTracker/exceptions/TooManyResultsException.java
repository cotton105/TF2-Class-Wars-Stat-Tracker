package TF2ClassWarsStatTracker.exceptions;

import java.sql.SQLException;

public class TooManyResultsException extends SQLException {

    public TooManyResultsException() {
        super();
    }

    public TooManyResultsException(String s) {
        super(s);
    }
}
