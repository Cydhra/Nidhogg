package net.cydhra.nidhogg.data

import kotlinx.serialization.Serializable

/**
 * A security challenge compound object. Three of them form a Mojang security challenge to verify an account's IP address.
 *
 * @param answer an [Answer] instance for this answer-question pair
 * @param question a [Question] instance for this answer-question pair
 */
@Serializable
data class SecurityChallenge(val answer: Answer, val question: Question) {

    /**
     * Generate the answer for this particular security question
     *
     * @param answer text answer by end-user
     *
     * @return a [SecurityChallengeSolve] correctly formatted to be the answer to the question
     */
    fun answerQuestion(answer: String): SecurityChallengeSolve {
        return SecurityChallengeSolve(this.answer.id, answer)
    }
}

/**
 * A answer for a security question
 *
 * @param id answer id
 */
@Serializable
data class Answer(val id: Int)

/**
 * A question for a security challenge
 *
 * @param id question id
 * @param question question text
 */
@Serializable
data class Question(val id: Int, val question: String)