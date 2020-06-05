package net.cydhra.nidhogg.requests

import kotlinx.serialization.Serializable

/**
 * Request entity for the invalidate endpoint. Invalidates an [accessToken] using the [clientToken] previously used to
 * obtain it.
 */
@Serializable
internal data class InvalidateRequest(
        val accessToken: String,
        val clientToken: String
)