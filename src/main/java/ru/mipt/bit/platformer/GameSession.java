package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.ai.BotStrategy;
import ru.mipt.bit.platformer.config.WorldModelFactory;
import ru.mipt.bit.platformer.level.LevelData;
import ru.mipt.bit.platformer.level.LevelLoader;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeObstacleModel;
import ru.mipt.bit.platformer.model.WorldModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameSession {
    private static final float MOVEMENT_SPEED = 0.4f;

    private final WorldModelFactory worldFactory;
    private final BotStrategy botStrategy;
    private final HealthBarsController healthBarsController;
    private final LevelLoader levelLoader;
    private final LevelGraphics levelGraphics;
    private final int botsCount;

    private WorldModel world;
    private MovementRules movementRules;
    private InputHandler inputHandler;
    private AIHandler aiHandler;
    private final List<TankModel> aiModels = new ArrayList<>();
    private final List<Obstacle> obstacleModels = new ArrayList<>();
    private TankModel playerModel;

    public GameSession(WorldModelFactory worldFactory,
                       BotStrategy botStrategy,
                       HealthBarsController healthBarsController,
                       LevelLoader levelLoader,
                       LevelGraphics levelGraphics,
                       int botsCount) {
        this.worldFactory = worldFactory;
        this.botStrategy = botStrategy;
        this.healthBarsController = healthBarsController;
        this.levelLoader = levelLoader;
        this.levelGraphics = levelGraphics;
        this.botsCount = botsCount;
    }

    public void start() {
        LevelData data = levelLoader.load();
        int w = levelGraphics.getField().widthInTiles();
        int h = levelGraphics.getField().heightInTiles();

        world = worldFactory.create(w, h);
        world.addObserver(levelGraphics);

        playerModel = new TankModel(MOVEMENT_SPEED);
        playerModel.setPosition(new GridPoint2(data.getPlayerStart().x, data.getPlayerStart().y));
        world.addTank(playerModel);

        for (GridPoint2 pos : data.getTreePositions()) {
            TreeObstacleModel m = new TreeObstacleModel();
            m.setPosition(new GridPoint2(pos.x, pos.y));
            obstacleModels.add(m);
            world.addObstacle(m);
        }

        movementRules = new MovementRules(w, h, obstacleModels);

        List<GridPoint2> free = collectFreeCells(w, h);
        int aiCount = Math.min(botsCount, free.size());
        Random rnd = new Random();
        for (int i = 0; i < aiCount; i++) {
            int idx = rnd.nextInt(free.size());
            GridPoint2 p = free.remove(idx);
            TankModel ai = new TankModel(MOVEMENT_SPEED);
            ai.setPosition(new GridPoint2(p));
            world.addTank(ai);
            aiModels.add(ai);
        }

        inputHandler = new InputHandler(playerModel, movementRules, healthBarsController);
        inputHandler.setShooter(world);
        aiHandler = new AIHandler(movementRules, aiModels, botStrategy, world);
    }

    private List<GridPoint2> collectFreeCells(int w, int h) {
        Set<GridPoint2> forbidden = new HashSet<>();
        forbidden.add(new GridPoint2(playerModel.getCoordinates()));
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
        return free;
    }

    public void renderFrame(float deltaTime) {
        Set<GridPoint2> occupied = computeOccupied();
        Set<GridPoint2> reserved = computeReserved();
        movementRules.setOccupied(occupied, reserved);

        inputHandler.handle();
        aiHandler.handle();
        world.tick(deltaTime);

        levelGraphics.renderFrame(deltaTime);
    }

    private Set<GridPoint2> computeOccupied() {
        Set<GridPoint2> occ = new HashSet<>();
        for (TankModel t : world.getTanks()) {
            occ.add(new GridPoint2(t.getCoordinates()));
        }
        return occ;
    }

    private Set<GridPoint2> computeReserved() {
        Set<GridPoint2> res = new HashSet<>();
        for (TankModel t : world.getTanks()) {
            if (!t.isReady()) {
                res.add(new GridPoint2(t.getCoordinates()));
                res.add(new GridPoint2(t.getDestination()));
            }
        }
        return res;
    }

    public void dispose() {
        levelGraphics.dispose();
        aiModels.clear();
        obstacleModels.clear();
        playerModel = null;
    }
}
