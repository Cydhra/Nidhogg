@file:Suppress("ArrayInDataClass")

package net.cydhra.nidhogg.data

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.cydhra.nidhogg.util.decodeBase64

/**
 * The user profile associated to an account. It does not contain any data associated with Mojang games. See
 * [GameProfile] for that. The profile is returned by the Yggdrasil server during authentication, if requested. All
 * values except [id] and [username] may be null. I'm unaware under which conditions they are present.
 *
 * @param id the uuid of the user without hyphenation
 * @param email probably the email address of the user account. If the account is not migrated, this may contain a hash
 * value.
 * @param username the login name of the account. For migrated accounts this is the email address.
 * @param registerIp an IP address with the least significant byte censored. I am not exactly sure what IP address
 * this is supposed to be and why it is here. I'm also unsure when exactly it is present, and when it is null.
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
        val email: String? = null,
        val username: String,
        val registerIp: String? = null,
        val migratedFrom: String? = null,
        @SerialName("migratedAt") val migrationDate: Long? = null,
        @SerialName("registeredAt") val registrationDate: Long? = null,
        @SerialName("passwordChangedAt") val passwordChangeDate: Long? = null,
        val dateOfBirth: Long? = null,
        val suspended: Boolean? = null,
        val blocked: Boolean? = null,
        val secured: Boolean? = null,
        val migrated: Boolean? = null,
        val emailVerified: Boolean? = null,
        val legacyUser: Boolean? = null,
        val verifiedByParent: Boolean? = null,
        val properties: Array<ProfileProperty>? = null
) {
    /**
     * Generate an instance of [Uuid] from the player [id]. The [Uuid] instance is generated lazily and will be
     * stored afterwards.
     */
    val uuid: Uuid by lazy {
        uuidFrom("${id.subSequence(0, 8)}-${id.subSequence(8, 12)}-${id.subSequence(12, 16)}" +
                "-${id.subSequence(16, 20)}-${id.subSequence(20, 32)}"
        )
    }

    /**
     * The encoded texture data of the user profile. This will throw an exception if no texture data are present.
     * Usually texture data is only present, if the profile is requested through
     * [net.cydhra.nidhogg.mojang.MojangClient.getProfileByUUID]
     */
    val textures: TexturePropertyData by lazy {
        val json = Json(JsonConfiguration.Stable)
        json.fromJson(
                TexturePropertyData.serializer(),
                json.parseJson(properties!!.find { it.name == "textures" }!!.value.decodeBase64())
        )
    }

}

/**
 * Additional properties that can be assigned to [UserProfile]s as key-value-pairs. Typical properties include
 * "preferredLanguage" and "twitch_access_token". The latter contains an OAuth token for the user's twitch account.
 */
@Serializable
data class ProfileProperty(val name: String, val value: String)
