// Правила движения: границы, препятствия и занятость клеток
package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Direction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovementRules {
    private final int width;
    private final int height;
    private final List<Obstacle> obstacles;
    private Set<GridPoint2> occupiedCurrent = new HashSet<>();
    private Set<GridPoint2> reserved = new HashSet<>();

    public MovementRules(int width, int height, List<Obstacle> obstacles) {
        this.width = width;
        this.height = height;
        this.obstacles = obstacles;
    }

    public void setOccupied(Set<GridPoint2> occupiedCurrent, Set<GridPoint2> reserved) {
        this.occupiedCurrent = occupiedCurrent;
        this.reserved = reserved;
    }

    public boolean canStart(TankModel tank, Direction direction) {
        if (!tank.isReady()) return false;
        GridPoint2 from = tank.getCoordinates();
        GridPoint2 target = direction.apply(from);
        if (reserved.contains(from)) return false;
        return isInside(target) && !isObstacle(target) && !occupiedCurrent.contains(target) && !reserved.contains(target);
    }

    public boolean attemptStartOrFace(TankModel tank, Direction direction) {
        if (!tank.isReady()) {
            tank.face(direction);
            return false;
        }
        GridPoint2 from = tank.getCoordinates();
        GridPoint2 to = direction.apply(from);
        if (!reserved.contains(from) && isInside(to) && !isObstacle(to) && !occupiedCurrent.contains(to) && !reserved.contains(to)) {
            tank.start(direction);
            reserved.add(new GridPoint2(from));
            reserved.add(new GridPoint2(to));
            return true;
        }
        tank.face(direction);
        return false;
    }

    private boolean isInside(GridPoint2 p) {
        return p.x >= 0 && p.y >= 0 && p.x < width && p.y < height;
    }

    private boolean isObstacle(GridPoint2 p) {
        for (Obstacle o : obstacles) {
            if (o.occupies(p)) return true;
        }
        return false;
    }

}
