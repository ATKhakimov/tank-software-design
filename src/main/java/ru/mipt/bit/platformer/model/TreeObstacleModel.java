// Модель препятствия дерева без графики
package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class TreeObstacleModel {
    private final GridPoint2 coordinates = new GridPoint2();

    public void setPosition(GridPoint2 position) {
        coordinates.set(position);
    }

    public boolean occupies(GridPoint2 point) {
        return coordinates.equals(point);
    }

    public GridPoint2 getCoordinates() {
        return coordinates;
    }
}
