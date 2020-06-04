package net.cydhra.nidhogg

import io.ktor.client.HttpClient
import io.ktor.utils.io.core.Closeable

/**
 * A client to the Yggdrasil authentication API by Mojang.
 */
class YggdrasilClient() : Closeable {
    private val client = HttpClient()

    override fun close() {
        this.client.close()
    }
}