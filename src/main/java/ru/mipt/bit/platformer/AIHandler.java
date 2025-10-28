// Простейший AI контроллер c переключаемыми стратегиями
package ru.mipt.bit.platformer;

import ru.mipt.bit.platformer.ai.BotStrategy;
import ru.mipt.bit.platformer.ai.HoldCourseStrategy;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.List;

public class AIHandler {
    private final MovementRules rules;
    private final List<TankModel> tanks;
    private final BotStrategy strategy;

    public AIHandler(MovementRules rules, List<TankModel> tanks, BotStrategy strategy) {
        this.rules = rules;
        this.tanks = tanks;
        this.strategy = strategy;
    }

    public void handle() {
        for (TankModel tank : tanks) {
            if (!tank.isReady()) continue;
            for (Direction d : strategy.proposeDirections(tank, rules)) {
                if (rules.attemptStartOrFace(tank, d)) {
                    if (strategy instanceof HoldCourseStrategy) {
                        ((HoldCourseStrategy) strategy).onMoveStarted(tank, d);
                    }
                    break;
                }
            }
        }
    }
}
