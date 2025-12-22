package ru.mipt.bit.platformer.config;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import ru.mipt.bit.platformer.GameDesktopLauncher;
import ru.mipt.bit.platformer.view.HealthBarsController;
import ru.mipt.bit.platformer.config.WorldModelFactory;
import ru.mipt.bit.platformer.level.FileLevelLoader;
import ru.mipt.bit.platformer.level.LevelLoader;
import ru.mipt.bit.platformer.level.RandomLevelGenerator;
import ru.mipt.bit.platformer.ai.BotStrategy;
import ru.mipt.bit.platformer.view.GameField;

@Configuration
@Import({CoreConfiguration.class, AiConfiguration.class, InputConfiguration.class, RenderingConfiguration.class})
public class GameSessionConfiguration {
    @Bean
    @Lazy
    public LevelLoader levelLoader(GameField field,
                                   @Value("${level.mode:}") String modeProperty,
                                   @Value("${LEVEL_MODE:}") String modeEnv) {
        String mode = modeProperty.isEmpty() ? modeEnv : modeProperty;
        LevelLoader fileLoader = new FileLevelLoader("level.txt");
        LevelLoader randomLoader = new RandomLevelGenerator(field.widthInTiles(), field.heightInTiles(), 0.2f);
        LevelLoader tmxLoader = new ru.mipt.bit.platformer.level.TmxLevelLoader("level.tmx", fileLoader);
        if ("random".equalsIgnoreCase(mode)) {
            return randomLoader;
        }
        if ("file".equalsIgnoreCase(mode)) {
            return fileLoader;
        }
        return tmxLoader;
    }

    @Bean
    public GameDesktopLauncher gameDesktopLauncher(BotStrategy botStrategy,
                                                   HealthBarsController healthBarsController,
                                                   WorldModelFactory worldModelFactory,
                                                   ObjectProvider<Batch> batchProvider,
                                                   ObjectProvider<GameField> fieldProvider,
                                                   ObjectProvider<LevelLoader> levelLoaderProvider,
                                                   @Qualifier("tankTexture") ObjectProvider<Texture> tankTextureProvider,
                                                   @Qualifier("treeTexture") ObjectProvider<Texture> treeTextureProvider,
                                                   @Qualifier("pixelTexture") ObjectProvider<Texture> pixelTextureProvider,
                                                   @Value("${bots:3}") int botsCount) {
        return new GameDesktopLauncher(botStrategy, healthBarsController, worldModelFactory, batchProvider, fieldProvider, levelLoaderProvider, tankTextureProvider, treeTextureProvider, pixelTextureProvider, botsCount);
    }

    private boolean resourceExists(String resourcePath) {
        return GameSessionConfiguration.class.getClassLoader().getResource(resourcePath) != null;
    }
}
