package net.cydhra.nidhogg.yggdrasil.requests

import kotlinx.serialization.Serializable
import net.cydhra.nidhogg.data.GameProfile
import net.cydhra.nidhogg.data.UserProfile

/**
 * Server response entity for refresh requests.
 *
 * @param accessToken the regenerated JWT token
 * @param clientToken the client token provided upon session creation and in the refresh request
 * @param selectedProfile Optional: the profile this session is intended for
 * @param user Optional: the [UserProfile] of the session's account. Only if requested
 */
@Serializable
internal data class RefreshResponse(
        val accessToken: String,
        val clientToken: String,
        val selectedProfile: GameProfile? = null,
        val user: UserProfile? = null
)