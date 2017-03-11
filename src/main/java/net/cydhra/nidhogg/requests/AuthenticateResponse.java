package net.cydhra.nidhogg.requests;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class AuthenticateResponse {
    private final String accessToken;
    private final String clientToken;
    
    private final List<Profile> availableProfiles;
    private final Profile selectedProfile;
    private final User user;
    
    @Data
    public static class Profile {
        private final String id;
        private final String name;
        private final boolean legacy;
    }
    
    @Data
    public static class User {
        private final String id;
        private final List<UserProperty> properties;
    }
    
    @Data
    public static class UserProperty {
        private final String name;
        private final String value;
    }
}
