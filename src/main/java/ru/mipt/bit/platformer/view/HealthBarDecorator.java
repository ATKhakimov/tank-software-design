package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.HealthProvider;
import ru.mipt.bit.platformer.util.TileMovement;

public class HealthBarDecorator<T extends Renderable> implements Renderable {
    private final T base;
    private final HealthProvider provider;
    private final TextureRegion region;
    private final Rectangle bounds;

    public HealthBarDecorator(T base, HealthProvider provider, Texture pixelTexture, Rectangle bounds) {
        this.base = base;
        this.provider = provider;
        this.region = new TextureRegion(pixelTexture);
        this.bounds = bounds;
    }

    @Override
    public void render(Batch batch) {
        base.render(batch);
        float bw = bounds.width;
        float bh = Math.max(2f, bounds.height * 0.12f);
        float bx = bounds.x;
        float by = bounds.y + bounds.height + 2f;
        float health = Math.max(0f, Math.min(1f, provider.getHealth()));
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
