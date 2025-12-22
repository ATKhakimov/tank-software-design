package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.Direction;

import static org.junit.jupiter.api.Assertions.*;

class TankModelTest {

    @Test
    void testInitialPosition() {
        TankModel tank = new TankModel(0.5f);
        tank.setPosition(new GridPoint2(2, 3));
        
        assertEquals(new GridPoint2(2, 3), tank.getCoordinates());
        assertEquals(new GridPoint2(2, 3), tank.getDestination());
        assertTrue(tank.isReady());
    }

    @Test
    void testStartMovement() {
        TankModel tank = new TankModel(0.5f);
        tank.setPosition(new GridPoint2(1, 1));
        
        tank.start(Direction.UP);
        
        assertEquals(new GridPoint2(1, 2), tank.getDestination());
        assertFalse(tank.isReady());
        assertEquals(90f, tank.getRotation());
    }

    @Test
    void testFaceDirection() {
        TankModel tank = new TankModel(0.5f);
        tank.setPosition(new GridPoint2(0, 0));
        
        tank.face(Direction.LEFT);
        
        assertEquals(-180f, tank.getRotation());
        assertTrue(tank.isReady());
    }

    @Test
    void testProgressUpdate() {
        TankModel tank = new TankModel(1.0f);
        tank.setPosition(new GridPoint2(0, 0));
        tank.start(Direction.RIGHT);
        
        tank.updateProgress(0.5f);
        
        assertEquals(0.5f, tank.getProgress(), 0.01f);
        assertFalse(tank.isReady());
        
        tank.updateProgress(0.5f);
        
        assertTrue(tank.isReady());
        assertEquals(new GridPoint2(1, 0), tank.getCoordinates());
    }

    @Test
    void testCannotStartWhileMoving() {
        TankModel tank = new TankModel(1.0f);
        tank.setPosition(new GridPoint2(0, 0));
        tank.start(Direction.UP);
        
        GridPoint2 firstDestination = new GridPoint2(tank.getDestination());
        tank.start(Direction.RIGHT);
        
        assertEquals(firstDestination, tank.getDestination());
        assertEquals(0f, tank.getRotation());
    }

    @Test
    void testGetSpeed() {
        TankModel tank = new TankModel(0.3f);
        assertEquals(0.3f, tank.getSpeed());
    }
}
