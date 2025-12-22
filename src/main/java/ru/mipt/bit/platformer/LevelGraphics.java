package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeObstacleModel;
import ru.mipt.bit.platformer.model.WorldObserver;
import ru.mipt.bit.platformer.util.TileMovement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelGraphics implements WorldObserver {
    private final GameField field;
    private final Batch batch;
    private final Texture tankTexture;
    private final Texture treeTexture;
    private final Texture pixelTexture;
    private final HealthBarsController healthBarsController;

    private Tank player;
    private Renderable playerWithHealth;
    private final List<Renderable> obstacles = new ArrayList<>();
    private final List<Tank> aiTanks = new ArrayList<>();
    private final List<Renderable> aiTanksWithHealth = new ArrayList<>();
    private final List<Renderable> bullets = new ArrayList<>();
    private final Map<TankModel, Tank> tankViews = new HashMap<>();

    public LevelGraphics(GameField field, Batch batch, Texture tankTexture, Texture treeTexture, Texture pixelTexture, HealthBarsController healthBarsController) {
        this.field = field;
        this.batch = batch;
        this.tankTexture = tankTexture;
        this.treeTexture = treeTexture;
        this.pixelTexture = pixelTexture;
        this.healthBarsController = healthBarsController;
    }

    public GameField getField() {
        return field;
    }

    public void renderFrame(float deltaTime) {
        TileMovement movement = field.movement();
        if (player != null) {
            player.update(movement, deltaTime);
        }
        for (Tank t : aiTanks) {
            t.update(movement, deltaTime);
        }
        for (Renderable b : bullets) {
            if (b instanceof BulletRenderable) {
                ((BulletRenderable) b).update(movement);
            }
        }

        field.render();

        batch.begin();
        if (healthBarsController != null && healthBarsController.isEnabled() && playerWithHealth != null) {
            playerWithHealth.render(batch);
        } else if (player != null) {
            player.render(batch);
        }
        for (Renderable r : obstacles) {
            r.render(batch);
        }
        if (healthBarsController != null && healthBarsController.isEnabled()) {
            for (Renderable t : aiTanksWithHealth) {
                t.render(batch);
            }
        } else {
            for (Tank t : aiTanks) {
                t.render(batch);
            }
        }
        for (Renderable b : bullets) {
            b.render(batch);
        }
        batch.end();
    }

    @Override
    public void objectAdded(GameObject object) {
        if (object instanceof TankModel) {
            TankModel tm = (TankModel) object;
            if (tankViews.containsKey(tm)) {
                return;
            }
            Tank tank = new Tank(tankTexture, tm);
            tank.align(field.movement());
            tankViews.put(tm, tank);
            if (player == null) {
                player = tank;
                playerWithHealth = new HealthBarTank(player, tm, pixelTexture);
                playerWithHealth.align(field.movement());
            } else {
                aiTanks.add(tank);
                Renderable decorated = new HealthBarTank(tank, tm, pixelTexture);
                decorated.align(field.movement());
                aiTanksWithHealth.add(decorated);
            }
        } else if (object instanceof Obstacle) {
            Obstacle o = (Obstacle) object;
            if (o instanceof TreeObstacleModel) {
                TreeObstacleModel tm = (TreeObstacleModel) o;
                Renderable r = new TreeObstacle(treeTexture, tm);
                r.align(field.movement());
                obstacles.add(r);
            }
        } else if (object instanceof BulletModel) {
            BulletModel bm = (BulletModel) object;
            BulletRenderable br = new BulletRenderable(bm, pixelTexture);
            br.align(field.movement());
            bullets.add(br);
        }
    }

    @Override
    public void objectRemoved(GameObject object) {
        if (object instanceof TankModel) {
            TankModel tm = (TankModel) object;
            Tank view = tankViews.remove(tm);
            if (view == null) {
                return;
            }
            if (player != null && player.getModel() == tm) {
                player = null;
                playerWithHealth = null;
            } else {
                for (int i = 0; i < aiTanks.size(); i++) {
                    if (aiTanks.get(i).getModel() == tm) {
                        aiTanks.remove(i);
                        aiTanksWithHealth.remove(i);
                        break;
                    }
                }
            }
        } else if (object instanceof Obstacle) {
            // obstacles are static in current implementation; skip removal
        } else if (object instanceof BulletModel) {
            for (int i = 0; i < bullets.size(); i++) {
                Renderable r = bullets.get(i);
                if (r instanceof BulletRenderable) {
                    BulletRenderable br = (BulletRenderable) r;
                    if (br.getModel() == object) {
                        bullets.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public void dispose() {
        bullets.clear();
        aiTanksWithHealth.clear();
        aiTanks.clear();
        obstacles.clear();
        tankViews.clear();
        player = null;
        playerWithHealth = null;
    }
}
