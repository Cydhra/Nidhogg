package net.cydhra.nidhogg.data;

import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class Session implements Serializable {
    private final String alias;
    private final String accessToken;
    private final String clientToken;
}
