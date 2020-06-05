@file:Suppress("ArrayInDataClass")

package net.cydhra.nidhogg.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The user profile associated to an account. It does not contain any data associated with Mojang games. See
 * [GameProfile] for that. The profile is returned by the Yggdrasil server during authentication, if requested.
 *
 * @param id the uuid of the user without hyphenation
 * @param email the email address of the user account. If the account is not migrated, this may contain a hash value.
 * @param username the login name of the account. For migrated accounts this is the email address.
 * @param registerIp an IP address with the least significant byte censored. I am not exactly sure what IP address
 * this is supposed to be and why it is here.
 * @param migratedFrom a string describing where this account has been migrated from (if anywhere). Usually this is
 * set to "minecraft.net", if the account has been migrated from the legacy authentication system.
 * @param migrationDate timestamp of when the account has been migrated
 * @param registrationDate timestamp of when the account was created
 * @param passwordChangeDate timestamp of the last password change
 * @param dateOfBirth date of birth set in the account settings as a timestamp
 * @param suspended whether the account has been suspended
 * @param blocked I have no idea what this is. It is usually false
 * @param secured I guess this is true, if security questions are available for account recovery. I am unsure due to
 * lacking documentation though
 * @param migrated I have no idea what this means, as it seems to be false always?
 * @param emailVerified whether the account's email address has been verified by a verification link
 * @param legacyUser Seems redundant?
 * @param verifiedByParent I ave no idea what this means.
 * @param properties dictionary of additional account properties. See [ProfileProperty] for more information.
 *
 * @see [net.cydhra.nidhogg.YggdrasilClient.authenticate]
 * @see [ProfileProperty]
 * @see [GameProfile]
 */
@Serializable
data class UserProfile(
        val id: String,
        val email: String,
        val username: String,
        val registerIp: String,
        val migratedFrom: String,
        @SerialName("migratedAt") val migrationDate: Long,
        @SerialName("registeredAt") val registrationDate: Long,
        @SerialName("passwordChangedAt") val passwordChangeDate: Long,
        val dateOfBirth: Long,
        val suspended: Boolean,
        val blocked: Boolean,
        val secured: Boolean,
        val migrated: Boolean,
        val emailVerified: Boolean,
        val legacyUser: Boolean,
        val verifiedByParent: Boolean,
        val properties: Array<ProfileProperty>
)

/**
 * Additional properties that can be assigned to [UserProfile]s as key-value-pairs. Typical properties include
 * "preferredLanguage" and "twitch_access_token". The latter contains an OAuth token for the user's twitch account.
 */
@Serializable
data class ProfileProperty(val name: String, val value: String)
