package net.cydhra.nidhogg.requests

/**
 * A request for session validation
 */
class ValidationRequest(val accessToken: String, val clientToken: String)