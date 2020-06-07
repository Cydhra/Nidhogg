package net.cydhra.nidhogg.mojang

import com.benasher44.uuid.Uuid
import com.soywiz.klock.DateTime
import io.ktor.client.call.receive
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.HttpStatement
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.utils.io.core.Closeable
import net.cydhra.nidhogg.data.*
import net.cydhra.nidhogg.generateHttpClient
import net.cydhra.nidhogg.mojang.requests.StatisticsRequest

private const val STATUS_API_URL = "https://status.mojang.com"
private const val MOJANG_API_URL = "https://api.mojang.com"
private const val SESSION_SERVER_URL = "https://sessionserver.mojang.com"

private const val STATUS_ENDPOINT = "/check"
private const val USER_TO_UUID_BY_TIME_ENDPOINT = "/users/profiles/minecraft/%s"
private const val BY_TIME_PARAMETER = "?at=%s"
private const val NAME_HISTORY_BY_UUID_ENDPOINT = "/user/profiles/%s/names"
private const val UUIDS_BY_NAMES_ENDPOINT = "/profiles/minecraft"
private const val PROFILE_BY_UUID_ENDPOINT = "/session/minecraft/profile/%s"
private const val SKIN_ENDPOINT = "/user/profile/%s/skin"
private const val LOCATION_ENDPOINT = "/user/security/location"
private const val CHALLENGES_ENDPOINT = "/user/security/challenges"
private const val BLOCKED_SERVERS_ENDPOINT = "/blockedservers"
private const val STATISTICS_ENDPOINT = "/orders/statistics"

class MojangClient() : Closeable {
    private val client = generateHttpClient()

    suspend fun checkStatus() {
        throw UnsupportedOperationException()
    }

    /**
     * Request an account's [Uuid] by its in-game username. Optionally, a [DateTime] instance can be provided to
     * specify when this particular username was associated with the account. If no time is provided, the server will
     * look up which account is currently assigned this username.
     *
     * @param name in-game username
     * @param time a timestamp specifying when the username was associated with the account in question
     *
     * @return an [UUIDEntry] from the Mojang account database containing the account's [Uuid] and some more meta
     * information
     */
    suspend fun getUUIDbyUsername(name: String, time: DateTime? = null): UUIDEntry? {
        var endpoint = USER_TO_UUID_BY_TIME_ENDPOINT.replace("%s", name)
        if (time == null) {
            endpoint += BY_TIME_PARAMETER.replace("%s", "0")
        } else {
            endpoint += BY_TIME_PARAMETER.replace("%s", time.unixMillisLong.toString())
        }

        val statement = client.get<HttpStatement>(MOJANG_API_URL + endpoint)
        val response = statement.execute()
        return when (response.status) {
            HttpStatusCode.NoContent -> null
            else -> response.receive()
        }
    }

    /**
     * Receive a list of names that were associated with the account specified by [uuid] and the time of change. The
     * first name does not have a time of change.
     *
     * @param uuid the account's [Uuid]
     *
     * @return a list of [NameHistoryEntry]
     */
    suspend fun getNameHistoryByUUID(uuid: Uuid): List<NameHistoryEntry> {
        val endpoint = NAME_HISTORY_BY_UUID_ENDPOINT.replace("%s", uuid.toString().replace("-", ""))
        return client.get(MOJANG_API_URL + endpoint)
    }

    /**
     * Receive a list of [Uuid]s, one for each entry in the given list of usernames. If any name does not match an
     * account, it will be missing in the result list. Other than that, order is preserved.
     *
     * @param names a list of usernames.
     *
     * @return a list of [UUIDEntries][UUIDEntry]
     *
     * @throws [IllegalArgumentException] if more than 100 names are requested at once
     * @throws [IllegalArgumentException] if any of the usernames is an empty string
     */
    suspend fun getUUIDsByNames(names: List<String>): List<UUIDEntry> {
        // the api also raises those errors but of course the input can already be validated here and then the
        // response can be expected to be valid
        if (names.size > 100)
            throw IllegalArgumentException("You cannot request more than 100 names per request")

        if (names.any { it == "" })
            throw IllegalArgumentException("profileName can not be null or empty.")

        return client.post(MOJANG_API_URL + UUIDS_BY_NAMES_ENDPOINT) {
            constructHeaders(this)
            body = names
        }
    }

    /**
     * @param uuid the uuid of an account
     *
     * @return a [UserProfile] instance
     */
    suspend fun getProfileByUUID(uuid: Uuid): SkinProfile {
        val endpoint = PROFILE_BY_UUID_ENDPOINT.replace("%s", uuid.toString().replace("-", ""))
        return client.get(SESSION_SERVER_URL + endpoint)
    }

    /**
     * Request whether this IP address is allowed to perform secured requests. Obtain this permission by answering
     * the security questions using [getSecurityChallenges] and [submitSecurityChallengeAnswers]
     *
     * @param session A valid Yggdrasil session, that has been authorized by [submitSecurityChallengeAnswers]
     *
     * @return true, if the IP address previously submitted the security answers
     */
    suspend fun isIpSecure(session: Session): Boolean {
        val response = client.get<HttpStatement>(MOJANG_API_URL + LOCATION_ENDPOINT) {
            header("Authorization", "Bearer ${session.accessToken}")
        }

        return response.execute().status == HttpStatusCode.NoContent
    }

    /**
     * Obtain the security questions associated with the account. The challenges come with ids, that must match the
     * ids of the answers submitted using [submitSecurityChallengeAnswers]
     *
     * @param session A valid Yggdrasil session of the account in question
     *
     * @return an array of exactly three security questions
     *
     * @see [submitSecurityChallengeAnswers]
     * @see [isIpSecure]
     */
    suspend fun getSecurityChallenges(session: Session): Array<SecurityChallenge> {
        return client.get(MOJANG_API_URL + CHALLENGES_ENDPOINT) {
            header("Authorization", "Bearer ${session.accessToken}")
        }
    }

    /**
     * Submit answers to the three security questions of Mojang to verify the validity of later API calls to secured endpoints. If all
     * answers were correct, the method returns normally. If one or more answers were incorrect, an [IllegalArgumentException] is thrown.
     *
     * @param session a valid session for the account whose challenges shall be solved
     * @param answers an array of exactly three [SecurityChallengeSolves][SecurityChallengeSolve] to the questions
     *
     * @throws IllegalArgumentException if more or less than three solves are submitted
     *
     * @return true, if the security challenges were correctly solved
     *
     * @see [getSecurityChallenges]
     * @see [isIpSecure]
     */
    suspend fun submitSecurityChallengeAnswers(session: Session, answers: List<SecurityChallengeSolve>): Boolean {
        if (answers.size != 3)
            throw IllegalArgumentException("The answers array must contain exactly three answers to the security challenges")

        val httpStatement = client.post<HttpStatement>(MOJANG_API_URL + LOCATION_ENDPOINT) {
            constructHeaders(this)
            header("Authorization", "Bearer ${session.accessToken}")
            body = answers
        }
        return httpStatement.execute().status == HttpStatusCode.NoContent
    }

    /**
     * Change the Minecraft skin of an account. This request is only valid, if a valid session is supplied and the IP
     * of the requesting client is secured.
     *
     * @param session a valid session for the account whose skin shall be changed
     * @param playerUuid the account's [Uuid]
     * @param source a URL to an internet address containing a valid skin image
     * @param slimModel true, if the skin is the slim model
     *
     * @return true, if the skin was accepted, false if the ip was not secured
     *
     * @see [isIpSecure]
     * @see [submitSecurityChallengeAnswers]
     */
    suspend fun changeSkin(session: Session, playerUuid: Uuid, source: Url, slimModel: Boolean = false): Boolean {
        val endpoint = SKIN_ENDPOINT.replace("%s", playerUuid.toString().replace("-", ""))
        val statement = client.post<HttpStatement>(MOJANG_API_URL + endpoint) {
            header("Authorization", "Bearer ${session.accessToken}")
            contentType(ContentType.MultiPart.FormData)

            body = MultiPartFormDataContent(formData {
                append("model", if (slimModel) "slim" else "")
                append("url", source.toString())
            })
        }

        return statement.execute().status == HttpStatusCode.OK
    }

    /**
     * Delete a skin of an account. This request is only valid, if a valid session is supplied and the IP of the
     * requesting client is secured.
     *
     * @param session the session of the account whose skin shall be deleted
     *
     * @see [isIpSecure]
     * @see [submitSecurityChallengeAnswers]
     */
    suspend fun resetSkin(session: Session, uuid: Uuid) {
        val endpoint = SKIN_ENDPOINT.replace("%s", uuid.toString().replace("-", ""))
        val statement = client.delete<HttpStatement>(MOJANG_API_URL + endpoint) {
            header("Authorization", "Bearer ${session.accessToken}")
        }
        statement.execute()
    }

    /**
     * Get a list of SHA1 hashes of server addresses blocked by the vanilla client. If the vanilla client tries to
     * connect to a server address, the address and each of its subdomains is hashed and matched against this list.
     * For example, if the client would try to connect to `ac.cydhra.net`, it would match against the hashes of
     * `ac.cydhra.net`, `*.cydhra.net` and `*.net`. If the hash is present in the list, the client will refuse
     * connection. This way Mojang enforces the EULA.
     */
    suspend fun getBlockedServers(): List<String> {
        return client.get<String>(SESSION_SERVER_URL + BLOCKED_SERVERS_ENDPOINT).split("\n").toList()
    }

    /**
     * Get sale metrics for a list of metric keys. The metrics are all summed up and returned as a [SaleMetrics]
     * instance.
     *
     * @param metrics a list of metric keys
     *
     * @return a sum of all sales of the given metrics
     *
     * @see MetricKeys
     */
    suspend fun getSaleStatistics(metrics: List<MetricKey>): SaleMetrics {
        return client.post(MOJANG_API_URL + STATISTICS_ENDPOINT) {
            constructHeaders(this)
            body = StatisticsRequest(metrics)
        }
    }

    private fun constructHeaders(builder: HttpRequestBuilder) {
        builder.contentType(ContentType.Application.Json)
    }

    override fun close() {
        this.client.close()
    }
}