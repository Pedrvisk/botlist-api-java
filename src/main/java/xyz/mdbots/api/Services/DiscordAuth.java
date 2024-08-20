package xyz.mdbots.api.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;
import xyz.mdbots.api.Config.DiscordConfig;
import xyz.mdbots.api.Services.Models.DiscordTokenModel;
import xyz.mdbots.api.Services.Models.DiscordUserModel;

@Service
public class DiscordAuth {

    @Autowired
    DiscordConfig discordConfig;

    @Value("${discord.oauth.clientid}")
    private String clientId;

    @Value("${discord.oauth.secret}")
    private String clientSecret;

    @Value("${discord.oauth.redirect}")
    private String redirectUri;

    public String getOAuthUrl() {
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("discord.com")
                .path("/oauth2/authorize")
                .queryParam("scope", "guilds.join identify email")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .build().toUriString();
    }

    public Mono<DiscordTokenModel> requestToken(String code) {
        if (code == null || code.isEmpty())
            return Mono.error(new RuntimeException("Code não definido"));

        var body = BodyInserters.fromFormData("client_id", clientId)
                .with("client_secret", clientSecret)
                .with("grant_type", "authorization_code")
                .with("redirect_uri", redirectUri)
                .with("code", code);

        return discordConfig.authWebClient().post()
                .uri("/oauth2/token")
                .body(body)
                .retrieve()
                .bodyToMono(DiscordTokenModel.class)
                .onErrorResume(WebClientResponseException.class,
                        e -> Mono.error(new RuntimeException("Erro ao solicitar token", e)));
    }

    public Mono<DiscordTokenModel> refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty())
            return Mono.error(new RuntimeException("RefreshToken não definido"));

        var body = BodyInserters.fromFormData("client_id", clientId)
                .with("client_secret", clientSecret)
                .with("refresh_token", refreshToken)
                .with("redirect_uri", redirectUri)
                .with("grant_type", "refresh_token");

        return discordConfig.authWebClient().post()
                .uri("/oauth2/token")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(DiscordTokenModel.class)
                .onErrorResume(WebClientResponseException.class,
                        e -> Mono.error(new RuntimeException("Erro ao atualizar o token", e)));
    }

    public Mono<DiscordUserModel> fetchAuthUser(String token) {
        if (token == null || token.isEmpty())
            return Mono.error(new RuntimeException("Token não definido"));

        return discordConfig.authWebClient().get()
                .uri("/users/@me")
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(DiscordUserModel.class)
                .retry(2)
                .map(data -> {
                    String format = data.getAvatar() != null && data.getAvatar().startsWith("a_")
                            ? "gif"
                            : "png";

                    String avatarUrl = data.getAvatar() != null
                            ? String.format("https://cdn.discordapp.com/avatars/%s/%s.%s?size=4096",
                                    data.getId(),
                                    data.getAvatar(), format)
                            : "https://cdn.discordapp.com/embed/avatars/0.png?size=4096";

                    data.setAvatarUrl(avatarUrl);
                    return data;
                })
                .onErrorResume(WebClientResponseException.class,
                        e -> Mono.error(new RuntimeException(
                                "Erro ao buscar dados do usuário autenticado", e)));
    }
}
