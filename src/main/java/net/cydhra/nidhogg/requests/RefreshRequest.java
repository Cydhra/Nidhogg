package net.cydhra.nidhogg.requests;

import lombok.Data;

/**
 *
 */
@Data
public class RefreshRequest {
    private final String accessToken;
    private final String clientToken;
    private final boolean requestUser;
}
