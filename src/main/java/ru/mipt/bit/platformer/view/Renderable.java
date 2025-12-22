package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.util.TileMovement;

public interface Renderable {
    void render(Batch batch);
    void align(TileMovement movement);
    void dispose();
}
