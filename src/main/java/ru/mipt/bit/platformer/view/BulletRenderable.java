package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.util.GdxGameUtils;
import ru.mipt.bit.platformer.util.TileMovement;

public class BulletRenderable implements Renderable {
    private final BulletModel model;
    private final Rectangle rectangle;
    private final TextureRegion region;

    public BulletRenderable(BulletModel model, Texture pixelTexture) {
        this.model = model;
        this.region = new TextureRegion(pixelTexture);
        rectangle = new Rectangle(0,0,16,16);
    }

    public BulletModel getModel() { return model; }

    @Override
    public void render(Batch batch) {
        batch.setColor(1f, 1f, 0f, 1f);
        GdxGameUtils.drawTextureRegionUnscaled(batch, region, rectangle, 0f);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void align(TileMovement movement) {
        GridPoint2 p = model.getCoordinates();
        movement.moveRectangleBetweenTileCenters(rectangle, p, p, 1f);
    }

    @Override
    public void dispose() { }

    public void update(TileMovement movement) {
        align(movement);
    }
}
