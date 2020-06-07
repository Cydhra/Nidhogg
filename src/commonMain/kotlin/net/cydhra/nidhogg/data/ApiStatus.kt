@file:Suppress("unused")

package net.cydhra.nidhogg.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The different status codes, a Mojang API can have
 */
@Serializable
enum class ApiStatus {
    /**
     * No problems with this API
     */
    @SerialName("green")
    GREEN,

    /**
     * The API has some issues, but is generally available
     */
    @SerialName("yellow")
    YELLOW,

    /**
     * The API is not available
     */
    @SerialName("red")
    RED
}