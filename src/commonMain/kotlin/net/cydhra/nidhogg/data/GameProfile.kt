package net.cydhra.nidhogg.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A game profile defines data about a Mojang account regarding one specific Mojang game. Information within an
 * account may vary between profiles. [GameProfile]s are returned by the authentication if an
 * [net.cydhra.nidhogg.YggdrasilAgent] is provided.
 *
 * @param agent defines the [net.cydhra.nidhogg.YggdrasilAgent.name] that can be used to request
 * authentication for this profile. May be null, if the profile is the selected profile in an authentication response.
 * @param id uuid without dashes identifying the profile
 * @param userAlias the in-game user name of this game
 * @param userId a hexadecimal id for the user
 * @param createdAt a time stamp of when the profile was created
 * @param legacyProfile false, if the profile has been migrated from the legacy authentication system. Always present
 * @param suspended whether the account has been suspended
 * @param paid whether the game has been paid for (false if the profile was created for a demo)
 * @param legacy only present if true. Redundant to [legacyProfile]
 *
 * @see net.cydhra.nidhogg.YggdrasilClient.authenticate
 */
@Serializable
data class GameProfile(
        val agent: String? = null,
        val id: String,
        @SerialName("name") val userAlias: String,
        val userId: String,
        val createdAt: Long,
        val legacyProfile: Boolean,
        val suspended: Boolean,
        val paid: Boolean,
        val migrated: Boolean,
        val legacy: Boolean? = null
)