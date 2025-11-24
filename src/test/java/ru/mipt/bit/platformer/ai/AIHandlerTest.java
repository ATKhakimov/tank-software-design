package ru.mipt.bit.platformer.ai;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.AIHandler;
import ru.mipt.bit.platformer.Direction;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.Shooter;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AIHandlerTest {

    static class CountingShooter implements Shooter {
        int shots = 0;
        @Override
        public void shoot(TankModel tank) { shots++; }
    }

    @Test
    void handlerStartsMovementEventuallyDespiteRandomShooting() {
        MovementRules rules = new MovementRules(20, 20, Collections.emptyList());
        rules.setOccupied(new HashSet<>(), new HashSet<>());
        TankModel tank = new TankModel(0.5f);
        tank.setPosition(new GridPoint2(5,5));
        CountingShooter shooter = new CountingShooter();
        BotStrategy strategy = (t, r) -> Collections.singletonList(Direction.UP);

        AIHandler handler = new AIHandler(rules, Collections.singletonList(tank), strategy, shooter);

        int attempts = 0;
        while (tank.getProgress() == 1f && attempts < 50) { // loop until movement started
            handler.handle();
            attempts++;
        }
        assertNotEquals(1f, tank.getProgress(), "Movement should have started (progress reset) within attempts");
        assertEquals(new GridPoint2(5,6), tank.getDestination(), "Destination should be one tile up");
    }

    @Test
    void holdCourseStrategyRemembersLastSuccessfulDirection() {
        MovementRules rules = new MovementRules(20, 20, Collections.emptyList());
        rules.setOccupied(new HashSet<>(), new HashSet<>());
        TankModel tank = new TankModel(0.2f); // fast to finish
        tank.setPosition(new GridPoint2(10,10));
        CountingShooter shooter = new CountingShooter();
        HoldCourseStrategy strategy = new HoldCourseStrategy();

        AIHandler handler = new AIHandler(rules, Collections.singletonList(tank), strategy, shooter);
        int attempts = 0;
        while (tank.getProgress() == 1f && attempts < 50) {
            handler.handle();
            attempts++;
        }
        assertNotEquals(1f, tank.getProgress(), "Movement should have started");
        tank.updateProgress(10f);
        assertTrue(tank.isReady(), "Tank should be ready after progress update");
        rules.setOccupied(new HashSet<>(), new HashSet<>());
        Direction lastMoved = Direction.fromRotation(tank.getRotation());
        var proposed = strategy.proposeDirections(tank, rules);
        assertEquals(1, proposed.size(), "Expected single preferred direction after success");
        assertEquals(lastMoved, proposed.get(0), "Expected strategy to keep moving in last direction");
    }
}
