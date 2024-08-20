package xyz.mdbots.api.Controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;
import xyz.mdbots.api.Data.BotModel;
import xyz.mdbots.api.Repositories.BotRepository;
import xyz.mdbots.api.Services.DiscordAuth;
import xyz.mdbots.api.Services.DiscordBot;

@RestController
@RequestMapping("/me")
public class UserController {

    @Autowired
    DiscordAuth discordAuth;

    @Autowired
    DiscordBot discordbot;

    @Autowired
    BotRepository botRepository;

    @GetMapping("/profile")
    @Cacheable(value = "usersProfile", key = "#req.getAttribute('userId')")
    public Mono<Map<String, Object>> findUserInfo(HttpServletRequest req) {
        var auth = req.getAttribute("auth");
        Claims claims = (Claims) auth;
        var accessToken = claims.get("access_token", String.class);

        return discordAuth.fetchAuthUser(accessToken)
                .map(discordUserModel -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", discordUserModel.getId());
                    userMap.put("username", discordUserModel.getUsername());
                    userMap.put("avatar_url", discordUserModel.getAvatarUrl());
                    return userMap;
                });
    }

    @PostMapping("/bots/create")
    public BotModel createBot(@RequestBody BotModel botModel, HttpServletRequest req) {
        var userId = req.getAttribute("userId");
        botModel.setOwnerId((String) userId);

        discordbot.findById(botModel.getBotId()).map((discordBotModel) -> {
            if (discordBotModel.isBot() == true)
                return false;
            botModel.setBannerUrl(discordBotModel.getBannerUrl());
            botModel.setAvatarUrl(discordBotModel.getAvatarUrl());
            return discordBotModel;
        });

        return botRepository.saveBot(botModel);
    }
}
