package xyz.mdbots.api.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import xyz.mdbots.api.Repositories.Interfaces.IBotRepository;
import org.springframework.stereotype.Service;
import xyz.mdbots.api.Data.BotModel;
import java.util.List;

/**
 *
 * @author Pedrovisk
 * @timestamp 2024-07-26 16:38:10
 */
@Service
public class BotRepository {

    @Autowired
    private IBotRepository iBotRepository;

    public List<BotModel> findAll() {
        return this.iBotRepository.findAll();
    }

    public BotModel saveBot(BotModel bot) {
        return this.iBotRepository.save(bot);
    }

    public BotModel findByBotId(String botId) {
        return this.iBotRepository.findByBotId(botId);
    }
}
