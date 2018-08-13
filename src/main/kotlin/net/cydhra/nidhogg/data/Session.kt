package net.cydhra.nidhogg.data

import java.io.Serializable
import java.util.*

/**
 * A Yggdrasil session with an access token, that is used to validate the session and a client token, that is used to identify the client
 * that created the session
 *
 * @param id the player UUID
 * @param alias the player's character name
 * @param accessToken the session access token
 * @param clientToken the client token used to obtain the session
 */
data class Session(var id: String, var alias: String, var accessToken: String, var clientToken: String) : Serializable {
    
    /**
     * Returns a UUID object containing the UUID defined by [id]. However, accessing this property two times, does not return the
     * identically equal object but two objects representing the same UUID.
     */
    val uuid: UUID
        get() = UUID.fromString("${id.subSequence(0, 8)}-${id.subSequence(8, 12)}-" +
                "${id.subSequence(12, 16)}-${id.subSequence(16, 20)}-${id.subSequence(20, 32)}"
        )
}
