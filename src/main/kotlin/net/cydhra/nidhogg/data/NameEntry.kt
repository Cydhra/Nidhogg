package net.cydhra.nidhogg.data

/**
 * An entry in the name history response. Each entry in the list represents one user name, the requested UUID was once associated with.
 *
 * @param name player name
 * @param changedToAt (optional) the timestamp in milliseconds when the name was changed to this entry
 */
data class NameEntry(val name: String, val changedToAt: Long?)