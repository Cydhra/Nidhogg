package net.cydhra.nidhogg.data

import java.io.Serializable

/**
 * A Yggdrasil session with an access token, that is used to validate the session and a client token, that is used to identify the client
 * that created the session
 *
 * @param id the player UUID
 * @param alias the player's character name
 * @param accessToken the session access token
 * @param clientToken the client token used to obtain the session
 */
data class Session(var id: String, var alias: String, var accessToken: String, var clientToken: String) : Serializable
