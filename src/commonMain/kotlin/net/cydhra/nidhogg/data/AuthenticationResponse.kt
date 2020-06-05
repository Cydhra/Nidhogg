@file:Suppress("ArrayInDataClass")

package net.cydhra.nidhogg.data

/**
 * A union of all fields that can be returned by authentication.
 */
data class AuthenticationResponse(
        val session: Session,
        val availableProfiles: Array<GameProfile>,
        val selectedProfile: GameProfile,
        val userProfile: UserProfile
)