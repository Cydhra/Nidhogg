package net.cydhra.nidhogg.data

import com.google.gson.Gson
import java.util.*

/**
 * A set of data returned by the session server is asked for an account profile. Contains a property with skin textures.
 *
 * @param id profile id
 * @param name profile's player name
 * @param properties an array of properties containing data associated with this profile (like skin textures)
 */
data class Profile(val id: String, val name: String, val properties: Array<ProfileProperty>) {

    /**
     * Returns a UUID object containing the UUID defined by [id]. However, accessing this property two times, does not return the
     * identically equal object but two objects representing the same UUID.
     */
    val uuid: UUID
        get() = UUID.fromString("${id.subSequence(0, 8)}-${id.subSequence(8, 12)}-" +
                "${id.subSequence(12, 16)}-${id.subSequence(16, 20)}-${id.subSequence(20, 32)}"
        )

    /**
     * The [TexturePropertyData] associated with this profile
     */
    val textures: TexturePropertyData
        get() = Gson().fromJson(
                String(Base64.getDecoder().decode(properties.find { it.name == "textures" }!!.value)), TexturePropertyData::class.java
        )
}

