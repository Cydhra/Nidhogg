package net.cydhra.nidhogg.yggdrasil.requests

import kotlinx.serialization.Serializable
import net.cydhra.nidhogg.data.GameProfile

/**
 * @param accessToken session token that shall be refreshed
 * @param clientToken secret client token that was used to create the session
 * @param selectedProfile optionally the profile this session is intended for. However, sending it, will likely
 * result in an error, so just don't send it.
 * @param requestUser if true, the response will contain a [net.cydhra.nidhogg.data.UserProfile] instance for this
 * account
 */
@Serializable
internal data class RefreshRequest(
        val accessToken: String,
        val clientToken: String,
        val selectedProfile: GameProfile? = null,
        val requestUser: Boolean? = false
)