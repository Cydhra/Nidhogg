package net.cydhra.nidhogg.exception

/**
 * Thrown when attempting to log into a Yggdrasil account with invalid credentials
 */
class InvalidCredentialsException(message: String) : RuntimeException(message)
