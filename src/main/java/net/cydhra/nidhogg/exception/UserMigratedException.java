package net.cydhra.nidhogg.exception;

/**
 * Thrown when attempting to log into an Yggdrasil account that is migrated (requires login with e-mail) with the player name.
 */
public class UserMigratedException extends RuntimeException {
    public UserMigratedException(final String message) {
        super(message);
    }
}
