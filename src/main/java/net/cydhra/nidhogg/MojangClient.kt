@file:Suppress("CanBeParameter", "unused")

package net.cydhra.nidhogg

import net.cydhra.nidhogg.data.Session
import net.cydhra.nidhogg.requests.UUIDResponse
import java.awt.image.BufferedImage
import java.net.URL
import java.time.Instant
import java.util.*

private const val HOST_STATUS_URL = "https://status.mojang.com"
private const val HOST_API_URL = "https://api.mojang.com"

private const val STATUS_ENDPOINT = "/check"
private const val USER_TO_UUID_BY_TIME_ENDPOINT = "/users/profiles/minecraft/"

/**
 * A client for the Mojang API. It wraps the endpoints of the service in functions and respective data classes.
 */
class MojangClient(private val nidhoggClientToken: String = DEFAULT_CLIENT_TOKEN) : NidhoggClient(nidhoggClientToken) {

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
    fun getUUIDbyUsername(name: String, time: Instant? = null): Optional<UUID> {
        val endpoint = USER_TO_UUID_BY_TIME_ENDPOINT + name

        val response = if (time != null)
            getRequest(HOST_API_URL, endpoint, Pair("at", time.epochSecond.toString()))
        else
            getRequest(HOST_API_URL, endpoint)

        if (response.status == 204) return Optional.empty()

        val uuid = gson.fromJson(response.getEntity(String::class.java), UUIDResponse::class.java).id
        val formattedUUID = "${uuid.subSequence(0, 8)}-${uuid.subSequence(8, 12)}-${uuid.subSequence(12, 16)}-${uuid.subSequence(16, 20)}-${uuid.subSequence(20, 32)}"
        return Optional.of(UUID.fromString(formattedUUID))
    }

    fun getNameHistoryByUUID(uuid: UUID) {
        throw UnsupportedOperationException()
    }

    fun getUUIDsByNames(names: Array<String>) {
        throw UnsupportedOperationException()
    }

    fun getProfileByUUID(uuid: UUID) {
        throw UnsupportedOperationException()
    }

    fun changeSkin(session: Session, uuid: UUID, source: URL) {
        throw UnsupportedOperationException()
    }

    fun uploadSkin(session: Session, uuid: UUID, skin: BufferedImage) {
        throw UnsupportedOperationException()
    }

    fun resetSkin(session: Session, uuid: UUID) {
        throw UnsupportedOperationException()
    }
}