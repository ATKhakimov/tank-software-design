// Обработчик ввода через абстракции GameObject/Obstacle для соблюдения DIP
package ru.mipt.bit.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.ArrayList;
import java.util.List;

public class InputHandler {
    private final List<InputCommand> commands = new ArrayList<>();

    public InputHandler(TankModel tank, Obstacle obstacle) {
        commands.add(new MoveCommand(tank, obstacle, Direction.UP, Input.Keys.UP, Input.Keys.W));
        commands.add(new MoveCommand(tank, obstacle, Direction.LEFT, Input.Keys.LEFT, Input.Keys.A));
        commands.add(new MoveCommand(tank, obstacle, Direction.DOWN, Input.Keys.DOWN, Input.Keys.S));
        commands.add(new MoveCommand(tank, obstacle, Direction.RIGHT, Input.Keys.RIGHT, Input.Keys.D));
        commands.add(new ShootCommand(tank, Input.Keys.SPACE));
    }

    public void handle() {
        for (InputCommand command : commands) {
            command.execute();
        }
    }

    private interface InputCommand {
        void execute();
    }

    private static class MoveCommand implements InputCommand {
        private final TankModel tank;
        private final Obstacle obstacle;
        private final Direction direction;
        private final int primaryKey;
        private final int secondaryKey;

        MoveCommand(TankModel tank, Obstacle obstacle, Direction direction, int primaryKey, int secondaryKey) {
            this.tank = tank;
            this.obstacle = obstacle;
            this.direction = direction;
            this.primaryKey = primaryKey;
            this.secondaryKey = secondaryKey;
        }

        @Override
        public void execute() {
            if (!Gdx.input.isKeyPressed(primaryKey) && !Gdx.input.isKeyPressed(secondaryKey)) {
                return;
            }
            if (tank.isReady()) {
                if (obstacle.occupies(direction.apply(tank.getCoordinates()))) {
                    tank.face(direction);
                } else {
                    tank.start(direction);
                }
            } else {
                tank.face(direction);
            }
        }
    }

    private static class ShootCommand implements InputCommand {
        private final TankModel tank;
        private final int key;
        private boolean wasPressed = false;

        ShootCommand(TankModel tank, int key) {
            this.tank = tank;
            this.key = key;
        }

        @Override
        public void execute() {
            boolean isPressed = Gdx.input.isKeyPressed(key);
            if (isPressed && !wasPressed) {
                GridPoint2 pos = tank.getCoordinates();
                System.out.println("Tank shoots from (" + pos.x + ", " + pos.y + ") at rotation: " + tank.getRotation());
            }
            wasPressed = isPressed;
        }
    }
}
