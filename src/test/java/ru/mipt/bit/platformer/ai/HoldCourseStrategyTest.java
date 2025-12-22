package ru.mipt.bit.platformer.ai;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.Direction;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class HoldCourseStrategyTest {
    @Test
    void keepsCourseWhenFree() {
        List<Obstacle> obstacles = new ArrayList<>();
        MovementRules rules = new MovementRules(5, 5, obstacles);
        TankModel t = new TankModel(1f);
        t.setPosition(new GridPoint2(1, 1));
        rules.setOccupied(new HashSet<>(), new HashSet<>());

        HoldCourseStrategy s = new HoldCourseStrategy();
        AIProbe probe = new AIProbe(rules, t, s);
        probe.step();

        assertFalse(t.isReady());
        assertEquals(new GridPoint2(2, 1), t.getDestination());
    }

    @Test
    void onBlockTriesLeftRightBack() {
        List<Obstacle> obstacles = new ArrayList<>();
        MovementRules rules = new MovementRules(5, 5, obstacles);
        TankModel t = new TankModel(1f);
        t.setPosition(new GridPoint2(1, 1));
        Set<GridPoint2> occ = new HashSet<>();
        Set<GridPoint2> res = new HashSet<>();
        // Block forward (RIGHT)
        occ.add(new GridPoint2(2, 1));
        rules.setOccupied(occ, res);

        HoldCourseStrategy s = new HoldCourseStrategy();
        AIProbe probe = new AIProbe(rules, t, s);
        probe.step();

        assertFalse(t.isReady());
        assertEquals(new GridPoint2(1, 2), t.getDestination());
    }

    static class AIProbe {
        private final MovementRules rules;
        private final TankModel tank;
        private final HoldCourseStrategy strategy;

        AIProbe(MovementRules rules, TankModel tank, HoldCourseStrategy strategy) {
            this.rules = rules;
            this.tank = tank;
            this.strategy = strategy;
        }

        void step() {
            for (Direction d : strategy.proposeDirections(tank, rules)) {
                if (rules.attemptStartOrFace(tank, d)) {
                    strategy.onMoveStarted(tank, d);
                    break;
                }
            }
        }
    }
}
