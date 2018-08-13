package net.cydhra.nidhogg.requests

/**
 * A response from Mojang API about a player's UUID
 *
 * @param id the requested UUID
 * @param name the name of the UUID (not the requested name)
 * @param legacy only appears when true - if the account is not migrated
 * @param demo only appears when true - if the account hasn't bought an account
 *
 * see <a href="http://wiki.vg/Mojang_API#Username_-.3E_UUID_at_time">Mojang API</a>
 */
internal data class UUIDResponse(val id: String, val name: String, val legacy: Boolean?, val demo: Boolean?)