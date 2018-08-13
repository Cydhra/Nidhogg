package net.cydhra.nidhogg.exception

/**
 * Thrown by the Mojang API, if an operation is performed with insufficient permissions
 *
 * @param message error message by the API
 */
class UnauthorizedOperationException(message: String?) : RuntimeException(message)