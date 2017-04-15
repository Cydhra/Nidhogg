package net.cydhra.nidhogg.requests

data class LoginRequest(val agent: Agent, val username: String, val password: String, val clientToken: String, val requestUser: Boolean)

data class Agent(val name: String, val version: Int)