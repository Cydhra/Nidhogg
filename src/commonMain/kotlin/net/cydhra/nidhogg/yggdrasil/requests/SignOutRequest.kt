package net.cydhra.nidhogg.yggdrasil.requests

import kotlinx.serialization.Serializable

/**
 * Request entity to sign out of any remaining sessions associated with an account
 *
 * @param username account username
 * @param password account password
 */
@Serializable
internal data class SignOutRequest(
        val username: String,
        val password: String
)