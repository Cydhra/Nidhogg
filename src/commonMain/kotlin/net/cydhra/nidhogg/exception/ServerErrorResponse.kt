package net.cydhra.nidhogg.exception

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The body of erroneous responses from the API server
 *
 * @param error the type of error as a human-readable description
 * @param description the error description that can be shown to the user
 * @param cause another type of error that was the cause for this error. Also a human-readable description
 */
@Serializable
internal data class ServerErrorResponse(
        val error: String,
        @SerialName("errorMessage") val description: String,
        val cause: String? = null
)