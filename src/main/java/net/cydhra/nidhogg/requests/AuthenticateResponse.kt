package net.cydhra.nidhogg.requests

class AuthenticateResponse(val accessToken: String,
                           val clientToken: String,
                           val availableProfiles: List<Profile>?,
                           val selectedProfile: Profile?,
                           val user: User?)

data class Profile(val id: String, val name: String, val isLegacy: Boolean)

data class User(val id: String, val properties: List<UserProperty>)

data class UserProperty(val value: String, val name: String)

