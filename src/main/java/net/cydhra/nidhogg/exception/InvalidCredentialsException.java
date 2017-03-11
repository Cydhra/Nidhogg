package net.cydhra.nidhogg.exception;

/**
 * Thrown when attempting to log into a Yggdrasil account with invalid credentials
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(final String message) {
        super(message);
    }
}
