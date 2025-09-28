// Реализовано перечисление направлений игрока
package ru.mipt.bit.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;

public enum Direction {
    UP(0, 1, 90f, Input.Keys.UP, Input.Keys.W),
    LEFT(-1, 0, -180f, Input.Keys.LEFT, Input.Keys.A),
    DOWN(0, -1, -90f, Input.Keys.DOWN, Input.Keys.S),
    RIGHT(1, 0, 0f, Input.Keys.RIGHT, Input.Keys.D);

    private final int dx;
    private final int dy;
    private final float rotation;
    private final int primaryKey;
    private final int secondaryKey;

    Direction(int dx, int dy, float rotation, int primaryKey, int secondaryKey) {
        this.dx = dx;
        this.dy = dy;
        this.rotation = rotation;
        this.primaryKey = primaryKey;
        this.secondaryKey = secondaryKey;
    }

    public boolean isPressed() {
        return Gdx.input.isKeyPressed(primaryKey) || Gdx.input.isKeyPressed(secondaryKey);
    }

    public GridPoint2 apply(GridPoint2 coordinates) {
        return new GridPoint2(coordinates).add(dx, dy);
    }

    public float rotation() {
        return rotation;
    }
}
