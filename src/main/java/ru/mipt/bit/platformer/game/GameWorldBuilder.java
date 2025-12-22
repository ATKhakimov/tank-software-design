package ru.mipt.bit.platformer.game;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.game.AIHandler;
import ru.mipt.bit.platformer.view.HealthBarsController;
import ru.mipt.bit.platformer.input.GdxInputSource;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.input.InputSource;
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
import java.util.List;
import java.util.Random;

public class GameWorldBuilder {
    private final WorldModelFactory worldFactory;
    private final BotStrategy botStrategy;
    private final HealthBarsController healthBarsController;
    private final LevelLoader levelLoader;
    private final InputSource inputSource;
    private final int botsCount;
    private final float movementSpeed;
    private final Random random;

    public GameWorldBuilder(WorldModelFactory worldFactory,
                            BotStrategy botStrategy,
                            HealthBarsController healthBarsController,
                            LevelLoader levelLoader,
                            InputSource inputSource,
                            int botsCount,
                            float movementSpeed,
                            Random random) {
        this.worldFactory = worldFactory;
        this.botStrategy = botStrategy;
        this.healthBarsController = healthBarsController;
        this.levelLoader = levelLoader;
        this.inputSource = inputSource;
        this.botsCount = botsCount;
        this.movementSpeed = movementSpeed;
        this.random = random;
    }

    public GameWorldBuilder(WorldModelFactory worldFactory,
                            BotStrategy botStrategy,
                            HealthBarsController healthBarsController,
                            LevelLoader levelLoader,
                            int botsCount) {
        this(worldFactory, botStrategy, healthBarsController, levelLoader, new GdxInputSource(), botsCount, 0.4f, new Random());
    }

    public GameRuntime build() {
        LevelData data = levelLoader.load();
        int w = data.getWidth();
        int h = data.getHeight();

        WorldModel world = worldFactory.create(w, h);

        TankModel playerModel = new TankModel(movementSpeed);
        playerModel.setPosition(new GridPoint2(data.getPlayerStart().x, data.getPlayerStart().y));
        world.addTank(playerModel);

        List<Obstacle> obstacles = new ArrayList<>();
        for (GridPoint2 pos : data.getTreePositions()) {
            TreeObstacleModel obstacle = new TreeObstacleModel();
            obstacle.setPosition(new GridPoint2(pos.x, pos.y));
            obstacles.add(obstacle);
            world.addObstacle(obstacle);
        }

        MovementRules movementRules = new MovementRules(w, h, obstacles);

        List<TankModel> aiModels = spawnAiTanks(w, h, obstacles, playerModel);
        for (TankModel ai : aiModels) {
            world.addTank(ai);
        }

        InputHandler inputHandler = new InputHandler(inputSource, playerModel, movementRules, healthBarsController);
        inputHandler.setShooter(world);
        AIHandler aiHandler = new AIHandler(
            movementRules,
            aiModels,
            botStrategy,
            world,
            new ru.mipt.bit.platformer.ai.RandomShootingPolicy(random, 0.1f));

        return new GameRuntime(world, movementRules, inputHandler, aiHandler, playerModel, aiModels, obstacles);
    }

    private List<TankModel> spawnAiTanks(int width, int height, List<Obstacle> obstacles, TankModel playerModel) {
        List<GridPoint2> free = collectFreeCells(width, height, obstacles, playerModel);
        int aiCount = Math.min(botsCount, free.size());
        List<TankModel> aiModels = new ArrayList<>();
        for (int i = 0; i < aiCount; i++) {
            int idx = random.nextInt(free.size());
            GridPoint2 pos = free.remove(idx);
            TankModel ai = new TankModel(movementSpeed);
            ai.setPosition(new GridPoint2(pos));
            aiModels.add(ai);
        }
        return aiModels;
    }

    private List<GridPoint2> collectFreeCells(int width, int height, List<Obstacle> obstacles, TankModel playerModel) {
        List<GridPoint2> free = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                GridPoint2 p = new GridPoint2(x, y);
                if (isFreeCell(p, obstacles, playerModel)) {
                    free.add(p);
                }
            }
        }
        return free;
    }

    private boolean isFreeCell(GridPoint2 point, List<Obstacle> obstacles, TankModel playerModel) {
        if (playerModel.getCoordinates().equals(point)) {
            return false;
        }
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getCoordinates().equals(point)) {
                return false;
            }
        }
        return true;
    }
}
