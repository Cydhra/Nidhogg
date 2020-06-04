@file:Suppress("CanBeParameter", "unused")

package net.cydhra.nidhogg

import com.google.gson.reflect.TypeToken
import com.sun.jersey.core.util.MultivaluedMapImpl
import com.sun.jersey.multipart.FormDataBodyPart
import com.sun.jersey.multipart.FormDataMultiPart
import com.sun.jersey.multipart.file.FileDataBodyPart
import net.cydhra.nidhogg.data.*
import net.cydhra.nidhogg.exception.TooManyRequestsException
import net.cydhra.nidhogg.exception.UnauthorizedOperationException
import net.cydhra.nidhogg.requests.ErrorResponse
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.time.Instant
import java.util.*
import javax.ws.rs.core.MediaType

private const val STATUS_API_URL = "https://status.mojang.com"
private const val MOJANG_API_URL = "https://api.mojang.com"
private const val SESSION_SERVER_URL = "https://sessionserver.mojang.com"

private const val STATUS_ENDPOINT = "/check"
private const val USER_TO_UUID_BY_TIME_ENDPOINT = "/users/profiles/minecraft/%s"
private const val NAME_HISTORY_BY_UUID_ENDPOINT = "/user/profiles/%s/names"
private const val UUIDS_BY_NAMES_ENDPOINT = "/profiles/minecraft"
private const val PROFILE_BY_UUID_ENDPOINT = "/session/minecraft/profile/%s"
private const val SKIN_ENDPOINT = "/user/profile/%s/skin"
private const val LOCATION_ENDPOINT = "/user/security/location"
private const val CHALLENGES_ENDPOINT = "/user/security/challenges"

/**
 * A client for the Mojang API. It wraps the endpoints of the service in functions and respective data classes.
 */
class MojangClient() : NidhoggClient("Niddhog/1.4") {

    fun checkStatus() {
        throw UnsupportedOperationException()
    }

    /**
     * Get the UUID a user had at a given point in time.
     *
     * @param name username
     * @param time an [Instant] point in time, that is converted to a UNIX time stamp. If this parameter is null (which is default) the
     * current time is used. If this parameter is time stamp 0, the UUID of the first username this user had is returned. However, this
     * does only work, if the name was changed at least once or the account is legacy (not migrated).
     *
     * See also: <a href="http://wiki.vg/Mojang_API#Username_-.3E_UUID_at_time">Mojang API</a>
     * @return Optional UUID of given user at that time or empty optional, of no such user is found
     */
    fun getUUIDbyUsername(name: String, time: Instant? = null): UUIDEntry? {
        val endpoint = USER_TO_UUID_BY_TIME_ENDPOINT.format(name)

        val response = if (time != null)
            getRequest(MOJANG_API_URL, endpoint, queryParams = mapOf(("at" to time.epochSecond.toString())))
        else
            getRequest(MOJANG_API_URL, endpoint)

        return when {
            response.status == 204 -> null
            response.status == 200 -> gson.fromJson(response.getEntity(String::class.java), UUIDEntry::class.java)
            else -> {
                val error = gson.fromJson(response.getEntity(String::class.java), ErrorResponse::class.java)

                when {
                    error.error == "TooManyRequestsException" -> throw TooManyRequestsException(error.errorMessage)
                    else -> throw IllegalStateException("Unexpected exception: ${error.error}: ${error.errorMessage}")
                }
            }
        }
    }

    /**
     * Get the history of usernames associated with an account. The history is a sorted array beginning with the oldest entry.
     *
     * @param uuid the account's uuid
     *
     * @return an array of [NameEntries][NameEntry]
     */
    fun getNameHistoryByUUID(uuid: UUID): List<NameEntry> {
        val endpoint = NAME_HISTORY_BY_UUID_ENDPOINT.format(uuid.toString().replace("-", ""))
        val response = getRequest(MOJANG_API_URL, endpoint)

        if (response.status == 200)
            return gson.fromJson(response.getEntity(String::class.java), object : TypeToken<List<NameEntry>>() {}.type)
        else {
            val error = gson.fromJson(response.getEntity(String::class.java), ErrorResponse::class.java)

            when {
                error.error == "TooManyRequestsException" -> throw TooManyRequestsException(error.errorMessage)
                else -> throw IllegalStateException("Unexpected exception: ${error.error}: ${error.errorMessage}")
            }
        }
    }

    /**
     * Bulk request a list of UUIDs by a list of player names. Names that are unknown will not return anything and will not result in an
     * error. The maximum number of requested names is 100.
     *
     * @param names a list of at most 100 player names
     *
     * @return a list of at most 100 [UUIDEntries][UUIDEntry]. If any provided name is not associated with any account, no corresponding
     * UUID will be returned
     *
     * @throws IllegalArgumentException if more than 100 names are given in [names]
     * @throws IllegalArgumentException if one name is an empty string
     */
    fun getUUIDsByNames(names: List<String>): List<UUIDEntry> {
        // the api also raises those errors but of course the input can already be validated here and then the response can be expected to
        // be valid
        if (names.size > 100)
            throw IllegalArgumentException("You cannot request more than 100 names per request")

        if (names.any { it == "" })
            throw IllegalArgumentException("profileName can not be null or empty.")

        val response = postRequest(MOJANG_API_URL, UUIDS_BY_NAMES_ENDPOINT, gson.toJson(names))

        if (response.status == 200)
            return gson.fromJson(response.getEntity(String::class.java), object : TypeToken<List<UUIDEntry>>() {}.type)
        else {
            val error = gson.fromJson(response.getEntity(String::class.java), ErrorResponse::class.java)

            when {
                error.error == "TooManyRequestsException" -> throw TooManyRequestsException(error.errorMessage)
                else -> throw IllegalStateException("Unexpected exception: ${error.error}: ${error.errorMessage}")
            }
        }
    }

    /**
     * Get an account's [Profile] by its UUID. This API is rate limited to one request per minute per user, but there may be as many
     * requests for unique users as you like
     *
     * @param uuid account [UUID]
     *
     * @return the UUID's [Profile]
     *
     * @throws TooManyRequestsException if the rate limit is reached
     */
    fun getProfileByUUID(uuid: UUID): Profile {
        val response = getRequest(SESSION_SERVER_URL, PROFILE_BY_UUID_ENDPOINT.format(uuid.toString().replace("-", "")))

        if (response.status == 200)
            return gson.fromJson(response.getEntity(String::class.java), Profile::class.java)
        else {
            val error = gson.fromJson(response.getEntity(String::class.java), ErrorResponse::class.java)

            when {
                error.error == "TooManyRequestsException" -> throw TooManyRequestsException(error.errorMessage)
                else -> throw IllegalStateException("Unexpected exception: ${error.error}: ${error.errorMessage}")
            }
        }
    }

    /**
     * Check whether the current IP is considered secure by the Mojang API server. To secure the IP, the security questions must be solved.
     *
     * @param session the session whose security questions have been solved and for whom this IP could now be considered secure
     *
     * @return true, if the IP is considered secure
     *
     * @throws UnauthorizedOperationException if the session is invalid
     */
    fun isIpSecure(session: Session): Boolean {
        val response = getRequest(MOJANG_API_URL, LOCATION_ENDPOINT, header = mapOf(
                "Authorization" to "Bearer ${session.accessToken}"
        )
        )

        if (response.status == 204)
            return true
        else {
            val error = gson.fromJson(response.getEntity(String::class.java), ErrorResponse::class.java)

            when {
                error.error == "ForbiddenOperationException" && error.errorMessage == "Current IP is not secured" -> return false

                error.error == "TooManyRequestsException" -> throw TooManyRequestsException(error.errorMessage)
                error.error == "Unauthorized" -> throw UnauthorizedOperationException("${error.error}: ${error.errorMessage}")
                else -> throw IllegalStateException("Unexpected exception: ${error.error}: ${error.errorMessage}")
            }
        }
    }

    /**
     * Get an array of the three security questions forming the security challenge. Solving the security challenge is required to secure an
     * IP address to perform critical account actions, such as changing the skin.
     *
     * @param session a valid session of the account whose challenges are requested
     *
     * @return an array of three [SecurityChallenges][SecurityChallenge]
     *
     * @throws UnauthorizedOperationException if the session is invalid
     */
    fun getSecurityChallenges(session: Session): Array<SecurityChallenge> {
        val response = getRequest(MOJANG_API_URL, CHALLENGES_ENDPOINT, header = mapOf(
                "Authorization" to "Bearer ${session.accessToken}"
        )
        )

        if (response.status == 200) {
            return gson.fromJson(response.getEntity(String::class.java), Array<SecurityChallenge>::class.java)
        } else {
            val error = gson.fromJson(response.getEntity(String::class.java), ErrorResponse::class.java)

            when {
                error.error == "TooManyRequestsException" -> throw TooManyRequestsException(error.errorMessage)
                error.error == "Unauthorized" -> throw UnauthorizedOperationException("${error.error}: ${error.errorMessage}")
                else -> throw IllegalStateException("Unexpected exception: ${error.error}: ${error.errorMessage}")
            }
        }
    }

    /**
     * Submit answers to the three security questions of Mojang to verify the validity of later API calls to secured endpoints. If all
     * answers were correct, the method returns normally. If one or more answers were incorrect, an [IllegalArgumentException] is thrown.
     *
     * @param session a valid session for the account whose challenges shall be solved
     * @param answers an array of exactly three [SecurityChallengeSolves][SecurityChallengeSolve] to the questions
     *
     * @throws UnauthorizedOperationException if the session is invalid
     * @throws IllegalArgumentException if [answers] does not contain exactly three answers or if one or more answers where incorrect
     *
     * @see [getSecurityChallenges]
     * @see [isIpSecure]
     */
    fun submitSecurityChallengeAnswers(session: Session, answers: Array<SecurityChallengeSolve>) {
        if (answers.size != 3)
            throw IllegalArgumentException("The answers array must contain exactly three answers to the security challenges")

        val response = postRequest(MOJANG_API_URL, LOCATION_ENDPOINT,
                header = mapOf(
                        "Authorization" to "Bearer ${session.accessToken}"
                ),
                body = gson.toJson(answers)
        )

        if (response.status == 204) {
            return
        } else {
            val error = gson.fromJson(response.getEntity(String::class.java), ErrorResponse::class.java)

            when {
                error.error == "TooManyRequestsException" -> throw TooManyRequestsException(error.errorMessage)
                error.error == "Unauthorized" -> throw UnauthorizedOperationException("${error.error}: ${error.errorMessage}")
                error.error == "ForbiddenOperationException" -> throw IllegalArgumentException(error.errorMessage)
                else -> throw IllegalStateException("Unexpected exception: ${error.error}: ${error.errorMessage}")
            }
        }
    }

    /**
     * Change the Minecraft skin of an account. This request is only valid, if a valid session is supplied and the IP of the requesting
     * client is secured.
     *
     * @param session a valid session for the account whose skin shall be changed
     * @param source a URL to an internet address containing a valid skin image
     * @param slimModel true, if the skin is the slim model
     *
     * @throws UnauthorizedOperationException if the IP is not secured or the session is invalid
     *
     * @see [isIpSecure]
     * @see [submitSecurityChallengeAnswers]
     */
    fun changeSkin(session: Session, source: URL, slimModel: Boolean = false) {
        val header = mapOf(
                "Authorization" to "Bearer ${session.accessToken}"
        )
        val formData = MultivaluedMapImpl()
        formData.add("model", if (slimModel) "slim" else "")
        formData.add("url", source.toExternalForm())

        val response = postRequest(MOJANG_API_URL,
                SKIN_ENDPOINT.format(session.id),
                formData,
                MediaType.APPLICATION_FORM_URLENCODED_TYPE,
                header
        )

        if (response.status != 200) {
            val error = gson.fromJson(response.getEntity(String::class.java), ErrorResponse::class.java)

            when {
                error.error == "TooManyRequestsException" -> throw TooManyRequestsException(error.errorMessage)
                error.error == "Forbidden" -> throw UnauthorizedOperationException("${error.error}: ${error.errorMessage}")
                error.error == "Unauthorized" -> throw UnauthorizedOperationException("${error.error}: ${error.errorMessage}")
                else -> throw IllegalStateException("Unexpected exception: ${error.error}: ${error.errorMessage}")
            }
        }
    }

    /**
     * Upload a new skin for an account. This request is only valid, if a valid session is supplied and the IP of the requesting
     * client is secured.
     *
     * @param session a valid session for the account
     * @param file a PNG file with the new skin data
     * @param slim true, if the slim model shall be uploaded. False by default.
     *
     * @throws UnauthorizedOperationException if the session is invalid or the IP is not secure
     * @throws IllegalArgumentException if the file is not a valid image
     * @throws FileNotFoundException if [file] does not exist
     *
     * @see [isIpSecure]
     * @see [submitSecurityChallengeAnswers]
     */
    fun uploadSkin(session: Session, file: File, slim: Boolean = false) {
        if (!file.exists())
            throw FileNotFoundException("A valid skin image must be uploaded to change the skin")

        val response = putRequest(MOJANG_API_URL, SKIN_ENDPOINT.format(session.id),
                mediaType = MediaType.MULTIPART_FORM_DATA_TYPE,
                header = mapOf(
                        "Authorization" to "Bearer ${session.accessToken}"
                ),
                body = with(FormDataMultiPart()) {
                    this.bodyPart(FormDataBodyPart().also {
                        it.name = "model"
                        it.value = if (slim) "slim" else ""
                    })
                    this.bodyPart(FileDataBodyPart("file", file))
                })

        if (response.status == 204) {
            return
        } else {
            val error = gson.fromJson(response.getEntity(String::class.java), ErrorResponse::class.java)

            when {
                error.error == "TooManyRequestsException" -> throw TooManyRequestsException(error.errorMessage)
                error.error == "Unauthorized" -> throw UnauthorizedOperationException("${error.error}: ${error.errorMessage}")
                error.error == "IllegalArgumentException" -> throw IllegalArgumentException(error.errorMessage)
                else -> throw IllegalStateException("Unexpected exception: ${error.error}: ${error.errorMessage}")
            }
        }
    }

    /**
     * Delete a skin of an account. This request is only valid, if a valid session is supplied and the IP of the requesting
     * client is secured.
     *
     * @param session the session of the account whose skin shall be deleted
     *
     * @throws UnauthorizedOperationException if the session is invalid or the IP is not secure
     *
     * @see [isIpSecure]
     * @see [submitSecurityChallengeAnswers]
     */
    fun resetSkin(session: Session) {
        val response = deleteRequest(MOJANG_API_URL, SKIN_ENDPOINT.format(session.id), header = mapOf(
                "Authorization" to "Bearer ${session.accessToken}"
        )
        )

        if (response.status == 204) {
            return
        } else {
            val error = gson.fromJson(response.getEntity(String::class.java), ErrorResponse::class.java)

            when {
                error.error == "TooManyRequestsException" -> throw TooManyRequestsException(error.errorMessage)
                error.error == "Unauthorized" -> throw UnauthorizedOperationException("${error.error}: ${error.errorMessage}")
                else -> throw IllegalStateException("Unexpected exception: ${error.error}: ${error.errorMessage}")
            }
        }
    }
}