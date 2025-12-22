package ru.mipt.bit.platformer.view;

import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.util.TileMovement;

public interface RenderableFactory {
    RenderableCreation create(GameObject object, TileMovement movement, boolean isPlayerTank);
}