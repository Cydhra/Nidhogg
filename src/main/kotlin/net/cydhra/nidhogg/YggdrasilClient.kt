package net.cydhra.nidhogg

import net.cydhra.nidhogg.YggdrasilAgent.MINECRAFT
import net.cydhra.nidhogg.data.AccountCredentials
import net.cydhra.nidhogg.data.Session
import net.cydhra.nidhogg.exception.InvalidCredentialsException
import net.cydhra.nidhogg.exception.InvalidSessionException
import net.cydhra.nidhogg.exception.UserMigratedException
import net.cydhra.nidhogg.requests.*
import java.util.regex.Pattern

private const val YGGDRASIL_HOST_SERVER = "https://authserver.mojang.com"
private const val ENDPOINT_AUTHENTICATE = "/authenticate"
private const val ENDPOINT_REFRESH = "/refresh"
private const val ENDPOINT_VALIDATE = "/validate"
private const val ENDPOINT_SIGNOUT = "/signout"
private const val ENDPOINT_INVALIDATE = "/invalidate"

/**
 * A client for the Yggdrasil Authentication service. It wraps all endpoints of the service in functions and respective data classes.
 *
 * @param nidhoggClientToken the token used to identify this client at the Yggdrasil server. Can be chosen freely.
 */
class YggdrasilClient(private val nidhoggClientToken: String = DEFAULT_CLIENT_TOKEN) : NidhoggClient(nidhoggClientToken) {

    companion object {
        private val errorMessageRegex = Pattern.compile(
                "\\{\"error\":\\s*\"[a-zA-Z0-9\\s\\.\\,]*\",\\s*\"errorMessage\":\\s*\"[a-zA-Z0-9\\s\\.\\,]*\"(," + "\\s*\"cause\":\\s*\"[a-zA-Z0-9\\s]*\")*\\}")
    }

    /**
     * Log in with given account credentials at given service agent
     *
     * @param credentials Yggdrasil account credentials
     * @param agent       Yggdrasil account agent
     *
     * @return a Yggdrasil session object
     *
     * @throws UserMigratedException       if the account credentials used the player agentName but the account is migrated
     * @throws InvalidCredentialsException if the given credentials are invalid
     * @throws IllegalArgumentException    if the account credentials are partially empty
     */
    fun login(credentials: AccountCredentials, agent: YggdrasilAgent = YggdrasilAgent.MINECRAFT): Session {
        if (credentials.username == "" || credentials.password == "") {
            throw IllegalArgumentException("User credentials may not be empty")
        }

        val request = LoginRequest(
                Agent(agent.agentName, 1),
                credentials.username,
                credentials.password,
                this.nidhoggClientToken,
                true)

        val response = postRequest(YGGDRASIL_HOST_SERVER, ENDPOINT_AUTHENTICATE, this.gson.toJson(request)).getEntity(String::class.java)
        this.throwOnError(response)

        val authenticateResponse = this.gson.fromJson(response, AuthenticateResponse::class.java)
        return Session(authenticateResponse.selectedProfile!!.id, authenticateResponse.selectedProfile.name, authenticateResponse.accessToken,
                authenticateResponse.clientToken)
    }

    /**
     * Validate a Yggdrasil session.
     *
     * @param session A session with access and client token
     *
     * @return true, if the session is still valid. This method does not return false, but throws an appropriate exception
     *
     * @throws IllegalArgumentException if the given session has an empty access token
     * @throws InvalidSessionException  if the given session is invalid
     */
    fun validate(session: Session): Boolean {
        if (session.accessToken == "") {
            throw IllegalArgumentException("Access token may not be empty")
        }

        val response = postRequest(YGGDRASIL_HOST_SERVER, ENDPOINT_VALIDATE,
                this.gson.toJson(ValidationRequest(session.accessToken, session.clientToken)))

        if (response.hasEntity() && response.status != 204 /* success, no content */) {
            this.throwOnError(response.getEntity(String::class.java))
        }
        // session is valid
        return true
    }

    /**
     * Refreshes a session at Yggdrasil
     *
     * @param session a Yggdrasil session with valid access token
     *
     * @throws IllegalArgumentException if the given session has an empty access token
     * @throws InvalidSessionException  if the given session is already invalidated / was never valid
     */
    fun refresh(session: Session) {
        if (session.accessToken == "") {
            throw IllegalArgumentException("Access token may not be empty")
        }

        val response = postRequest(YGGDRASIL_HOST_SERVER, ENDPOINT_REFRESH,
                this.gson.toJson(RefreshRequest(session.accessToken, session.clientToken, true))).getEntity(String::class.java)

        this.throwOnError(response)

        // the refresh response is similar to authentication response, except that the available profiles are
        // missing.
        val refreshResponse = this.gson.fromJson<AuthenticateResponse>(response, AuthenticateResponse::class.java)

        // refresh the session with latest data
        session.accessToken = refreshResponse.accessToken
        session.clientToken = refreshResponse.clientToken
        session.alias = refreshResponse.selectedProfile!!.name
    }

    /**
     * Sign out from an Yggdrasil account (invalidate all sessions currently associated with the given account)

     * @param data account's credentials
     *
     * @throws IllegalArgumentException    if given credentials are partially empty
     * @throws UserMigratedException       if the account credentials used the player agentName but the account is migrated
     * @throws InvalidCredentialsException if the given credentials are invalid
     */
    fun signOut(data: AccountCredentials) {
        if (data.username == "" || data.password == "") {
            throw IllegalArgumentException("User Credentials may not be empty")
        }

        val response = postRequest(YGGDRASIL_HOST_SERVER, ENDPOINT_SIGNOUT,
                this.gson.toJson(SignOutRequest(data.username, data.password)))
        if (response.status != 204) this.throwOnError(response.getEntity(String::class.java))
    }

    /**
     * Invalidate a session. Yggdrasil will invalidate the access token so all sessions using it can no longer authenticate

     * @param session A so far valid [Session]
     *
     *
     * @throws IllegalArgumentException if the given session has an empty access token
     *
     * @throws InvalidSessionException  if the session was already invalid
     */
    fun invalidate(session: Session) {
        if (session.accessToken == "") {
            throw IllegalArgumentException("Access token may not be empty")
        }

        // invalidation and validation requests are exactly the same
        val response = postRequest(YGGDRASIL_HOST_SERVER, ENDPOINT_INVALIDATE,
                this.gson.toJson(ValidationRequest(session.accessToken, session.clientToken)))

        if (response.hasEntity() && response.status != 204 /* success, no content */) {
            this.throwOnError(response.getEntity(String::class.java))
        }
    }

    /**
     * If the response is of [ErrorResponse] type, an appropriate exception is thrown, otherwise, nothing happens

     * @param response serialized response from Yggdrasil REST service
     */
    private fun throwOnError(response: String) {
        if (!errorMessageRegex.matcher(response).find()) return

        val errorResponse = this.gson.fromJson<ErrorResponse>(response, ErrorResponse::class.java)

        // on user migrated error
        if (errorResponse.cause != null && errorResponse.cause == "UserMigratedException") {
            throw UserMigratedException("User account has been migrated. Login with username is not allowed.")
        }

        // on invalid credentials
        if (errorResponse.errorMessage == "Invalid credentials. Invalid username or password.") {
            throw InvalidCredentialsException("Provided account credentials where invalid.")
        }

        // access token invalid
        if (errorResponse.errorMessage == "Invalid token") {
            throw InvalidSessionException("Invalid access token provided")
        }

        // unknown or unexpected exception
        throw RuntimeException((errorResponse.error ?: "unknown error") + ":\n" +
                (errorResponse.errorMessage ?: "no description") + "\nCause: " +
                (errorResponse.cause ?: "no cause"))
    }
}

/**
 * Different Agents for Yggdrasil authentication service. Currently, only [MINECRAFT] is supported.
 */
enum class YggdrasilAgent(val agentName: String) {
    MINECRAFT("Minecraft");
}
