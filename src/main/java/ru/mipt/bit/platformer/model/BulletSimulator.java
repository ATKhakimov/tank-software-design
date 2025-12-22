package ru.mipt.bit.platformer.model;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class BulletSimulator {
    public void tick(float delta, List<BulletModel> bullets, CollisionContext context, Consumer<BulletModel> removalHandler) {
        Iterator<BulletModel> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            BulletModel bullet = iterator.next();
            bullet.tick(delta, context);
            if (bullet.isRemoved()) {
                iterator.remove();
                removalHandler.accept(bullet);
            }
        }
    }
}
