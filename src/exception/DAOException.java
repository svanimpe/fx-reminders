package exception;

/*
 * A more specific type indicating the exception is DAO-related.
 */
public class DAOException extends RemindersException {

    public DAOException(String message, Throwable cause, boolean recoverable) {
        super(message, cause, recoverable);
    }
}
