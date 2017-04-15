package net.cydhra.nidhogg.data

import java.io.Serializable

/**
 * Mojang account credentials
 */
data class AccountCredentials(val username: String, val password: String) : Serializable