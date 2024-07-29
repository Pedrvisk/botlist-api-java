package xyz.mdbots.api.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.mdbots.api.Repositories.BotRepository;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import xyz.mdbots.api.Data.BotModel;
import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;

/**
 *
 * @author Pedrovisk
 * @timestamp 2024-07-26 15:32:49
 */
@RestController
@RequestMapping("/bots")
@Tag(name = "Endpoint de Bot", description = "Destinado ao CRUD de Bot")
public class BotController {

    @Autowired
    BotRepository _botsRepository;

    @GetMapping
    public List<BotModel> findAllBots() {
        return this._botsRepository.findAll();
    }

    @GetMapping("/{botId}")
    public ResponseEntity<?> findByBotId(@PathVariable String botId) {
        if (botId == null || botId.trim().isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id de bot inválido");
        
        BotModel bot = this._botsRepository.findByBotId(botId);
        if (bot != null) return ResponseEntity.ok(bot);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O bot de id " + botId + " não existe");
    }
    
    @PostMapping
    public BotModel createBot(@RequestBody BotModel bot) {
        return this._botsRepository.saveBot(bot);
    }
}
