package ru.mipt.bit.platformer.ai;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.List;

public interface BotStrategy {
    List<Direction> proposeDirections(TankModel tank, MovementRules rules);

    default void onMoveStarted(TankModel tank, Direction direction) {
    }
}
