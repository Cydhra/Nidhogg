@file:Suppress("unused")

package net.cydhra.nidhogg.data

import kotlinx.serialization.Serializable

/**
 * The different API endpoints of the whole Mojang API
 */
@Serializable
enum class API(val hostname: String) {
    MINECRAFT("minecraft.net"),
    SESSION_MINECRAFT("session.minecraft.net"),
    ACCOUNT_MOJANG("account.mojang.com"),
    AUTH_MOJANG("auth.mojang.com"),
    SKINS_MINECRAFT("skins.minecraft.net"),
    AUTHSERVER_MOJANG("authserver.mojang.com"),
    SESSION_SERVER_MOJANG("sessionserver.mojang.com"),
    API_MOJANG("api.mojang.com"),
    TEXTURES_MINECRAFT("textures.minecraft.net"),
    MOJANG("mojang.com");

    fun getApiUrl() = "https://$hostname"
}

/**
 * The different status codes, a Mojang API can have
 */
@Serializable
enum class ApiStatus {
    GREEN, YELLOW, RED
}