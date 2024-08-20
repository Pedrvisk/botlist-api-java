package xyz.mdbots.api.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import xyz.mdbots.api.Data.BotModel;
import xyz.mdbots.api.Repositories.BotRepository;

@RestController
@RequestMapping("/bots")
public class BotController {

    @Autowired
    BotRepository botsRepository;

    @GetMapping
    public List<BotModel> findAllBots() {
        return this.botsRepository.findAll();
    }

    @GetMapping("/{botId}")
    public ResponseEntity<BotModel> findByBotId(@PathVariable String botId) {
        if (botId == null || botId.trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id de bot inválido");

        BotModel bot = this.botsRepository.findByBotId(botId);
        if (bot != null) {
            return ResponseEntity.ok(bot);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não encontrado o bot de id " + botId);
        }
    }
}
