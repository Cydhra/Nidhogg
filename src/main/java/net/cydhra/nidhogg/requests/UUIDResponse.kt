package net.cydhra.nidhogg.requests

/**
 * A response from Mojang API about a player's UUID
 *
 * [id] is the UUID requested
 *
 * [name] is the name of the UUID (not the requested name)
 *
 * [legacy] only appears when true - if the account is not migrated
 *
 * [demo] only appears when true - if the account hasn't bought an account
 *
 * see <a href="http://wiki.vg/Mojang_API#Username_-.3E_UUID_at_time">Mojang API</a>
 */
internal data class UUIDResponse(val id: String, val name: String, val legacy: Boolean?, val demo: Boolean?)