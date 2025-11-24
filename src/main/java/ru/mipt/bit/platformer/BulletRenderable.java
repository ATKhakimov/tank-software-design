package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.BulletModel;
import ru.mipt.bit.platformer.util.GdxGameUtils;
import ru.mipt.bit.platformer.util.TileMovement;

public class BulletRenderable implements Renderable {
    private static Texture pixel;
    private static TextureRegion region;
    private final BulletModel model;
    private final Rectangle rectangle;

    public BulletRenderable(BulletModel model) {
        this.model = model;
        ensurePixel();
        rectangle = new Rectangle(0,0,16,16);
    }

    private static void ensurePixel() {
        if (pixel == null) {
            Pixmap pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
            pm.setColor(1f,1f,0f,1f);
            pm.drawPixel(0,0);
            pixel = new Texture(pm);
            pm.dispose();
            region = new TextureRegion(pixel);
        }
    }

    public BulletModel getModel() { return model; }

    @Override
    public void render(Batch batch) {
        GdxGameUtils.drawTextureRegionUnscaled(batch, region, rectangle, 0f);
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
