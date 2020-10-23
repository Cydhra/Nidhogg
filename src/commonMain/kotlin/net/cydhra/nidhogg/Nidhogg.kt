package net.cydhra.nidhogg

import io.ktor.client.HttpClient
import io.ktor.client.features.HttpResponseValidator
import io.ktor.client.features.UserAgent
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.readRemaining
import kotlinx.serialization.json.Json
import net.cydhra.nidhogg.exception.*

internal const val NIDHOGG_BRAND = "Nidhogg"
internal const val NIDHOGG_VERSION = "2.0.0"
internal const val NIDHOGG_USER_AGENT = "$NIDHOGG_BRAND/$NIDHOGG_VERSION"

/**
 * Generate an http client to use for any Mojang/Yggdrasil endpoints. Serialization strategy and header metadata is
 * configured accordingly.
 */
internal fun generateHttpClient(): HttpClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    install(UserAgent) {
        agent = NIDHOGG_USER_AGENT
    }

    HttpResponseValidator {
        validateResponse { response ->
            when (response.status) {
                HttpStatusCode.Forbidden -> {
                    val error =
                            Json.decodeFromString(ServerErrorResponse.serializer(),
                                    response.content.readRemaining().readText()
                            )
                    if (error.error == "ForbiddenOperationException") {
                        when {
                            error.cause == "UserMigratedException" -> {
                                throw UserMigratedException(error.description)
                            }
                            error.description.contains("username or password") -> {
                                throw InvalidCredentialsException(error.description)
                            }
                            error.description.contains("Invalid token") -> {
                                throw InvalidAccessTokenException(error.description)
                            }
                            error.description.contains("Invalid credentials") -> {
                                throw AuthenticationRefusedException(error.description)
                            }
                        }
                    } else {
                        throw RuntimeException("unexpected error from server: \"${error}\"")
                    }
                }
                HttpStatusCode.BadRequest -> {
                    val error =
                            Json.decodeFromString(ServerErrorResponse.serializer(),
                                    response.content.readRemaining().readText()
                            )
                    throw BadRequestException(error.description)
                }
            }
        }
    }
}