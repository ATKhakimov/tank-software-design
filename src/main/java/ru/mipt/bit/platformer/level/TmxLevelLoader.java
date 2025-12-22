package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TmxLevelLoader implements LevelLoader {
    private final String resourcePath;
    private final LevelLoader fallback;

    public TmxLevelLoader(String resourcePath, LevelLoader fallback) {
        this.resourcePath = resourcePath;
        this.fallback = fallback;
    }

    @Override
    public LevelData load() {
        LevelData data = parseTmx();
        boolean empty = data.getPlayerStart() == null && data.getTreePositions().isEmpty();
        if (empty && fallback != null) {
            return fallback.load();
        }
        if (data.getPlayerStart() == null) {
            data.setPlayerStart(new GridPoint2(0, 0));
        }
        return data;
    }

    private LevelData parseTmx() {
        try (InputStream in = TmxLevelLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                return empty();
            }
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            doc.getDocumentElement().normalize();

            LevelData data = new LevelData();
            parseObjects(doc, data);
            parseAsciiProperty(doc, data);
            return data;
        } catch (Exception e) {
            return empty();
        }
    }

    private void parseObjects(Document doc, LevelData data) {
        NodeList groups = doc.getElementsByTagName("objectgroup");
        for (int i = 0; i < groups.getLength(); i++) {
            Node group = groups.item(i);
            NodeList objects = group.getChildNodes();
            for (int j = 0; j < objects.getLength(); j++) {
                Node obj = objects.item(j);
                if (!"object".equals(obj.getNodeName())) continue;
                String type = attr(obj, "type");
                String name = attr(obj, "name");
                float x = floatAttr(obj, "x", 0f);
                float y = floatAttr(obj, "y", 0f);
                int tileX = Math.round(x / 128f);
                int tileY = Math.round(y / 128f);
                if ("tree".equalsIgnoreCase(type) || "tree".equalsIgnoreCase(name)) {
                    data.getTreePositions().add(new GridPoint2(tileX, tileY));
                } else if ("player".equalsIgnoreCase(type) || "player".equalsIgnoreCase(name) || "start".equalsIgnoreCase(name)) {
                    data.setPlayerStart(new GridPoint2(tileX, tileY));
                }
            }
        }
    }

    private void parseAsciiProperty(Document doc, LevelData data) {
        NodeList properties = doc.getElementsByTagName("property");
        for (int i = 0; i < properties.getLength(); i++) {
            Node p = properties.item(i);
            if (!"ascii".equalsIgnoreCase(attr(p, "name"))) continue;
            String value = attr(p, "value");
            if (value == null || value.isEmpty()) {
                value = text(p);
            }
            if (value == null || value.isEmpty()) continue;
            List<String> lines = decodeLines(value);
            applyAscii(lines, data);
            break;
        }
    }

    private void applyAscii(List<String> lines, LevelData data) {
        int height = lines.size();
        for (int row = 0; row < height; row++) {
            String line = lines.get(row);
            int y = height - 1 - row;
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == 'T') {
                    data.getTreePositions().add(new GridPoint2(x, y));
                } else if (c == 'X') {
                    data.setPlayerStart(new GridPoint2(x, y));
                }
            }
        }
    }

    private List<String> decodeLines(String value) {
        String normalized = value.replace("\r", "");
        String[] split = normalized.split("\n");
        List<String> lines = new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()) {
                lines.add(s);
            }
        }
        return lines;
    }

    private LevelData empty() {
        return new LevelData();
    }

    private String attr(Node node, String name) {
        if (node.getAttributes() == null) return null;
        Node a = node.getAttributes().getNamedItem(name);
        return a != null ? a.getNodeValue() : null;
    }

    private float floatAttr(Node node, String name, float def) {
        String v = attr(node, name);
        if (v == null) return def;
        try {
            return Float.parseFloat(v);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private String text(Node node) {
        if (node == null) return null;
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node c = children.item(i);
            if (c.getNodeType() == Node.TEXT_NODE) {
                return c.getTextContent();
            }
        }
        return null;
    }
}
