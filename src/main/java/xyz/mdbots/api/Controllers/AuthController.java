package xyz.mdbots.api.Controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;
import xyz.mdbots.api.Services.DiscordAuth;
import xyz.mdbots.api.Services.JwtToken;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    DiscordAuth discordAuth;

    @Autowired
    JwtToken jwtToken;

    @Value("${frontend.cookie}")
    private String cookieName;

    @Value("${frontend.url}")
    private String frontendUrl;

    @GetMapping("/login")
    public RedirectView getLoginUrl() {
        return new RedirectView(discordAuth.getOAuthUrl());
    }

    @GetMapping("/callback")
    public Mono<RedirectView> callbackOAuth(@RequestParam Optional<String> error, @RequestParam Optional<String> code,
            HttpServletResponse res) {
        if (error.isPresent())
            return Mono.just(new RedirectView(frontendUrl + "/auth/callback?error=" + error.get()));

        String authorizationCode = code.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código de autorização não fornecido"));

        return discordAuth.requestToken(authorizationCode)
                .flatMap(discordTokenModel -> {
                    if (discordTokenModel == null || discordTokenModel.getAccessToken() == null)
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token não definido"));

                    String accessToken = discordTokenModel.getAccessToken();

                    return discordAuth.fetchAuthUser(accessToken)
                            .flatMap(discordUserModel -> {
                                if (discordUserModel == null)
                                    return Mono.error(new RuntimeException("Dados do usuário não disponíveis"));

                                return Mono.fromCallable(() -> {
                                    String authToken = jwtToken.encode(discordTokenModel, discordUserModel);
                                    if (authToken == null)
                                        Mono.error(new RuntimeException("Erro ao gerar token"));

                                    ResponseCookie cookie = ResponseCookie.from(cookieName, authToken)
                                            .httpOnly(true)
                                            .path("/")
                                            .secure("production"
                                                    .equalsIgnoreCase(System.getProperty("spring.profiles.active")))
                                            .sameSite("Strict")
                                            .maxAge(518400) // 6 days
                                            .build();
                                    res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                                    return new RedirectView(frontendUrl + "/auth/callback");
                                });
                            });
                })
                .onErrorResume(
                        e -> Mono.just(new RedirectView(frontendUrl + "/auth/callback?error=" + e.getMessage())));
    }

    @PostMapping("/logout")
    public void logoutOAuth(HttpServletResponse res) {
        try {
            ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                    .httpOnly(true)
                    .path("/")
                    .secure("production".equalsIgnoreCase(System.getProperty("spring.profiles.active")))
                    .sameSite("Strict")
                    .maxAge(0)
                    .build();
            res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            res.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Erro ao fazer logout");
        }
    }

    @PostMapping("/verify")
    public boolean verifyOAuth() {
        return true;
    }
}
