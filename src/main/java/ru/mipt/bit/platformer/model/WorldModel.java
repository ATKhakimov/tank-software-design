package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Direction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WorldModel implements Shooter {
    private final int width;
    private final int height;
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<TankModel> tanks = new ArrayList<>();
    private final List<BulletModel> bullets = new ArrayList<>();
    private final List<WorldObserver> observers = new ArrayList<>();
    private final float bulletSpeed;
    private final float bulletDamage;

    public WorldModel(int width, int height, float bulletSpeed, float bulletDamage) {
        this.width = width;
        this.height = height;
        this.bulletSpeed = bulletSpeed;
        this.bulletDamage = bulletDamage;
    }

    public void addObserver(WorldObserver observer) {
        observers.add(observer);
    }

    private void publishAdded(GameObject obj) {
        for (WorldObserver o : observers) o.objectAdded(obj);
    }

    private void publishRemoved(GameObject obj) {
        for (WorldObserver o : observers) o.objectRemoved(obj);
    }

    public void addObstacle(Obstacle o) {
        obstacles.add(o);
        publishAdded(o);
    }

    public void addTank(TankModel t) {
        tanks.add(t);
        publishAdded(t);
    }

    public void removeTank(TankModel t) {
        tanks.remove(t);
        publishRemoved(t);
    }

    public void removeBullet(BulletModel b) {
        bullets.remove(b);
        publishRemoved(b);
    }

    public boolean inBounds(GridPoint2 p) {
        return p.x >= 0 && p.y >= 0 && p.x < width && p.y < height;
    }

    public boolean isObstacle(GridPoint2 p) {
        for (Obstacle o : obstacles) if (o.getCoordinates().equals(p)) return true;
        return false;
    }

    public TankModel findTank(GridPoint2 p) {
        for (TankModel t : tanks) if (t.getCoordinates().equals(p)) return t;
        return null;
    }

    public BulletModel findBullet(GridPoint2 p, BulletModel except) {
        for (BulletModel b : bullets) if (b != except && b.getCoordinates().equals(p)) return b;
        return null;
    }

    @Override
    public void shoot(TankModel tank) {
        Direction d = Direction.fromRotation(tank.getRotation());
        GridPoint2 start = d.apply(tank.getCoordinates());
        if (!inBounds(start)) return;

        if (isObstacle(start)) return; 
        TankModel hit = findTank(start);
        if (hit != null) {
            damageTank(hit, bulletDamage);
            return;
        }
        BulletModel b = new BulletModel(start, d, bulletSpeed, bulletDamage);
        bullets.add(b);
        publishAdded(b);
    }

    public void damageTank(TankModel t, float dmg) {
        t.setHealth(t.getHealth() - dmg);
        if (t.getHealth() <= 0f) {
            removeTank(t);
        }
    }

    public void tick(float delta) {
        Iterator<BulletModel> it = bullets.iterator();
        while (it.hasNext()) {
            BulletModel b = it.next();
            b.tick(delta, this);
            if (b.isRemoved()) {
                it.remove();
                publishRemoved(b);
            }
        }
    }

    public List<TankModel> getTanks() {
        return tanks;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<BulletModel> getBullets() { return bullets; }
}
