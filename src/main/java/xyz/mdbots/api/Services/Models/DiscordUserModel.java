package xyz.mdbots.api.Services.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DiscordUserModel {
    private String id;
    private String username;
    private String avatar;
    private String banner;
    private String email;

    @JsonProperty("bot")
    private boolean isBot;

    @JsonProperty("avatar_url")
    private String AvatarUrl;

    @JsonProperty("banner_url")
    private String BannerUrl;
}
