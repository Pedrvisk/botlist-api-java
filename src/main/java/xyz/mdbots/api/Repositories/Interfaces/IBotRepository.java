package xyz.mdbots.api.Repositories.Interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.mdbots.api.Data.BotModel;

/**
 *
 * @author Pedrovisk
 * @timestamp 2024-07-26 16:38:29
 */
@Repository
public interface IBotRepository extends JpaRepository<BotModel, Long> {
    BotModel findByBotId(String botId);
}
