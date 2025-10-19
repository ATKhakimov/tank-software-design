// Перечисление направлений: вектор направления, поворот и привязка клавиш
package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

public enum Direction {
    UP(0, 1, 90f),
    LEFT(-1, 0, -180f),
    DOWN(0, -1, -90f),
    RIGHT(1, 0, 0f);

    private final int dx;
    private final int dy;
    private final float rotation;
    private final Vector2 vector;

    Direction(int dx, int dy, float rotation) {
        this.dx = dx;
        this.dy = dy;
        this.rotation = rotation;
        this.vector = new Vector2(dx, dy);
    }

    public GridPoint2 apply(GridPoint2 coordinates) {
        return new GridPoint2(coordinates).add(dx, dy);
    }

    public float rotation() {
        return rotation;
    }

    public Vector2 vector() {
        return vector.cpy();
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }
}
