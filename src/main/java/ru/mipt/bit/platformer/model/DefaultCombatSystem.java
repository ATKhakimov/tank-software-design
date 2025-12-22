package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class DefaultCombatSystem implements CombatSystem {
    private final float bulletSpeed;
    private final float bulletDamage;

    public DefaultCombatSystem(float bulletSpeed, float bulletDamage) {
        this.bulletSpeed = bulletSpeed;
        this.bulletDamage = bulletDamage;
    }

    @Override
    public void shoot(TankModel tank, CombatContext context) {
        Direction direction = Direction.fromRotation(tank.getRotation());
        GridPoint2 start = direction.apply(tank.getCoordinates());
        if (!context.inBounds(start)) {
            return;
        }
        if (context.isObstacle(start)) {
            return;
        }
        TankModel hit = context.findTank(start);
        if (hit != null) {
            applyDamage(hit, bulletDamage, context);
            return;
        }
        context.addBullet(new BulletModel(start, direction, bulletSpeed, bulletDamage));
    }

    @Override
    public void applyDamage(TankModel target, float damage, CombatContext context) {
        target.setHealth(target.getHealth() - damage);
        if (target.getHealth() <= 0f) {
            context.removeTank(target);
        }
    }
}
