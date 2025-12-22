package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovementReservations {
    public Snapshot snapshot(List<TankModel> tanks) {
        Set<GridPoint2> occupied = new HashSet<>();
        Set<GridPoint2> reserved = new HashSet<>();
        for (TankModel t : tanks) {
            occupied.add(new GridPoint2(t.getCoordinates()));
            if (!t.isReady()) {
                reserved.add(new GridPoint2(t.getCoordinates()));
                reserved.add(new GridPoint2(t.getDestination()));
            }
        }
        return new Snapshot(occupied, reserved);
    }

    public static class Snapshot {
        private final Set<GridPoint2> occupied;
        private final Set<GridPoint2> reserved;

        public Snapshot(Set<GridPoint2> occupied, Set<GridPoint2> reserved) {
            this.occupied = occupied;
            this.reserved = reserved;
        }

        public Set<GridPoint2> occupied() {
            return occupied;
        }

        public Set<GridPoint2> reserved() {
            return reserved;
        }
    }
}
