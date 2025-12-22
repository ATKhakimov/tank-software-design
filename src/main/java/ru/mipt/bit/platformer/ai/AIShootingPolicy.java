package ru.mipt.bit.platformer.ai;

import ru.mipt.bit.platformer.model.TankModel;

public interface AIShootingPolicy {
    boolean shouldShoot(TankModel tank);
}
