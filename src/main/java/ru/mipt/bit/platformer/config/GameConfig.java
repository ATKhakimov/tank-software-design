package ru.mipt.bit.platformer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import ru.mipt.bit.platformer.ai.BotStrategy;
import ru.mipt.bit.platformer.ai.HoldCourseStrategy;
import ru.mipt.bit.platformer.ai.RandomStrategy;
import ru.mipt.bit.platformer.HealthBarsController;
import ru.mipt.bit.platformer.HealthBarsControllerImpl;
import ru.mipt.bit.platformer.model.WorldModel;

@Configuration
@ComponentScan(basePackages = "ru.mipt.bit.platformer")
public class GameConfig {
    @Bean
    public BotStrategy botStrategy(@Value("${ai:random}") String mode) {
        if ("hold".equalsIgnoreCase(mode)) return new HoldCourseStrategy();
        return new RandomStrategy();
    }

    @Bean
    public HealthBarsController healthBarsController() {
        return new HealthBarsControllerImpl();
    }

    @Bean
    public WorldModelFactory worldModelFactory(@Value("${bullet.speed:0.05}") float bulletSpeed,
                                               @Value("${bullet.damage:0.25}") float bulletDamage) {
        return new WorldModelFactory(bulletSpeed, bulletDamage);
    }
}
