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
        List<Obstacle> list = new ArrayList<>();
        list.add(obstacle);
        initCommands(tank, list);
    }

    public InputHandler(TankModel tank, List<Obstacle> obstacles) {
        initCommands(tank, obstacles);
    }

    private void initCommands(TankModel tank, List<Obstacle> obstacles) {
        commands.add(new MoveCommand(tank, obstacles, Direction.UP, Input.Keys.UP, Input.Keys.W));
        commands.add(new MoveCommand(tank, obstacles, Direction.LEFT, Input.Keys.LEFT, Input.Keys.A));
        commands.add(new MoveCommand(tank, obstacles, Direction.DOWN, Input.Keys.DOWN, Input.Keys.S));
        commands.add(new MoveCommand(tank, obstacles, Direction.RIGHT, Input.Keys.RIGHT, Input.Keys.D));
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
        private final List<Obstacle> obstacles;
        private final Direction direction;
        private final int primaryKey;
        private final int secondaryKey;

        MoveCommand(TankModel tank, List<Obstacle> obstacles, Direction direction, int primaryKey, int secondaryKey) {
            this.tank = tank;
            this.obstacles = obstacles;
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
                boolean blocked = false;
                GridPoint2 target = direction.apply(tank.getCoordinates());
                for (Obstacle o : obstacles) {
                    if (o.occupies(target)) {
                        blocked = true;
                        break;
                    }
                }
                if (blocked) {
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
