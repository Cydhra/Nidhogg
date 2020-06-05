package net.cydhra.nidhogg

import io.ktor.client.HttpClient
import io.ktor.client.features.UserAgent
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

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
}