package xyz.mdbots.api.Config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("bots", "users", "usersProfile");
    }

    @CacheEvict(value = "usersProfile", allEntries = true)
    @Scheduled(fixedRate = 300000)
    public void clearUsersProfile() {
        System.out.println("UsersProfile cleared!");
    }
}
