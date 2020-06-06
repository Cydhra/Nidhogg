package net.cydhra.nidhogg.mojang

import com.benasher44.uuid.Uuid
import com.soywiz.klock.DateTime
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.utils.io.core.Closeable
import net.cydhra.nidhogg.data.*
import net.cydhra.nidhogg.generateHttpClient

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

class YggdrasilClient() : Closeable {
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
        val endpoint = USER_TO_UUID_BY_TIME_ENDPOINT.replace("%s", name)
        client.get<UUIDEntry>(MOJANG_API_URL + endpoint) {

        }

        TODO()
    }

    suspend fun getNameHistoryByUUID(uuid: Uuid): List<NameHistoryEntry> {
        TODO()
    }

    suspend fun getUUIDsByNames(names: List<String>): List<UUIDEntry> {
        TODO()
    }

    suspend fun getProfileByUUID(uuid: Uuid): UserProfile {
        TODO()
    }

    suspend fun isIpSecure(session: Session): Boolean {
        TODO()
    }

    suspend fun getSecurityChallenges(session: Session): Array<SecurityChallenge> {
        TODO()
    }

    suspend fun submitSecurityChallengeAnswers(session: Session, answers: Array<SecurityChallengeSolve>) {
        TODO()
    }

    suspend fun changeSkin(session: Session, source: Url, slimModel: Boolean = false) {
        TODO()
    }

    suspend fun resetSkin(session: Session) {
        TODO()
    }

    private fun constructHeaders(builder: HttpRequestBuilder) {
        builder.contentType(ContentType.Application.Json)
    }

    override fun close() {
        this.client.close()
    }
}