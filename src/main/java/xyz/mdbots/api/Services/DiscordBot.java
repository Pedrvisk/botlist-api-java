package xyz.mdbots.api.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import xyz.mdbots.api.Config.DiscordConfig;
import xyz.mdbots.api.Services.Models.DiscordUserModel;

@Service
public class DiscordBot {
    @Autowired
    DiscordConfig discordConfig;

    public Mono<DiscordUserModel> findById(String id) {
        if (id == null || id.isEmpty())
            return Mono.error(new RuntimeException("Id não definido"));

        var discordUserData = discordConfig.botWebClient().get()
                .uri("/users/" + id)
                .retrieve()
                .bodyToMono(DiscordUserModel.class)
                .retry(2)
                .map(data -> {
                    String avatarFormat = data.getAvatar() != null
                            && data.getAvatar().startsWith("a_") ? "gif" : "png";
                    String avatarUrl = data.getAvatar() != null
                            ? String.format("https://cdn.discordapp.com/avatars/%s/%s.%s?size=4096",
                                    data.getId(),
                                    data.getAvatar(), avatarFormat)
                            : "https://cdn.discordapp.com/embed/avatars/0.png?size=4096";

                    String bannerFormat = data.getBanner() != null
                            && data.getBanner().startsWith("a_") ? "gif" : "png";
                    String bannerUrl = data.getBanner() != null
                            ? String.format("https://cdn.discordapp.com/banners/%s/%s.%s?size=4096",
                                    data.getId(),
                                    data.getBanner(), bannerFormat)
                            : "https://cdn.discordapp.com/embed/avatars/0.png?size=4096";

                    data.setAvatarUrl(avatarUrl);
                    data.setBannerUrl(bannerUrl);
                    return data;
                })
                .onErrorResume(WebClientResponseException.class,
                        e -> Mono.error(new RuntimeException(
                                "Erro ao buscar dados do usuário", e)));

        return discordUserData;
    }
}
