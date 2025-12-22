package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface CombatContext {
    boolean inBounds(GridPoint2 position);

    boolean isObstacle(GridPoint2 position);

    TankModel findTank(GridPoint2 position);

    void addBullet(BulletModel bullet);

    void removeTank(TankModel tank);
}
