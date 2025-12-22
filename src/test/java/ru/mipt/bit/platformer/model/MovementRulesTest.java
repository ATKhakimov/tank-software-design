package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MovementReservations;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MovementRulesTest {
    @Test
    void blocksOutOfBounds() {
        List<Obstacle> obstacles = new ArrayList<>();
        MovementRules rules = new MovementRules(3, 3, obstacles);
        TankModel tank = new TankModel(1f);
        tank.setPosition(new GridPoint2(0, 0));
    Set<GridPoint2> occupied = new HashSet<>();
    Set<GridPoint2> reserved = new HashSet<>();
    rules.setOccupied(occupied, reserved);

        boolean startedLeft = rules.attemptStartOrFace(tank, Direction.LEFT);
        assertFalse(startedLeft);
        assertEquals(-180f, tank.getRotation());

        boolean startedDown = rules.attemptStartOrFace(tank, Direction.DOWN);
        assertFalse(startedDown);
        assertEquals(-90f, tank.getRotation());

        boolean startedRight = rules.attemptStartOrFace(tank, Direction.RIGHT);
        assertTrue(startedRight);
        assertFalse(tank.isReady());
        assertEquals(new GridPoint2(1, 0), tank.getDestination());
    }

    @Test
    void respectsMovingTankReservation() {
        List<Obstacle> obstacles = new ArrayList<>();
        MovementRules rules = new MovementRules(5, 5, obstacles);
        TankModel a = new TankModel(1f);
        TankModel b = new TankModel(1f);
        a.setPosition(new GridPoint2(1, 1));
        b.setPosition(new GridPoint2(1, 2));

    Set<GridPoint2> occupied = new HashSet<>();
    Set<GridPoint2> reserved = new HashSet<>();
    rules.setOccupied(occupied, reserved);

    assertTrue(rules.attemptStartOrFace(a, Direction.UP));
    assertTrue(reserved.contains(new GridPoint2(1, 1)));
    assertTrue(reserved.contains(new GridPoint2(1, 2)));

        boolean bDown = rules.attemptStartOrFace(b, Direction.DOWN);
        assertFalse(bDown);
        assertTrue(b.isReady());

        boolean bUp = rules.attemptStartOrFace(b, Direction.UP);
        assertFalse(bUp);
        assertTrue(b.isReady());
    }

    @Test
    void snapshotBlocksEnteringReservedCells() {
        TankModel moving = new TankModel(1f);
        moving.setPosition(new GridPoint2(1, 1));
        moving.start(Direction.UP); // destination (1,2), not ready

        TankModel otherToFrom = new TankModel(1f);
        otherToFrom.setPosition(new GridPoint2(1, 0));

        TankModel otherToDest = new TankModel(1f);
        otherToDest.setPosition(new GridPoint2(2, 2));

        MovementReservations.Snapshot snap = new MovementReservations()
                .snapshot(Arrays.asList(moving, otherToFrom, otherToDest));

        MovementRules rules = new MovementRules(4, 4, new ArrayList<>());
        rules.setOccupied(snap.occupied(), snap.reserved());

        assertFalse(rules.attemptStartOrFace(otherToFrom, Direction.UP), "cannot step into moving tank current cell");
        assertTrue(otherToFrom.isReady());

        assertFalse(rules.attemptStartOrFace(otherToDest, Direction.LEFT), "cannot step into moving tank destination cell");
        assertTrue(otherToDest.isReady());
    }
}
