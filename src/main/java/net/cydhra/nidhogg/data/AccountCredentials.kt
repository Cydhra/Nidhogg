package net.cydhra.nidhogg.data

import java.io.Serializable

/**
 * Mojang account credentials
 *
 * @param username mojang account username
 * @param password mojang account password
 */
data class AccountCredentials(val username: String, val password: String) : Serializable