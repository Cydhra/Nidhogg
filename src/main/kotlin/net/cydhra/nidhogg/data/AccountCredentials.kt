package net.cydhra.nidhogg.data

import java.io.Serializable

/**
 * Mojang account credentials. Those are created by API users to use the authenticate function. This API never creates or stores instances of this class.
 *
 * @param username Mojang account username
 * @param password Mojang account password
 */
data class AccountCredentials(val username: String, val password: String) : Serializable