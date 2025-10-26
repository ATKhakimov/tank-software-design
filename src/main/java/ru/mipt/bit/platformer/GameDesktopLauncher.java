// Исправлены нарушения OCP/DIP через интерфейсы GameObject/Obstacle/Renderable
package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.level.FileLevelLoader;
import ru.mipt.bit.platformer.level.LevelData;
import ru.mipt.bit.platformer.level.LevelLoader;
import ru.mipt.bit.platformer.level.RandomLevelGenerator;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeObstacleModel;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameDesktopLauncher implements ApplicationListener {

    private static final float MOVEMENT_SPEED = 0.4f;

    private Batch batch;
    private GameField field;
    private Tank player;
    private final List<Renderable> obstacles = new ArrayList<>();
    private InputHandler inputHandler;

    @Override
    public void create() {
        batch = new SpriteBatch();
        field = new GameField(batch);

        LevelLoader loader = selectLoader();
        LevelData data = loader.load();

        TankModel tankModel = new TankModel(MOVEMENT_SPEED);
        tankModel.setPosition(new GridPoint2(data.getPlayerStart().x, data.getPlayerStart().y));
        player = new Tank(new Texture("images/tank_blue.png"), tankModel);
        player.align(field.movement());

        List<ru.mipt.bit.platformer.model.Obstacle> obstacleModels = new ArrayList<>();
        for (GridPoint2 pos : data.getTreePositions()) {
            TreeObstacleModel m = new TreeObstacleModel();
            m.setPosition(new GridPoint2(pos.x, pos.y));
            obstacleModels.add(m);
            Renderable r = new TreeObstacle(new Texture("images/greenTree.png"), m);
            r.align(field.movement());
            obstacles.add(r);
        }

        inputHandler = new InputHandler(tankModel, obstacleModels);
    }

    private LevelLoader selectLoader() {
        String mode = System.getProperty("level.mode", "");
        if (mode.isEmpty()) {
            String env = System.getenv("LEVEL_MODE");
            if (env != null) {
                mode = env;
            }
        }
        if ("random".equalsIgnoreCase(mode)) {
            return new RandomLevelGenerator(field.widthInTiles(), field.heightInTiles(), 0.2f);
        }
        if ("file".equalsIgnoreCase(mode)) {
            return new FileLevelLoader("level.txt");
        }
        if (resourceExists("level.txt")) {
            return new FileLevelLoader("level.txt");
        }
        return new RandomLevelGenerator(field.widthInTiles(), field.heightInTiles(), 0.2f);
    }

    private boolean resourceExists(String resourcePath) {
        return GameDesktopLauncher.class.getClassLoader().getResource(resourcePath) != null;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        inputHandler.handle();
        player.update(field.movement(), deltaTime);

        field.render();

        batch.begin();
        player.render(batch);
        for (Renderable r : obstacles) {
            r.render(batch);
        }
        batch.end();
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
        for (Renderable r : obstacles) {
            r.dispose();
        }
        player.dispose();
        field.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
