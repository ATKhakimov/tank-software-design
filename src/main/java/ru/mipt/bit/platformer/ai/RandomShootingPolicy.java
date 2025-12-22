package ru.mipt.bit.platformer.ai;

import ru.mipt.bit.platformer.model.TankModel;

import java.util.Random;

public class RandomShootingPolicy implements AIShootingPolicy {
    private final Random random;
    private final float shootProbability;

    public RandomShootingPolicy(Random random, float shootProbability) {
        this.random = random;
        this.shootProbability = shootProbability;
    }

    @Override
    public boolean shouldShoot(TankModel tank) {
        return random.nextFloat() < shootProbability;
    }
}
