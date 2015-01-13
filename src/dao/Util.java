package dao;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class Util {

    /*
     * Don't even try to.
     * This class contains only static utility methods.
     */
    private Util() {
        throw new UnsupportedOperationException("Uninstantiable class");
    }
    
    /*
     * Finds all the constraint violation messages in an exception, and
     * concatenates them into a single error message.
     */
    public static String buildErrorMessage(ConstraintViolationException ex) {
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation violation : ex.getConstraintViolations()) {
            if (message.length() != 0) {
                message.append(", ");
            }
            message.append(violation.getMessage());
        }
        message.append(".");
        return message.toString();
    }
}
