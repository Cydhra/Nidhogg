package net.cydhra.nidhogg.requests

internal data class LoginRequest(val agent: Agent, val username: String, val password: String, val clientToken: String, val requestUser:
Boolean)

internal data class Agent(val name: String, val version: Int)