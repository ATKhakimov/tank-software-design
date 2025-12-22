package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.mipt.bit.platformer.ai.BotStrategy;
import ru.mipt.bit.platformer.config.WorldModelFactory;
import ru.mipt.bit.platformer.level.LevelLoader;

public class GameDesktopLauncher implements ApplicationListener {

    private final ObjectProvider<Batch> batchProvider;
    private final ObjectProvider<GameField> fieldProvider;
    private final ObjectProvider<LevelLoader> levelLoaderProvider;
    private final ObjectProvider<Texture> tankTextureProvider;
    private final ObjectProvider<Texture> treeTextureProvider;
    private final ObjectProvider<Texture> pixelTextureProvider;
    private final HealthBarsController healthBarsController;
    private final WorldModelFactory worldFactory;
    private final BotStrategy botStrategy;
    private final int botsCount;

    private GameSession gameSession;

    public GameDesktopLauncher(BotStrategy botStrategy, HealthBarsController healthBarsController, WorldModelFactory worldFactory,
                               ObjectProvider<Batch> batchProvider,
                               ObjectProvider<GameField> fieldProvider,
                               ObjectProvider<LevelLoader> levelLoaderProvider,
                               @Qualifier("tankTexture") ObjectProvider<Texture> tankTextureProvider,
                               @Qualifier("treeTexture") ObjectProvider<Texture> treeTextureProvider,
                               @Qualifier("pixelTexture") ObjectProvider<Texture> pixelTextureProvider,
                               int botsCount) {
        this.botStrategy = botStrategy;
        this.healthBarsController = healthBarsController;
        this.worldFactory = worldFactory;
        this.batchProvider = batchProvider;
        this.fieldProvider = fieldProvider;
        this.levelLoaderProvider = levelLoaderProvider;
        this.tankTextureProvider = tankTextureProvider;
        this.treeTextureProvider = treeTextureProvider;
        this.pixelTextureProvider = pixelTextureProvider;
        this.botsCount = botsCount;
    }

    @Override
    public void create() {
        Batch batch = batchProvider.getObject();
        GameField field = fieldProvider.getObject();
        LevelLoader levelLoader = levelLoaderProvider.getObject();
        Texture tankTexture = tankTextureProvider.getObject();
        Texture treeTexture = treeTextureProvider.getObject();
        Texture pixelTexture = pixelTextureProvider.getObject();

        LevelGraphics levelGraphics = new LevelGraphics(field, batch, tankTexture, treeTexture, pixelTexture, healthBarsController);
        gameSession = new GameSession(worldFactory, botStrategy, healthBarsController, levelLoader, levelGraphics, botsCount);
        gameSession.start();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        com.badlogic.gdx.Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
        if (gameSession != null) {
            gameSession.renderFrame(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        if (gameSession != null) {
            gameSession.dispose();
        }
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 1024);
        try (org.springframework.context.annotation.AnnotationConfigApplicationContext ctx = new org.springframework.context.annotation.AnnotationConfigApplicationContext(ru.mipt.bit.platformer.config.GameSessionConfiguration.class)) {
            GameDesktopLauncher launcher = ctx.getBean(GameDesktopLauncher.class);
            new Lwjgl3Application(launcher, config);
        }
    }
}
