// Простейший AI контроллер случайного движения
package ru.mipt.bit.platformer;

import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AIHandler {
    private final MovementRules rules;
    private final List<TankModel> tanks;
    private final Random random = new Random();

    public AIHandler(MovementRules rules, List<TankModel> tanks) {
        this.rules = rules;
        this.tanks = tanks;
    }

    public void handle() {
        for (TankModel tank : tanks) {
            if (!tank.isReady()) {
                continue;
            }
            List<Direction> dirs = new ArrayList<>();
            Collections.addAll(dirs, Direction.values());
            // Случайное блуждание
            Collections.shuffle(dirs, random);
            for (Direction d : dirs) {
                if (rules.attemptStartOrFace(tank, d)) {
                    break;
                }
            }
        }
    }
}
