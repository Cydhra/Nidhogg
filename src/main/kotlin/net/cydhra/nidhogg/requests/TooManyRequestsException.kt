package net.cydhra.nidhogg.requests

/**
 * Thrown by the API, if the rate limit is reached
 *
 * @param errorMessage the message of the API
 */
class TooManyRequestsException(val errorMessage: String?) : RuntimeException(errorMessage)