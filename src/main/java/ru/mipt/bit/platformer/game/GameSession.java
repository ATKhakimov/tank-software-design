package ru.mipt.bit.platformer.game;

import ru.mipt.bit.platformer.input.CommandQueue;
import ru.mipt.bit.platformer.model.MovementReservations;
import ru.mipt.bit.platformer.model.WorldModel;
import ru.mipt.bit.platformer.view.LevelGraphics;

public class GameSession {
    private final GameRuntime runtime;
    private final LevelGraphics levelGraphics;
    private final MovementReservations movementReservations;

    public GameSession(GameRuntime runtime, LevelGraphics levelGraphics) {
        this(runtime, levelGraphics, new MovementReservations());
    }

    public GameSession(GameRuntime runtime, LevelGraphics levelGraphics, MovementReservations movementReservations) {
        this.runtime = runtime;
        this.levelGraphics = levelGraphics;
        this.movementReservations = movementReservations;
    }

    public void initialize() {
        runtime.world().addObserver(levelGraphics);
        syncWorldToObserver(runtime.world());
    }

    private void syncWorldToObserver(WorldModel world) {
        runtime.world().getTanks().forEach(levelGraphics::objectAdded);
        runtime.world().getObstacles().forEach(levelGraphics::objectAdded);
        runtime.world().getBullets().forEach(levelGraphics::objectAdded);
    }

    public void renderFrame(float deltaTime) {
        MovementReservations.Snapshot snapshot = movementReservations.snapshot(runtime.world().getTanks());
        runtime.movementRules().setOccupied(snapshot.occupied(), snapshot.reserved());

        CommandQueue queue = new CommandQueue();
        runtime.inputHandler().enqueueCommands(queue);
        runtime.aiHandler().enqueueCommands(queue);
        queue.executeAll();

        runtime.world().tick(deltaTime);
        levelGraphics.renderFrame(deltaTime);
    }

    public void dispose() {
        levelGraphics.dispose();
    }
}
