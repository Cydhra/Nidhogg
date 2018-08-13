@file:Suppress("unused")

package net.cydhra.nidhogg.requests

/**
 * A request for session validation
 */
internal class ValidationRequest(val accessToken: String, val clientToken: String)