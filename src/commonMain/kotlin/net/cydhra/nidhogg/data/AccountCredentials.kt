package net.cydhra.nidhogg.data

import kotlinx.serialization.Serializable

/**
 * Mojang account credentials. Those are created by API users to use the authenticate function. This API never
 * creates or stores instances of this class.
 *
 * @param username Mojang account username. If migrated, this is an E-Mail address.
 * @param password Mojang account password
 */
@Serializable
data class AccountCredentials(val username: String, val password: String)