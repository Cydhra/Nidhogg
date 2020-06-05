package net.cydhra.nidhogg.requests

import kotlinx.serialization.Serializable
import net.cydhra.nidhogg.data.GameProfile
import net.cydhra.nidhogg.data.UserProfile

/**
 * One-to-one representation of the authentication response by Yggdrasil. This is not exposed to the user, but
 * restructured into an [net.cydhra.nidhogg.data.AuthenticationResponse]
 *
 * @param accessToken secret access token. Always present on success.
 * @param clientToken client token that was initially sent to Yggdrasil. Always present on success.
 * @param availableProfiles only present if an agent was sent to Yggdrasil. List of available [GameProfile]s
 * @param selectedProfile only present if an agent was sent to Yggdrasil. Represents the [GameProfile] selected by
 * the agent
 * @param user only present if requested. Holds the account's profile information
 */
@Serializable
internal data class AuthResponse(
        val accessToken: String,
        val clientToken: String,
        val availableProfiles: Array<GameProfile>? = null,
        val selectedProfile: GameProfile? = null,
        val user: UserProfile? = null
)