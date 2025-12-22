package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.List;

public class LevelData {
    private int width = 1;
    private int height = 1;
    private GridPoint2 playerStart;
    private final List<GridPoint2> treePositions = new ArrayList<>();

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    public GridPoint2 getPlayerStart() {
        return playerStart;
    }

    public void setPlayerStart(GridPoint2 playerStart) {
        this.playerStart = playerStart;
    }

    public List<GridPoint2> getTreePositions() {
        return treePositions;
    }
}
