package net.cydhra.nidhogg.requests

import kotlinx.serialization.Serializable

/**
 * @param accessToken the session's access token to be validated
 * @param clientToken Optional: the client token used to obtain the session. This is sent by the Minecraft launcher,
 * however it seems to work without this token as well.
 */
@Serializable
internal data class ValidationRequest(
        val accessToken: String,
        val clientToken: String? = null
)