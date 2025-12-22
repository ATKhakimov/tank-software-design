package ru.mipt.bit.platformer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mipt.bit.platformer.view.HealthBarsController;
import ru.mipt.bit.platformer.view.HealthBarsControllerImpl;

@Configuration
public class InputConfiguration {
    @Bean
    public HealthBarsController healthBarsController() {
        return new HealthBarsControllerImpl();
    }
}
