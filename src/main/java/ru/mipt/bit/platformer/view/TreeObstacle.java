package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.TreeObstacleModel;
import ru.mipt.bit.platformer.util.GdxGameUtils;
import ru.mipt.bit.platformer.util.TileMovement;

public class TreeObstacle implements Renderable {
    private final Texture texture;
    private final TextureRegion region;
    private final TreeObstacleModel model;
    private final Rectangle rectangle;

    public TreeObstacle(Texture texture, TreeObstacleModel model) {
        this.texture = texture;
        this.model = model;
        this.region = new TextureRegion(texture);
        this.rectangle = GdxGameUtils.createBoundingRectangle(region);
    }

    @Override
    public void render(Batch batch) {
        GdxGameUtils.drawTextureRegionUnscaled(batch, region, rectangle, 0f);
    }

    @Override
    public void align(TileMovement movement) {
        movement.moveRectangleBetweenTileCenters(rectangle, model.getCoordinates(), model.getCoordinates(), 1f);
    }

    @Override
    public void dispose() {
    }
}
