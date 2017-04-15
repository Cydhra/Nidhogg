package net.cydhra.nidhogg.exception

/**
 * Thrown when trying to perform an action using a session as authentication that is not valid
 */
class InvalidSessionException(message: String) : RuntimeException(message)
