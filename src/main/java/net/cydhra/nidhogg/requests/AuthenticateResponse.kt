package net.cydhra.nidhogg.requests

internal class AuthenticateResponse(val accessToken: String,
                                    val clientToken: String,
                                    val availableProfiles: List<Profile>?,
                                    val selectedProfile: Profile?,
                                    val user: User?)

internal data class Profile(val id: String, val name: String, val isLegacy: Boolean)

internal data class User(val id: String, val properties: List<UserProperty>)

internal data class UserProperty(val value: String, val name: String)

