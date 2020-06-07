package net.cydhra.nidhogg.exception

/**
 * Returned by Yggdrasil API server if too many authentication requests were sent in a too short time. The provided
 * credentials may still be valid though. When this exception raises, the user is banned from further authentication
 * attempts for a few minutes and every new authentication attempt will result in this exception.
 */
class AuthenticationRefusedException(message: String) : ForbiddenOperationException(message)