package net.cydhra.nidhogg.exception;

/**
 * Thrown when trying to perform an action using a session as authentication that is not valid
 */
public class InvalidSessionException extends RuntimeException {
    public InvalidSessionException(final String message) {
        super(message);
    }
}
