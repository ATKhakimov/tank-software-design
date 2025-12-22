package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.CollisionContext;

public class BulletModel implements GameObject {
    private final GridPoint2 coordinates = new GridPoint2();
    private final Direction direction;
    private final float speed;
    private float elapsed = 0f;
    private boolean removed = false;
    private final float damage;

    public BulletModel(GridPoint2 start, Direction direction, float speed, float damage) {
        this.coordinates.set(start);
        this.direction = direction;
        this.speed = speed;
        this.damage = damage;
    }

    @Override
    public GridPoint2 getCoordinates() {
        return coordinates;
    }

    @Override
    public void setPosition(GridPoint2 position) {
        coordinates.set(position);
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isRemoved() {
        return removed;
    }

    public float getDamage() {
        return damage;
    }

    public void tick(float delta, CollisionContext context) {
        if (removed) return;
        elapsed += delta;
        if (elapsed < speed) return;
        elapsed = 0f;
        GridPoint2 next = direction.apply(coordinates);
        if (!context.inBounds(next)) {
            removed = true;
            return;
        }
        if (context.isObstacle(next)) {
            removed = true;
            return;
        }
        TankModel hitTank = context.findTank(next);
        if (hitTank != null) {
            context.applyDamage(hitTank, damage);
            removed = true;
            return;
        }
        coordinates.set(next);
        BulletModel other = context.findBullet(next, this);
        if (other != null) {
            removed = true;
            other.removed = true;
        }
    }
}
