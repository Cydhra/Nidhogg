package net.cydhra.nidhogg.requests;

import lombok.Data;

/**
 * A request to sign from an account (invalidate all currently existing sessions for this account)
 */
@Data
public class SignOutRequest {
    private final String username;
    private final String password;
}
