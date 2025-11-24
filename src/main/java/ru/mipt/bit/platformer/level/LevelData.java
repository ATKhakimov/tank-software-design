package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.List;

public class LevelData {
    private GridPoint2 playerStart;
    private final List<GridPoint2> treePositions = new ArrayList<>();

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
