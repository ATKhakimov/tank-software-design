// Реализована абстракция танка
package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.util.GdxGameUtils;
import ru.mipt.bit.platformer.util.TileMovement;

public class Tank {
    private final Texture texture;
    private final TextureRegion region;
    private final Rectangle rectangle;
    private final GridPoint2 coordinates = new GridPoint2();
    private final GridPoint2 destination = new GridPoint2();
    private final float speed;

    private float progress = 1f;
    private float rotation = 0f;

    public Tank(Texture texture, float speed) {
        this.texture = texture;
        this.region = new TextureRegion(texture);
        this.rectangle = GdxGameUtils.createBoundingRectangle(region);
        this.speed = speed;
    }

    public void setPosition(GridPoint2 position) {
        coordinates.set(position);
        destination.set(position);
        progress = 1f;
    }

    public boolean isReady() {
        return progress >= 1f;
    }

    public void start(Direction direction) {
        if (isReady()) {
            destination.set(direction.apply(coordinates));
            progress = 0f;
        }
        rotation = direction.rotation();
    }

    public void face(Direction direction) {
        rotation = direction.rotation();
    }

    public void update(TileMovement movement, float deltaTime) {
        movement.moveRectangleBetweenTileCenters(rectangle, coordinates, destination, progress);
        progress = GdxGameUtils.continueProgress(progress, deltaTime, speed);
        if (progress >= 1f) {
            coordinates.set(destination);
        }
    }

    public void render(Batch batch) {
        GdxGameUtils.drawTextureRegionUnscaled(batch, region, rectangle, rotation);
    }

    public void align(TileMovement movement) {
        movement.moveRectangleBetweenTileCenters(rectangle, coordinates, destination, 1f);
    }

    public GridPoint2 getCoordinates() {
        return coordinates;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public float getRotation() {
        return rotation;
    }

    public void dispose() {
        texture.dispose();
    }
}
