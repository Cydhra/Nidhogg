package net.cydhra.nidhogg.data

import java.util.*

/**
 * A response from Mojang API about a player's UUID
 *
 * @param id the requested UUID as a string without hyphens. Use [uuid] for an actual instance of [UUID]
 * @param name the current name of the account (not the requested name, if an older name was requested)
 * @param legacy only appears when true - if the account is not migrated
 * @param demo only appears when true - if the account hasn't bought an account
 *
 * see <a href="http://wiki.vg/Mojang_API#Username_-.3E_UUID_at_time">Mojang API</a>
 */
data class UUIDEntry(val id: String, val name: String, val legacy: Boolean?, val demo: Boolean?) {

    /**
     * Returns a UUID object containing the UUID defined by [id]. However, accessing this property two times, does not return the
     * identically equal object but two objects representing the same UUID.
     */
    val uuid: UUID?
        get() = UUID.fromString("${id.subSequence(0, 8)}-${id.subSequence(8, 12)}-" +
                "${id.subSequence(12, 16)}-${id.subSequence(16, 20)}-${id.subSequence(20, 32)}"
        )
}