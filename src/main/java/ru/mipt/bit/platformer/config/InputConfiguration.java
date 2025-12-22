package ru.mipt.bit.platformer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mipt.bit.platformer.HealthBarsController;
import ru.mipt.bit.platformer.HealthBarsControllerImpl;

@Configuration
public class InputConfiguration {
    @Bean
    public HealthBarsController healthBarsController() {
        return new HealthBarsControllerImpl();
    }
}
