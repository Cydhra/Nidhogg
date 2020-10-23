package net.cydhra.nidhogg.exception

/**
 * Thrown if the server rejects a request because of malformed parameters or syntax
 */
class BadRequestException(message: String) : RuntimeException(message)