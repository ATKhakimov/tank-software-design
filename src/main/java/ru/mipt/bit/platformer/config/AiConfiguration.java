package ru.mipt.bit.platformer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mipt.bit.platformer.ai.BotStrategy;
import ru.mipt.bit.platformer.ai.HoldCourseStrategy;
import ru.mipt.bit.platformer.ai.RandomStrategy;

@Configuration
public class AiConfiguration {
    @Bean
    public BotStrategy botStrategy(@Value("${ai:random}") String mode) {
        if ("hold".equalsIgnoreCase(mode)) {
            return new HoldCourseStrategy();
        }
        return new RandomStrategy();
    }
}
