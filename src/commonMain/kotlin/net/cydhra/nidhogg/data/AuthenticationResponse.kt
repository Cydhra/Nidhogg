@file:Suppress("ArrayInDataClass")

package net.cydhra.nidhogg.data

/**
 * A union of all fields that can be returned by authentication. Only [session] is always present (if the
 * authentication is successful), the other fields are only non-null if the right conditions are met.
 *
 * @param session the Yggdrasil session
 * @param availableProfiles a list of [GameProfile]s available at the account
 * @param selectedProfile the profile selected through the [agent][net.cydhra.nidhogg.YggdrasilAgent] during
 * authentication
 * @param userProfile the user profile data, if requested during authentication
 */
data class AuthenticationResponse(
        val session: Session,
        val availableProfiles: Array<GameProfile>?,
        val selectedProfile: GameProfile?,
        val userProfile: UserProfile?
)