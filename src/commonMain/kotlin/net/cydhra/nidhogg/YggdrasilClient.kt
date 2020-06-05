package net.cydhra.nidhogg

import io.ktor.client.HttpClient
import io.ktor.utils.io.core.Closeable
import net.cydhra.nidhogg.data.AccountCredentials
import net.cydhra.nidhogg.data.Session

/**
 * A client to the Yggdrasil authentication API by Mojang.
 */
class YggdrasilClient() : Closeable {
    private val client = HttpClient()

    /**
     * Authenticate at Yggdrasil for a given service using a pair of username and password.
     *
     * @param credentials username and password for authentication
     * @param agent a [YggdrasilAgent] instance indicating for which service the authentication is intended.
     *
     * @return a session instance on success
     *
     * @see [MinecraftAgent]
     * @see [ScrollsAgent]
     */
    fun authenticate(credentials: AccountCredentials, agent: YggdrasilAgent): Session {
        TODO()
    }

    fun refresh() {
        TODO()
    }

    override fun close() {
        this.client.close()
    }
}