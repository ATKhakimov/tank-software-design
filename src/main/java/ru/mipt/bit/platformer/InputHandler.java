// Обработчик ввода: маппинг клавиш -> команды, без прямой зависимости от Gdx
package ru.mipt.bit.platformer;

import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.Shooter;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.ArrayList;
import java.util.List;

public class InputHandler {
    private final InputSource input;
    private final List<InputCommand> commands;

    public InputHandler(InputSource input, List<InputCommand> commands) {
        this.input = input;
        this.commands = new ArrayList<>(commands);
    }

    public InputHandler(InputSource input, TankModel tank, MovementRules rules, HealthBarsController healthBarsController) {
        this(input, buildDefaultCommands(tank, rules, healthBarsController));
    }

    // Backward-compatible convenience ctor used by GameSession
    public InputHandler(TankModel tank, MovementRules rules, HealthBarsController healthBarsController) {
        this(new GdxInputSource(), tank, rules, healthBarsController);
    }

    public void addCommand(InputCommand command) {
        this.commands.add(command);
    }

    public void setShooter(Shooter shooter) {
        for (InputCommand command : commands) {
            if (command instanceof ShootCommand) {
                ((ShootCommand) command).setShooter(shooter);
            }
        }
    }

    public CommandQueue collectCommands() {
        CommandQueue queue = new CommandQueue();
        for (InputCommand command : commands) {
            command.enqueueIfTriggered(input, queue);
        }
        return queue;
    }

    private static List<InputCommand> buildDefaultCommands(TankModel tank, MovementRules rules, HealthBarsController controller) {
        List<InputCommand> cmds = new ArrayList<>();
        cmds.add(new MoveCommand(tank, rules, Direction.UP, Input.Keys.UP, Input.Keys.W));
        cmds.add(new MoveCommand(tank, rules, Direction.LEFT, Input.Keys.LEFT, Input.Keys.A));
        cmds.add(new MoveCommand(tank, rules, Direction.DOWN, Input.Keys.DOWN, Input.Keys.S));
        cmds.add(new MoveCommand(tank, rules, Direction.RIGHT, Input.Keys.RIGHT, Input.Keys.D));
        cmds.add(new ShootCommand(tank, Input.Keys.SPACE));
        cmds.add(new ToggleHealthBarsCommand(controller, Input.Keys.L));
        return cmds;
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
        public void enqueueIfTriggered(InputSource input, CommandQueue queue) {
            if (!input.isKeyPressed(primaryKey) && !input.isKeyPressed(secondaryKey)) {
                return;
            }
            queue.enqueue(() -> rules.attemptStartOrFace(tank, direction));
        }
    }

    private static class ShootCommand implements InputCommand {
        private final TankModel tank;
        private final int key;
        private boolean wasPressed = false;
        private Shooter shooter;

        ShootCommand(TankModel tank, int key) {
            this.tank = tank;
            this.key = key;
        }

        public void setShooter(Shooter shooter) {
            this.shooter = shooter;
        }

        @Override
        public void enqueueIfTriggered(InputSource input, CommandQueue queue) {
            if (shooter == null) return;
            boolean isPressed = input.isKeyPressed(key);
            if (isPressed && !wasPressed) {
                queue.enqueue(() -> shooter.shoot(tank));
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
        public void enqueueIfTriggered(InputSource input, CommandQueue queue) {
            boolean isPressed = input.isKeyPressed(key);
            if (isPressed && !wasPressed) {
                queue.enqueue(controller::toggle);
            }
            wasPressed = isPressed;
        }
    }
}
