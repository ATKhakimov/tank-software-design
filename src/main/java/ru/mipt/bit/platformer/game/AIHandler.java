package ru.mipt.bit.platformer.game;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.ai.AIShootingPolicy;
import ru.mipt.bit.platformer.ai.BotStrategy;
import ru.mipt.bit.platformer.input.CommandQueue;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.Shooter;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.List;

public class AIHandler {
    private final MovementRules rules;
    private final List<TankModel> tanks;
    private final BotStrategy strategy;
    private final Shooter shooter;
    private final AIShootingPolicy shootingPolicy;

    public AIHandler(MovementRules rules, List<TankModel> tanks, BotStrategy strategy, Shooter shooter, AIShootingPolicy shootingPolicy) {
        this.rules = rules;
        this.tanks = tanks;
        this.strategy = strategy;
        this.shooter = shooter;
        this.shootingPolicy = shootingPolicy;
    }

    public void enqueueCommands(CommandQueue queue) {
        for (TankModel tank : tanks) {
            if (!tank.isReady()) {
                continue;
            }
            if (shootingPolicy.shouldShoot(tank)) {
                queue.enqueue(() -> shooter.shoot(tank));
                continue;
            }
            for (Direction d : strategy.proposeDirections(tank, rules)) {
                queue.enqueue(() -> {
                    if (rules.attemptStartOrFace(tank, d)) {
                        strategy.onMoveStarted(tank, d);
                    }
                });
                break;
            }
        }
    }
}
