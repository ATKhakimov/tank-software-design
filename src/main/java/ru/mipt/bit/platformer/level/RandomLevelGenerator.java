package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Random;

public class RandomLevelGenerator implements LevelLoader {
    private final int width;
    private final int height;
    private final float treeDensity;
    private final Random random;

    public RandomLevelGenerator(int width, int height, float treeDensity, long seed) {
        this.width = width;
        this.height = height;
        this.treeDensity = treeDensity;
        this.random = new Random(seed);
    }

    public RandomLevelGenerator(int width, int height, float treeDensity) {
        this(width, height, treeDensity, System.currentTimeMillis());
    }

    @Override
    public LevelData load() {
        LevelData data = new LevelData();
        GridPoint2 start = new GridPoint2(random.nextInt(Math.max(1, width)), random.nextInt(Math.max(1, height)));
        data.setPlayerStart(start);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == start.x && y == start.y) {
                    continue;
                }
                if (random.nextFloat() < treeDensity) {
                    data.getTreePositions().add(new GridPoint2(x, y));
                }
            }
        }
        return data;
    }
}
