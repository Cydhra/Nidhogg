package net.cydhra.nidhogg.exception

/**
 * Returned by Yggdrasil if the credentials, that were used in an attempted login are wrong.
 */
class InvalidCredentialsException(message: String) : ForbiddenOperationException(message)