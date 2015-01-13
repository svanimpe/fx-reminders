package exception;

/*
 * A general exception in the application. Every RemindersException also holds a
 * flag that indicates whether or not the application can recover from it.
 */
public class RemindersException extends Exception {

    private boolean recoverable;
    
    public RemindersException(String message, Throwable cause, boolean recoverable) {
        super(message, cause);
        this.recoverable = recoverable;
    }

    public boolean isRecoverable() {
        return recoverable;
    }
}
