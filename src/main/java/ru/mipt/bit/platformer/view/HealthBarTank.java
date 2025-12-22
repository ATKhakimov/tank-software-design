package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.util.TileMovement;

public class HealthBarTank implements Renderable {
    private final Tank base;
    private final TankModel model;
    private final TextureRegion region;

    public HealthBarTank(Tank base, TankModel model, Texture pixelTexture) {
        this.base = base;
        this.model = model;
        this.region = new TextureRegion(pixelTexture);
    }

    public void update(TileMovement movement, float deltaTime) {
        base.update(movement, deltaTime);
    }

    @Override
    public void render(Batch batch) {
        base.render(batch);
        Rectangle r = base.getBounds();
        float bw = r.width;
        float bh = Math.max(2f, r.height * 0.12f);
        float bx = r.x;
        float by = r.y + r.height + 2f;
        float health = Math.max(0f, Math.min(1f, model.getHealth()));
        batch.setColor(1f, 0f, 0f, 1f);
        batch.draw(region, bx, by, bw, bh);
        batch.setColor(0f, 1f, 0f, 1f);
        batch.draw(region, bx, by, bw * health, bh);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void align(TileMovement movement) {
        base.align(movement);
    }

    @Override
    public void dispose() {
        base.dispose();
    }
}
