// Модель танка без графики
package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Direction;

public class TankModel implements GameObject, HealthProvider {
    private final GridPoint2 coordinates = new GridPoint2();
    private final GridPoint2 destination = new GridPoint2();
    private final float speed;
    private float progress = 1f;
    private float rotation = 0f;
    private float health = 1f;

    public TankModel(float speed) {
        this.speed = speed;
    }

    public void setPosition(GridPoint2 position) {
        coordinates.set(position);
        destination.set(position);
        progress = 1f;
    }

    public boolean isReady() {
        return progress >= 1f;
    }

    public void start(Direction direction) {
        if (isReady()) {
            destination.set(direction.apply(coordinates));
            progress = 0f;
        }
        rotation = direction.rotation();
    }

    public void face(Direction direction) {
        rotation = direction.rotation();
    }

    public void updateProgress(float deltaTime) {
        if (progress < 1f) {
            progress = Math.min(1f, progress + deltaTime / speed);
            if (progress >= 1f) {
                coordinates.set(destination);
            }
        }
    }

    public GridPoint2 getCoordinates() {
        return coordinates;
    }

    public GridPoint2 getDestination() {
        return destination;
    }

    public float getProgress() {
        return progress;
    }

    public float getRotation() {
        return rotation;
    }

    public float getSpeed() {
        return speed;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = Math.max(0f, Math.min(1f, health));
    }
}
