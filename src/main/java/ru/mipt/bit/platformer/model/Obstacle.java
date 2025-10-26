// Интерфейс препятствия
package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface Obstacle extends GameObject {
    boolean occupies(GridPoint2 point);
}
