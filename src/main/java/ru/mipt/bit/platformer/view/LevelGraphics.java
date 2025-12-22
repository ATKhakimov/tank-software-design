package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.model.WorldObserver;
import ru.mipt.bit.platformer.util.TileMovement;
import ru.mipt.bit.platformer.view.HealthBarsController;
import ru.mipt.bit.platformer.view.BulletRenderable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelGraphics implements WorldObserver {
    private final GameField field;
    private final Batch batch;
    private final RenderableFactory renderableFactory;
    private final HealthBarsController healthBarsController;

    private Tank player;
    private Renderable playerWithHealth;
    private final List<Renderable> obstacles = new ArrayList<>();
    private final List<Tank> aiTanks = new ArrayList<>();
    private final List<Renderable> aiTanksWithHealth = new ArrayList<>();
    private final List<Renderable> bullets = new ArrayList<>();
    private final Map<TankModel, Tank> tankViews = new HashMap<>();

    public LevelGraphics(GameField field, Batch batch, RenderableFactory renderableFactory, HealthBarsController healthBarsController) {
        this.field = field;
        this.batch = batch;
        this.renderableFactory = renderableFactory;
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
        boolean showHealth = healthBarsController != null && healthBarsController.isEnabled();
        if (showHealth && playerWithHealth != null) {
            playerWithHealth.render(batch);
        } else if (player != null) {
            player.render(batch);
        }
        for (Renderable r : obstacles) {
            r.render(batch);
        }
        List<Renderable> aiRenderables = showHealth && !aiTanksWithHealth.isEmpty()
            ? aiTanksWithHealth
            : new ArrayList<>(aiTanks);
        for (Renderable t : aiRenderables) {
            t.render(batch);
        }
        for (Renderable b : bullets) {
            b.render(batch);
        }
        batch.end();
    }

    @Override
    public void objectAdded(GameObject object) {
        RenderableCreation creation = renderableFactory.create(object, field.movement(), player == null && object instanceof TankModel);
        switch (creation.kind()) {
            case PLAYER_TANK:
                if (object instanceof TankModel) {
                    TankModel tm = (TankModel) object;
                    if (tankViews.containsKey(tm)) {
                        return;
                    }
                    Tank tank = (Tank) creation.main();
                    tankViews.put(tm, tank);
                    player = tank;
                    playerWithHealth = creation.decorated();
                }
                break;
            case AI_TANK:
                if (object instanceof TankModel) {
                    TankModel tm = (TankModel) object;
                    if (tankViews.containsKey(tm)) {
                        return;
                    }
                    Tank tank = (Tank) creation.main();
                    tankViews.put(tm, tank);
                    aiTanks.add(tank);
                    if (creation.decorated() != null) {
                        aiTanksWithHealth.add(creation.decorated());
                    } else {
                        aiTanksWithHealth.add(tank);
                    }
                }
                break;
            case OBSTACLE:
                if (creation.main() != null) {
                    obstacles.add(creation.main());
                }
                break;
            case BULLET:
                if (creation.main() != null) {
                    bullets.add(creation.main());
                }
                break;
            default:
                break;
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
