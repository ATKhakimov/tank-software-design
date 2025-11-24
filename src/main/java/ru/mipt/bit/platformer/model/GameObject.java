// Интерфейс модели игрового объекта
package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface GameObject {
    GridPoint2 getCoordinates();
    void setPosition(GridPoint2 position);
}
