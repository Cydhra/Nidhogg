@file:Suppress("unused")

package net.cydhra.nidhogg.requests

internal class RefreshRequest(val accessToken: String, val clientToken: String, val requestUser: Boolean)
