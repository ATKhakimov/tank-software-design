package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.Texture;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeObstacleModel;
import ru.mipt.bit.platformer.util.TileMovement;

public class GameObjectRenderableFactory implements RenderableFactory {
    private final Texture tankTexture;
    private final Texture treeTexture;
    private final Texture pixelTexture;
    private final HealthBarsController healthBarsController;

    public GameObjectRenderableFactory(Texture tankTexture,
                                       Texture treeTexture,
                                       Texture pixelTexture,
                                       HealthBarsController healthBarsController) {
        this.tankTexture = tankTexture;
        this.treeTexture = treeTexture;
        this.pixelTexture = pixelTexture;
        this.healthBarsController = healthBarsController;
    }

    @Override
    public RenderableCreation create(GameObject object, TileMovement movement, boolean isPlayerTank) {
        if (object instanceof TankModel) {
            TankModel tm = (TankModel) object;
            Tank tank = new Tank(tankTexture, tm);
            tank.align(movement);
            Renderable decorated = null;
            if (healthBarsController != null) {
                decorated = new HealthBarDecorator<>(tank, tm, pixelTexture, tank.getBounds());
                decorated.align(movement);
            }
            RenderableCreation.Kind kind = isPlayerTank
                    ? RenderableCreation.Kind.PLAYER_TANK
                    : RenderableCreation.Kind.AI_TANK;
            return new RenderableCreation(tank, decorated, kind);
        }
        if (object instanceof Obstacle) {
            if (object instanceof TreeObstacleModel) {
                TreeObstacleModel tm = (TreeObstacleModel) object;
                Renderable r = new TreeObstacle(treeTexture, tm);
                r.align(movement);
                return new RenderableCreation(r, null, RenderableCreation.Kind.OBSTACLE);
            }
        }
        if (object instanceof BulletModel) {
            BulletRenderable br = new BulletRenderable((BulletModel) object, pixelTexture);
            br.align(movement);
            return new RenderableCreation(br, null, RenderableCreation.Kind.BULLET);
        }
        return new RenderableCreation(null, null, RenderableCreation.Kind.UNKNOWN);
    }
}