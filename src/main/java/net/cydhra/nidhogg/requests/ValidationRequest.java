package net.cydhra.nidhogg.requests;

import lombok.RequiredArgsConstructor;

/**
 * A request for session validation
 */
@RequiredArgsConstructor
public class ValidationRequest {
    private final String accessToken;
    private final String clientToken;
}
