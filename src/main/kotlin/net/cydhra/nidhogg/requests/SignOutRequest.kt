@file:Suppress("unused")

package net.cydhra.nidhogg.requests

/**
 * A request to sign from an account (invalidate all currently existing sessions for this account)
 */
internal class SignOutRequest(val username: String, val password: String)
