package ru.mipt.bit.platformer.ai;

import ru.mipt.bit.platformer.Direction;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class HoldCourseStrategy implements BotStrategy {
    private final Map<TankModel, Direction> last = new IdentityHashMap<>();

    @Override
    public List<Direction> proposeDirections(TankModel tank, MovementRules rules) {
        Direction current = last.getOrDefault(tank, Direction.fromRotation(tank.getRotation())) ;
        List<Direction> order = new ArrayList<>();
        if (rules.canStart(tank, current)) {
            order.add(current);
            last.put(tank, current);
            return order;
        }
        Direction left = Direction.leftOf(current);
        Direction right = Direction.rightOf(current);
        Direction back = Direction.opposite(current);
        order.add(left);
        order.add(right);
        order.add(back);
        return order;
    }

    public void onMoveStarted(TankModel tank, Direction d) {
        last.put(tank, d);
    }
}
