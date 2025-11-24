package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.Direction;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BulletModelTest {

    @Test
    void bulletBlockedByObstacleNotCreated() {
        WorldModel world = new WorldModel(20,20,0.1f,0.2f);
        Obstacle obstacle = new Obstacle() {
            private final GridPoint2 c = new GridPoint2(6,5);
            @Override public boolean occupies(GridPoint2 p) { return c.equals(p); }
            @Override public GridPoint2 getCoordinates() { return c; }
            @Override public void setPosition(GridPoint2 position) { c.set(position); }
        };
        world.addObstacle(obstacle);
        TankModel shooter = new TankModel(1f); shooter.setPosition(new GridPoint2(5,5)); shooter.face(Direction.RIGHT);
        world.addTank(shooter);
        world.shoot(shooter);
        assertTrue(world.getBullets().isEmpty(), "Bullet should not be created because obstacle blocks start cell");
    }

    @Test
    void bulletDamagesAdjacentTankInsteadOfSpawning() {
        float dmg = 0.3f;
        WorldModel world = new WorldModel(10,1,0.1f,dmg);
        TankModel shooter = new TankModel(1f); shooter.setPosition(new GridPoint2(0,0)); shooter.face(Direction.RIGHT);
        TankModel target = new TankModel(1f); target.setPosition(new GridPoint2(1,0));
        world.addTank(shooter);
        world.addTank(target);
        float initialHealth = target.getHealth();
        world.shoot(shooter);
        assertEquals(initialHealth - dmg, target.getHealth(), 0.0001, "Target health reduced by damage");
        assertTrue(world.getBullets().isEmpty(), "No bullet spawned when immediate hit");
    }

    @Test
    void bulletMovesUntilOutOfBoundsAndIsRemoved() {
        WorldModel world = new WorldModel(3,1,0.1f,0.2f); 
        TankModel shooter = new TankModel(1f); shooter.setPosition(new GridPoint2(0,0)); shooter.face(Direction.RIGHT);
        world.addTank(shooter);
        world.shoot(shooter);
        assertEquals(1, world.getBullets().size(), "Bullet spawned ahead");
        world.tick(0.1f);
        assertEquals(1, world.getBullets().size(), "Bullet still inside bounds after first move");
        assertEquals(2, world.getBullets().get(0).getCoordinates().x, "Bullet advanced to x=2");
        world.tick(0.1f);
        assertTrue(world.getBullets().isEmpty(), "Bullet removed after leaving bounds");
    }
}
