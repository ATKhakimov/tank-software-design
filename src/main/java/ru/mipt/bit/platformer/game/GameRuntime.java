package ru.mipt.bit.platformer.game;

import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.WorldModel;

import java.util.Collections;
import java.util.List;

public class GameRuntime {
    private final WorldModel world;
    private final MovementRules movementRules;
    private final InputHandler inputHandler;
    private final AIHandler aiHandler;
    private final TankModel playerTank;
    private final List<TankModel> aiTanks;
    private final List<Obstacle> obstacles;

    public GameRuntime(WorldModel world,
                       MovementRules movementRules,
                       InputHandler inputHandler,
                       AIHandler aiHandler,
                       TankModel playerTank,
                       List<TankModel> aiTanks,
                       List<Obstacle> obstacles) {
        this.world = world;
        this.movementRules = movementRules;
        this.inputHandler = inputHandler;
        this.aiHandler = aiHandler;
        this.playerTank = playerTank;
        this.aiTanks = aiTanks;
        this.obstacles = obstacles;
    }

    public WorldModel world() {
        return world;
    }

    public MovementRules movementRules() {
        return movementRules;
    }

    public InputHandler inputHandler() {
        return inputHandler;
    }

    public AIHandler aiHandler() {
        return aiHandler;
    }

    public TankModel playerTank() {
        return playerTank;
    }

    public List<TankModel> aiTanks() {
        return Collections.unmodifiableList(aiTanks);
    }

    public List<Obstacle> obstacles() {
        return Collections.unmodifiableList(obstacles);
    }
}
