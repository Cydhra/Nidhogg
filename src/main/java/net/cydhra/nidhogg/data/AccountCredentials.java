package net.cydhra.nidhogg.data;

import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class AccountCredentials implements Serializable {
    private final String username;
    private final String password;
}
