package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface CollisionContext {
    boolean inBounds(GridPoint2 position);

    boolean isObstacle(GridPoint2 position);

    TankModel findTank(GridPoint2 position);

    BulletModel findBullet(GridPoint2 position, BulletModel except);

    void applyDamage(TankModel target, float damage);
}
