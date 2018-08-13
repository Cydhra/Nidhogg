@file:Suppress("CanBeParameter", "unused")

package net.cydhra.nidhogg

import com.google.gson.reflect.TypeToken
import net.cydhra.nidhogg.data.NameEntry
import net.cydhra.nidhogg.data.Session
import net.cydhra.nidhogg.data.UUIDEntry
import java.awt.image.BufferedImage
import java.net.URL
import java.time.Instant
import java.util.*

private const val STATUS_API_URL = "https://status.mojang.com"
private const val MOJANG_API_URL = "https://api.mojang.com"
private const val SESSION_SERVER_URL = "https://sessionserer.mojang.com"

private const val STATUS_ENDPOINT = "/check"
private const val USER_TO_UUID_BY_TIME_ENDPOINT = "/users/profiles/minecraft/%s"
private const val NAME_HISTORY_BY_UUID_ENDPOINT = "/user/profiles/%s/names"
private const val UUIDS_BY_NAMES_ENDPOINT = "/profiles/minecraft"

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
    fun getUUIDbyUsername(name: String, time: Instant? = null): UUIDEntry? {
        val endpoint = USER_TO_UUID_BY_TIME_ENDPOINT.format(name)

        val response = if (time != null)
            getRequest(MOJANG_API_URL, endpoint, Pair("at", time.epochSecond.toString()))
        else
            getRequest(MOJANG_API_URL, endpoint)

        if (response.status == 204) return null

        return gson.fromJson(response.getEntity(String::class.java), UUIDEntry::class.java)
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

        return gson.fromJson(response.getEntity(String::class.java), object : TypeToken<List<NameEntry>>() {}.type)
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

        return gson.fromJson(response.getEntity(String::class.java), object : TypeToken<List<UUIDEntry>>() {}.type)
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