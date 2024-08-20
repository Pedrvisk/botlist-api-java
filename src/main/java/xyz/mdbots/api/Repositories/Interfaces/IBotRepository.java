package xyz.mdbots.api.Repositories.Interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import xyz.mdbots.api.Data.BotModel;

@Repository
public interface IBotRepository extends JpaRepository<BotModel, Long> {
    BotModel findByBotId(String botId);

    boolean existsByBotId(String botId);
}
