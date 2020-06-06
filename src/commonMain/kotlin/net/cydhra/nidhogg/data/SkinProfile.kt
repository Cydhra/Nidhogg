@file:Suppress("ArrayInDataClass")

package net.cydhra.nidhogg.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.cydhra.nidhogg.util.decodeBase64

/**
 * Response of a skin request. Holds encoded skin data in a property. Use [textures] to extract the
 * [TexturePropertyData] from the encoded property.
 */
@Serializable
data class SkinProfile(
        val id: String,
        val name: String,
        val properties: Array<SkinProperty>
) {

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
 * Optionally signed base64-encoded properties sent in [SkinProfile] objects.
 *
 * @param name usually "textures". Other properties may be added later
 * @param value base64-encoded json data
 * @param signature signature of [value] signed with Yggdrasil's private key
 */
@Serializable
data class SkinProperty(val name: String, val value: String, val signature: String? = null)
