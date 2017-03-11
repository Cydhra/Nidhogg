package net.cydhra.nidhogg.requests;

import lombok.RequiredArgsConstructor;

/**
 *
 */
@RequiredArgsConstructor
public class LoginRequest {
    
    private final Agent agent;
    private final String username;
    private final String password;
    private final String clientToken;
    private final boolean requestUser;
    
    @RequiredArgsConstructor
    public static class Agent {
        private final String name;
        private final int version;
    }
}
