// Обработчик ввода через абстракции GameObject/Obstacle для соблюдения DIP
package ru.mipt.bit.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.ArrayList;
import java.util.List;

public class InputHandler {
    private final List<InputCommand> commands = new ArrayList<>();

    public InputHandler(TankModel tank, Obstacle obstacle) {
        List<Obstacle> list = new ArrayList<>();
        list.add(obstacle);
        MovementRules rules = new MovementRules(Integer.MAX_VALUE, Integer.MAX_VALUE, list);
        initCommands(tank, rules);
    }

    public InputHandler(TankModel tank, List<Obstacle> obstacles) {
        MovementRules rules = new MovementRules(Integer.MAX_VALUE, Integer.MAX_VALUE, obstacles);
        initCommands(tank, rules);
    }

    public InputHandler(TankModel tank, MovementRules rules) {
        initCommands(tank, rules);
    }

    public InputHandler(TankModel tank, MovementRules rules, HealthBarsController healthBarsController) {
        initCommands(tank, rules);
        commands.add(new ToggleHealthBarsCommand(healthBarsController, Input.Keys.L));
    }

    private void initCommands(TankModel tank, MovementRules rules) {
        commands.add(new MoveCommand(tank, rules, Direction.UP, Input.Keys.UP, Input.Keys.W));
        commands.add(new MoveCommand(tank, rules, Direction.LEFT, Input.Keys.LEFT, Input.Keys.A));
        commands.add(new MoveCommand(tank, rules, Direction.DOWN, Input.Keys.DOWN, Input.Keys.S));
        commands.add(new MoveCommand(tank, rules, Direction.RIGHT, Input.Keys.RIGHT, Input.Keys.D));
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
        private final MovementRules rules;
        private final Direction direction;
        private final int primaryKey;
        private final int secondaryKey;

        MoveCommand(TankModel tank, MovementRules rules, Direction direction, int primaryKey, int secondaryKey) {
            this.tank = tank;
            this.rules = rules;
            this.direction = direction;
            this.primaryKey = primaryKey;
            this.secondaryKey = secondaryKey;
        }

        @Override
        public void execute() {
            if (!Gdx.input.isKeyPressed(primaryKey) && !Gdx.input.isKeyPressed(secondaryKey)) {
                return;
            }
        
            rules.attemptStartOrFace(tank, direction);
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

    private static class ToggleHealthBarsCommand implements InputCommand {
        private final HealthBarsController controller;
        private final int key;
        private boolean wasPressed = false;

        ToggleHealthBarsCommand(HealthBarsController controller, int key) {
            this.controller = controller;
            this.key = key;
        }

        @Override
        public void execute() {
            boolean isPressed = Gdx.input.isKeyPressed(key);
            if (isPressed && !wasPressed) {
                controller.toggle();
            }
            wasPressed = isPressed;
        }
    }
}
