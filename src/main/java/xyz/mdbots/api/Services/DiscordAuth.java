package xyz.mdbots.api.Services;

import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import xyz.mdbots.api.Services.Interfaces.TokenResponse;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

/**
 *
 * @author Pedrovisk
 * @timestamp 2024-07-31 12:02:54
 */
@Service
public class DiscordAuth {

    private WebClient webClient;

    @Value("${discord.oauth.clientid}")
    private String clientId;

    @Value("${discord.oauth.clientsecret}")
    private String clientSecret;

    @Value("${discord.oauth.redirecturi}")
    private String redirectUri;

    @Value("${discord.oauth.baseurl}")
    private String baseUrl;

    public WebClient DiscordOAuthInstance() {
        return this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

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

    public Mono<TokenResponse> requestToken(String code) {
        if (code == null || code.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code não definido"));
        }

        String body = UriComponentsBuilder.newInstance()
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("grant_type", "authorization_code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("code", code)
                .build()
                .toUriString();

        return webClient.post()
                .uri("/oauth2/token")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .onErrorResume(WebClientResponseException.class, e
                        -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao solicitar token", e))
                );
    }

    public Mono<TokenResponse> refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "RefreshToken não definido"));
        }

        String body = UriComponentsBuilder.newInstance()
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("grant_type", "refresh_token")
                .queryParam("refresh_token", refreshToken)
                .build()
                .toUriString();

        return webClient.post()
                .uri("/oauth2/token")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .onErrorResume(WebClientResponseException.class, e
                        -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao atualizar o token", e))
                );
    }
}
