package xyz.mdbots.api.Repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.mdbots.api.Data.BotModel;
import xyz.mdbots.api.Repositories.Interfaces.IBotRepository;

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
