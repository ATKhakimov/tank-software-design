// Реализована абстракция препятствия дерева
package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.util.GdxGameUtils;
import ru.mipt.bit.platformer.util.TileMovement;

public class TreeObstacle {
    private final Texture texture;
    private final TextureRegion region;
    private final Rectangle rectangle;
    private final GridPoint2 coordinates = new GridPoint2();

    public TreeObstacle(Texture texture) {
        this.texture = texture;
        this.region = new TextureRegion(texture);
        this.rectangle = GdxGameUtils.createBoundingRectangle(region);
    }

    public void setPosition(GridPoint2 position) {
        coordinates.set(position);
    }

    public void align(TileMovement movement) {
        movement.moveRectangleBetweenTileCenters(rectangle, coordinates, coordinates, 1f);
    }

    public boolean occupies(GridPoint2 point) {
        return coordinates.equals(point);
    }

    public void render(Batch batch) {
        GdxGameUtils.drawTextureRegionUnscaled(batch, region, rectangle, 0f);
    }

    public GridPoint2 getCoordinates() {
        return coordinates;
    }

    public void dispose() {
        texture.dispose();
    }
}
