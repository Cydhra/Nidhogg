package net.cydhra.nidhogg.requests;

import lombok.Data;

/**
 *
 */
@Data
public class ErrorResponse {
    private final String error;
    private final String errorMessage;
    private final String cause;
}
