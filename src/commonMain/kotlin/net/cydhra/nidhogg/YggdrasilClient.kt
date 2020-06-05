package net.cydhra.nidhogg

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import io.ktor.client.HttpClient
import io.ktor.client.features.UserAgent
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.core.Closeable
import net.cydhra.nidhogg.data.AccountCredentials
import net.cydhra.nidhogg.data.AuthenticationResponse
import net.cydhra.nidhogg.data.Session
import net.cydhra.nidhogg.requests.AuthRequest
import net.cydhra.nidhogg.requests.AuthResponse

private const val YGGDRASIL_HOST_SERVER = "https://authserver.mojang.com"
private const val ENDPOINT_AUTHENTICATE = "/authenticate"
private const val ENDPOINT_REFRESH = "/refresh"
private const val ENDPOINT_VALIDATE = "/validate"
private const val ENDPOINT_SIGNOUT = "/signout"
private const val ENDPOINT_INVALIDATE = "/invalidate"

/**
 * A client to the Yggdrasil authentication API by Mojang.
 *
 * @param a secret client token used to refresh sessions. Defaults to a random [Uuid]. The client token used during
 * authentication is exposed in the generated [Session] and should be stored by the client for later use.
 */
class YggdrasilClient(private val clientToken: String = uuid4().toString()) : Closeable {
    private val client = HttpClient() {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(UserAgent) {
            agent = NIDHOGG_USER_AGENT
        }
    }

    /**
     * Authenticate at Yggdrasil for a given service using a pair of username and password.
     *
     * @param credentials username and password for authentication
     * @param agent a [YggdrasilAgent] instance indicating for which service the authentication is intended.
     * @param requestProfile if true, the Yggdrasil service will respond with the user's profile information
     *
     * @return a session instance on success
     *
     * @see [MinecraftAgent]
     * @see [ScrollsAgent]
     */
    suspend fun authenticate(
            credentials: AccountCredentials,
            agent: YggdrasilAgent?,
            requestProfile: Boolean
    ): AuthenticationResponse {
        val response = client.post<AuthResponse>("$YGGDRASIL_HOST_SERVER$ENDPOINT_AUTHENTICATE") {
            constructHeaders(this)
            body = AuthRequest(
                    agent,
                    credentials.username,
                    credentials.password,
                    clientToken,
                    requestProfile
            )
        }

        return AuthenticationResponse(
                session = Session(response.accessToken, response.clientToken),
                availableProfiles = response.availableProfiles,
                selectedProfile = response.selectedProfile,
                userProfile = response.user
        )
    }

    fun refresh() {
        TODO()
    }

    /**
     * Set default headers and settings for any http requests performed with this client.
     *
     * @param builder the builder for the request to be configured
     */
    private fun constructHeaders(builder: HttpRequestBuilder) {
        builder.contentType(ContentType.Application.Json)
    }

    override fun close() {
        this.client.close()
    }
}