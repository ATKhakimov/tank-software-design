// Обработчик ввода с поддержкой расширения новых команд
package ru.mipt.bit.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.model.TankModel;
import ru.mipt.bit.platformer.model.TreeObstacleModel;

import java.util.ArrayList;
import java.util.List;

public class InputHandler {
    private final List<InputCommand> commands = new ArrayList<>();

    public InputHandler(TankModel tank, TreeObstacleModel tree) {
        commands.add(new MoveCommand(tank, tree, Direction.UP, Input.Keys.UP, Input.Keys.W));
        commands.add(new MoveCommand(tank, tree, Direction.LEFT, Input.Keys.LEFT, Input.Keys.A));
        commands.add(new MoveCommand(tank, tree, Direction.DOWN, Input.Keys.DOWN, Input.Keys.S));
        commands.add(new MoveCommand(tank, tree, Direction.RIGHT, Input.Keys.RIGHT, Input.Keys.D));
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
        private final TreeObstacleModel tree;
        private final Direction direction;
        private final int primaryKey;
        private final int secondaryKey;

        MoveCommand(TankModel tank, TreeObstacleModel tree, Direction direction, int primaryKey, int secondaryKey) {
            this.tank = tank;
            this.tree = tree;
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
                if (tree.occupies(direction.apply(tank.getCoordinates()))) {
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
                System.out.println("Tank shoots at rotation: " + tank.getRotation());
            }
            wasPressed = isPressed;
        }
    }
}
