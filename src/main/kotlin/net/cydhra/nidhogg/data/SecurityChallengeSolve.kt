package net.cydhra.nidhogg.data

/**
 * A solve for one [SecurityChallenge]. An array of three of them must be submitted to the Mojang API to solve one complete security
 * challenge thus securing the current IP.
 *
 * @param id the answer id from the [SecurityChallenge.answer] instance
 * @param answer the clear text answer for the security question
 */
data class SecurityChallengeSolve(val id: Int, val answer: String)