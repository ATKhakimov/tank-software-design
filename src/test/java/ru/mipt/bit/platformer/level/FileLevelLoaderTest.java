package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileLevelLoaderTest {
    @Test
    void parsesLevelTxt() {
        FileLevelLoader loader = new FileLevelLoader("level.txt");
        LevelData data = loader.load();
        assertNotNull(data.getPlayerStart());
        assertEquals(new GridPoint2(5, 1), data.getPlayerStart());
        assertEquals(15, data.getTreePositions().size());
        assertTrue(data.getTreePositions().contains(new GridPoint2(3, 5)));
        assertTrue(data.getTreePositions().contains(new GridPoint2(6, 5)));
        assertTrue(data.getTreePositions().contains(new GridPoint2(9, 3)));
    }
}
