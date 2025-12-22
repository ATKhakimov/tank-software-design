package ru.mipt.bit.platformer.ai;

import ru.mipt.bit.platformer.Direction;
import ru.mipt.bit.platformer.model.MovementRules;
import ru.mipt.bit.platformer.model.TankModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomStrategy implements BotStrategy {
    private final Random rnd = new Random();

    @Override
    public List<Direction> proposeDirections(TankModel tank, MovementRules rules) {
        List<Direction> dirs = new ArrayList<>();
        Collections.addAll(dirs, Direction.values());
        Collections.shuffle(dirs, rnd);
        return dirs;
    }
}
