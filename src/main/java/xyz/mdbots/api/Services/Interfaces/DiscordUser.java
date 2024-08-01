package xyz.mdbots.api.Services.Interfaces;

import lombok.Data;

/**
 *
 * @author Pedrovisk
 * @timestamp 2024-08-01 01:34:55
 */
@Data
public class DiscordUser {
    private String id;
    private String username;
    private String avatar;
    private String email;
    private String avatarUrl;
}
