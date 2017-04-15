package net.cydhra.nidhogg.requests

import lombok.RequiredArgsConstructor

/**
 * A request for session validation
 */
class ValidationRequest(val accessToken: String, val clientToken: String)