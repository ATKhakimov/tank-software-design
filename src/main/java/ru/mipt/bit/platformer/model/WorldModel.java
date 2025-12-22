package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.List;

public class WorldModel implements Shooter, CombatContext, CollisionContext {
    private final int width;
    private final int height;
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<TankModel> tanks = new ArrayList<>();
    private final List<BulletModel> bullets = new ArrayList<>();
    private final List<WorldObserver> observers = new ArrayList<>();
    private final CombatSystem combatSystem;
    private final BulletSimulator bulletSimulator;

    public WorldModel(int width, int height, CombatSystem combatSystem, BulletSimulator bulletSimulator) {
        this.width = width;
        this.height = height;
        this.combatSystem = combatSystem;
        this.bulletSimulator = bulletSimulator;
    }

    public WorldModel(int width, int height, float bulletSpeed, float bulletDamage) {
        this(width, height, new DefaultCombatSystem(bulletSpeed, bulletDamage), new BulletSimulator());
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
        combatSystem.shoot(tank, this);
    }

    public void tick(float delta) {
        bulletSimulator.tick(delta, bullets, this, this::publishRemoved);
    }

    public List<TankModel> getTanks() {
        return tanks;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<BulletModel> getBullets() { return bullets; }

    @Override
    public void addBullet(BulletModel bullet) {
        bullets.add(bullet);
        publishAdded(bullet);
    }

    @Override
    public void removeTank(TankModel tank) {
        if (tanks.remove(tank)) {
            publishRemoved(tank);
        }
    }

    @Override
    public void applyDamage(TankModel tank, float damage) {
        combatSystem.applyDamage(tank, damage, this);
    }
}
