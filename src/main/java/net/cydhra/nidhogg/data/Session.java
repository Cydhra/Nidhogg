package net.cydhra.nidhogg.data;

import lombok.Data;

import java.io.Serializable;

/**
 * A Yggdrasil session with an access token, that is used to validate the session and a client token, that is used to identify the client
 * that created the session
 */
@Data
public class Session implements Serializable {
    private String alias;
    private String accessToken;
    private String clientToken;
    
    public Session(final String alias, final String accessToken, final String clientToken) {
        this.alias = alias;
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }
}
