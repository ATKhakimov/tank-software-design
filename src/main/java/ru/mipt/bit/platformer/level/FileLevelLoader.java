package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileLevelLoader implements LevelLoader {
    private final String resourcePath;

    public FileLevelLoader(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public LevelData load() {
        List<String> lines = readAllLines(resourcePath);
        LevelData data = new LevelData();
        if (lines.isEmpty()) {
            data.setPlayerStart(new GridPoint2(0, 0));
            return data;
        }
        int height = lines.size();
        int width = 0;
        for (String l : lines) {
            width = Math.max(width, l.length());
        }
        for (int row = 0; row < height; row++) {
            String line = lines.get(row);
            int y = height - 1 - row;
            for (int x = 0; x < width; x++) {
                char c = x < line.length() ? line.charAt(x) : '_';
                if (c == 'T') {
                    data.getTreePositions().add(new GridPoint2(x, y));
                } else if (c == 'X') {
                    data.setPlayerStart(new GridPoint2(x, y));
                }
            }
        }
        if (data.getPlayerStart() == null) {
            GridPoint2 fallback = new GridPoint2(0, 0);
            data.setPlayerStart(fallback);
        }
        return data;
    }

    private static List<String> readAllLines(String resourcePath) {
        List<String> lines = new ArrayList<>();
        try (InputStream in = FileLevelLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                return lines;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String s;
                while ((s = br.readLine()) != null) {
                    lines.add(s.replace("\r", ""));
                }
            }
        } catch (IOException ignored) {
        }
        return lines;
    }
}
