package ru.mipt.bit.platformer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mipt.bit.platformer.config.WorldModelFactory;

@Configuration
public class CoreConfiguration {
    @Bean
    public WorldModelFactory worldModelFactory(@Value("${bullet.speed:0.05}") float bulletSpeed,
                                               @Value("${bullet.damage:0.25}") float bulletDamage) {
        return new WorldModelFactory(bulletSpeed, bulletDamage);
    }
}
