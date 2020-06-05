package net.cydhra.nidhogg.requests

import kotlinx.serialization.Serializable
import net.cydhra.nidhogg.YggdrasilAgent

/**
 * Body of an authentication request. This is constructed by the API and not touched by the library user.
 */
@Serializable
internal data class AuthRequest(
        val agent: YggdrasilAgent?,
        val username: String,
        val password: String,
        val clientToken: String?,
        val requestUser: Boolean = false
)