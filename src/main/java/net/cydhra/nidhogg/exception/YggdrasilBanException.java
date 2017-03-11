package net.cydhra.nidhogg.exception;

/**
 * Thrown when attempting to log into Yggdrasil account but Yggdrasil rejects the connection because it banned the IP address.
 */
public class YggdrasilBanException extends RuntimeException {
    public YggdrasilBanException(final String message) {
        super(message);
    }
}
