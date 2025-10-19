// Графическое представление танка
package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.util.GdxGameUtils;
import ru.mipt.bit.platformer.util.TileMovement;

public class Tank {
    private final Texture texture;
    private final TextureRegion region;
    private final Rectangle rectangle;
    private final TankModel model;

    public Tank(Texture texture, TankModel model) {
        this.texture = texture;
        this.model = model;
        this.region = new TextureRegion(texture);
        this.rectangle = GdxGameUtils.createBoundingRectangle(region);
    }

    public TankModel getModel() {
        return model;
    }

    public void update(TileMovement movement, float deltaTime) {
        model.updateProgress(deltaTime);
        movement.moveRectangleBetweenTileCenters(rectangle, model.getCoordinates(), model.getDestination(), model.getProgress());
    }

    public void render(Batch batch) {
        GdxGameUtils.drawTextureRegionUnscaled(batch, region, rectangle, model.getRotation());
    }

    public void align(TileMovement movement) {
        movement.moveRectangleBetweenTileCenters(rectangle, model.getCoordinates(), model.getDestination(), 1f);
    }

    public void dispose() {
        texture.dispose();
    }
}
