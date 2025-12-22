package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreeObstacleModelTest {

    @Test
    void testSetAndGetPosition() {
        TreeObstacleModel tree = new TreeObstacleModel();
        tree.setPosition(new GridPoint2(3, 5));
        
        assertEquals(new GridPoint2(3, 5), tree.getCoordinates());
    }

    @Test
    void testOccupiesReturnsTrueForSamePosition() {
        TreeObstacleModel tree = new TreeObstacleModel();
        tree.setPosition(new GridPoint2(2, 2));
        
        assertTrue(tree.occupies(new GridPoint2(2, 2)));
    }

    @Test
    void testOccupiesReturnsFalseForDifferentPosition() {
        TreeObstacleModel tree = new TreeObstacleModel();
        tree.setPosition(new GridPoint2(1, 1));
        
        assertFalse(tree.occupies(new GridPoint2(1, 2)));
        assertFalse(tree.occupies(new GridPoint2(2, 1)));
    }

    @Test
    void testInitialCoordinates() {
        TreeObstacleModel tree = new TreeObstacleModel();
        assertEquals(new GridPoint2(0, 0), tree.getCoordinates());
    }
}
