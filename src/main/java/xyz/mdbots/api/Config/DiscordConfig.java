package xyz.mdbots.api.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DiscordConfig {

    @Value("${discord.oauth.baseurl}")
    private String baseUrl;

    @Value("${discord.oauth.bottoken}")
    private String botToken;

    @Bean
    public WebClient authWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    @Bean
    public WebClient botWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl + "/v10")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bot " + botToken)
                .build();
    }
}