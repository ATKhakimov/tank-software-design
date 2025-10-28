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
import ru.mipt.bit.platformer.ai.BotStrategy;
import ru.mipt.bit.platformer.ai.HoldCourseStrategy;
import ru.mipt.bit.platformer.ai.RandomStrategy;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeObstacleModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameDesktopLauncher implements ApplicationListener {

    private static final float MOVEMENT_SPEED = 0.4f;

    private Batch batch;
    private GameField field;
    private Tank player;
    private final List<Renderable> obstacles = new ArrayList<>();
    private final List<Tank> aiTanks = new ArrayList<>();
    private final List<TankModel> aiModels = new ArrayList<>();
    private InputHandler inputHandler;
    private AIHandler aiHandler;
    private MovementRules movementRules;

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

        List<Obstacle> obstacleModels = new ArrayList<>();
        for (GridPoint2 pos : data.getTreePositions()) {
            TreeObstacleModel m = new TreeObstacleModel();
            m.setPosition(new GridPoint2(pos.x, pos.y));
            obstacleModels.add(m);
            Renderable r = new TreeObstacle(new Texture("images/greenTree.png"), m);
            r.align(field.movement());
            obstacles.add(r);
        }

        
        int w = field.widthInTiles();
        int h = field.heightInTiles();
        Set<GridPoint2> forbidden = new HashSet<>();
        forbidden.add(new GridPoint2(tankModel.getCoordinates()));
        for (Obstacle o : obstacleModels) {
            forbidden.add(new GridPoint2(o.getCoordinates()));
        }
        List<GridPoint2> free = new ArrayList<>();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                GridPoint2 p = new GridPoint2(x, y);
                if (!forbidden.contains(p)) free.add(p);
            }
        }
        int aiCount = Math.min(readBotsCount(), free.size());
        Random rnd = new Random();
        for (int i = 0; i < aiCount; i++) {
            int idx = rnd.nextInt(free.size());
            GridPoint2 p = free.remove(idx);
            TankModel ai = new TankModel(MOVEMENT_SPEED);
            ai.setPosition(new GridPoint2(p));
            aiModels.add(ai);
            Tank aiRenderable = new Tank(new Texture("images/tank_blue.png"), ai);
            aiRenderable.align(field.movement());
            aiTanks.add(aiRenderable);
        }

        movementRules = new MovementRules(w, h, obstacleModels);
        inputHandler = new InputHandler(tankModel, movementRules);
        aiHandler = new AIHandler(movementRules, aiModels, selectStrategy());
    }

    private BotStrategy selectStrategy() {
        String mode = System.getProperty("ai", "");
        if (mode.isEmpty()) {
            String env = System.getenv("AI");
            if (env != null) mode = env;
        }
        if ("hold".equalsIgnoreCase(mode)) return new HoldCourseStrategy();
        return new RandomStrategy();
    }

    private int readBotsCount() {
        String val = System.getProperty("bots");
        if (val == null) {
            val = System.getenv("BOTS");
        }
        int def = 3;
        if (val == null || val.isEmpty()) return def;
        try {
            int n = Integer.parseInt(val.trim());
            return Math.max(0, n);
        } catch (NumberFormatException e) {
            return def;
        }
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

        
    Set<GridPoint2> occupied = computeOccupied();
    Set<GridPoint2> reserved = computeReserved();
    movementRules.setOccupied(occupied, reserved);

    
        inputHandler.handle();
        aiHandler.handle();

       
        player.update(field.movement(), deltaTime);
        for (Tank t : aiTanks) {
            t.update(field.movement(), deltaTime);
        }

        field.render();

        batch.begin();
        player.render(batch);
        for (Renderable r : obstacles) {
            r.render(batch);
        }
        for (Tank t : aiTanks) {
            t.render(batch);
        }
        batch.end();
    }

    private Set<GridPoint2> computeOccupied() {
        Set<GridPoint2> occ = new HashSet<>();
        addCurrentCells(occ, player);
        for (Tank t : aiTanks) addCurrentCells(occ, t);
        return occ;
    }

    private Set<GridPoint2> computeReserved() {
        Set<GridPoint2> res = new HashSet<>();
        addMovingReservations(res, player);
        for (Tank t : aiTanks) addMovingReservations(res, t);
        return res;
    }

    private void addCurrentCells(Set<GridPoint2> set, Tank t) {
        GridPoint2 from = t.getModel().getCoordinates();
        set.add(new GridPoint2(from));
    }

    private void addMovingReservations(Set<GridPoint2> set, Tank t) {
        if (!t.getModel().isReady()) {
            set.add(new GridPoint2(t.getModel().getCoordinates()));
            set.add(new GridPoint2(t.getModel().getDestination()));
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
        for (Renderable r : obstacles) {
            r.dispose();
        }
        for (Tank t : aiTanks) {
            t.dispose();
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
