package xyz.mdbots.api.Services.Interfaces;

import lombok.Data;

/**
 *
 * @author Pedrovisk
 * @timestamp 2024-08-01 01:34:02
 */
@Data
public class TokenResponse {
    private String token_type;
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String scope;
}
