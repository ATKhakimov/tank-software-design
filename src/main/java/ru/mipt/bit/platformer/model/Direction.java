// Перечисление направлений: вектор направления, поворот и привязка клавиш
package ru.mipt.bit.platformer.model;

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

    public static Direction leftOf(Direction d) {
        switch (d) {
            case UP: return LEFT;
            case LEFT: return DOWN;
            case DOWN: return RIGHT;
            case RIGHT: return UP;
        }
        return d;
    }

    public static Direction rightOf(Direction d) {
        switch (d) {
            case UP: return RIGHT;
            case RIGHT: return DOWN;
            case DOWN: return LEFT;
            case LEFT: return UP;
        }
        return d;
    }

    public static Direction opposite(Direction d) {
        switch (d) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
        }
        return d;
    }

    public static Direction fromRotation(float rotation) {
        Direction best = RIGHT;
        float bestDiff = Math.abs(normalize(rotation) - normalize(RIGHT.rotation()));
        for (Direction d : values()) {
            float diff = Math.abs(normalize(rotation) - normalize(d.rotation()));
            if (diff < bestDiff) {
                bestDiff = diff;
                best = d;
            }
        }
        return best;
    }

    private static float normalize(float deg) {
        float a = deg % 360f;
        if (a <= -180f) a += 360f;
        if (a > 180f) a -= 360f;
        return a;
    }
}
