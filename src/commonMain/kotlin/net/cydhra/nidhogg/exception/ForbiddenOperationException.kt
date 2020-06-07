package net.cydhra.nidhogg.exception

/**
 * This operation at the server was forbidden, due to invalid credentials or otherwise missing authorization. More
 * specific exceptions are [UserMigratedException], [InvalidCredentialsException] and
 * [AuthenticationRefusedException] which extends from this class.
 *
 * @param message the message sent by the API server as the explanation of the exception
 */
open class ForbiddenOperationException(message: String) : RuntimeException(message)