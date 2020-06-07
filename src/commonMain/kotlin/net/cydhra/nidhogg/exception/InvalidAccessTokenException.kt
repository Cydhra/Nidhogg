package net.cydhra.nidhogg.exception

/**
 * The provided access token was invalid. It is either invalidated, must be refreshed, or malformed.
 */
class InvalidAccessTokenException(message: String) : ForbiddenOperationException(message)