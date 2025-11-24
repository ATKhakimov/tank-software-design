package ru.mipt.bit.platformer.config;

import ru.mipt.bit.platformer.model.WorldModel;

public class WorldModelFactory {
    private final float bulletSpeed;
    private final float bulletDamage;

    public WorldModelFactory(float bulletSpeed, float bulletDamage) {
        this.bulletSpeed = bulletSpeed;
        this.bulletDamage = bulletDamage;
    }

    public WorldModel create(int width, int height) {
        return new WorldModel(width, height, bulletSpeed, bulletDamage);
    }
}
