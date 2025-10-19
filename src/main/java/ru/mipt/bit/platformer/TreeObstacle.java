// Графическое представление препятствия дерева
package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.TreeObstacleModel;
import ru.mipt.bit.platformer.util.GdxGameUtils;
import ru.mipt.bit.platformer.util.TileMovement;

public class TreeObstacle {
    private final Texture texture;
    private final TextureRegion region;
    private final Rectangle rectangle;
    private final TreeObstacleModel model;

    public TreeObstacle(Texture texture, TreeObstacleModel model) {
        this.texture = texture;
        this.model = model;
        this.region = new TextureRegion(texture);
        this.rectangle = GdxGameUtils.createBoundingRectangle(region);
    }

    public TreeObstacleModel getModel() {
        return model;
    }

    public void align(TileMovement movement) {
        movement.moveRectangleBetweenTileCenters(rectangle, model.getCoordinates(), model.getCoordinates(), 1f);
    }

    public void render(Batch batch) {
        GdxGameUtils.drawTextureRegionUnscaled(batch, region, rectangle, 0f);
    }

    public void dispose() {
        texture.dispose();
    }
}
