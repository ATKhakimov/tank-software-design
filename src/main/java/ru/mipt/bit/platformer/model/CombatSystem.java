package ru.mipt.bit.platformer.model;

public interface CombatSystem {
    void shoot(TankModel tank, CombatContext context);

    void applyDamage(TankModel target, float damage, CombatContext context);
}
