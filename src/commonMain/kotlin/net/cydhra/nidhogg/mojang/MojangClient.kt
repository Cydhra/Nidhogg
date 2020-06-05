package net.cydhra.nidhogg.mojang

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.core.Closeable
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


    private fun constructHeaders(builder: HttpRequestBuilder) {
        builder.contentType(ContentType.Application.Json)
    }

    override fun close() {
        this.client.close()
    }
}