package net.cydhra.nidhogg.data

import java.io.Serializable

/**
 * A Yggdrasil session with an access token, that is used to validate the session and a client token, that is used to identify the client
 * that created the session
 */
data class Session(var alias: String, var accessToken: String, var clientToken: String) : Serializable