package slotmachine;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/** Traduce nombres de colores a Color. */
final class Colors {
    private static final Map<String, Color> NAMED = new HashMap<>();
    static {
        NAMED.put("red", Color.RED);
        NAMED.put("green", new Color(0, 160, 0));
        NAMED.put("blue", Color.BLUE);
        NAMED.put("yellow", new Color(240, 200, 0));
        NAMED.put("orange", Color.ORANGE);
        NAMED.put("purple", new Color(128, 0, 160));
        NAMED.put("pink", Color.PINK);
        NAMED.put("cyan", Color.CYAN);
        NAMED.put("brown", new Color(139, 90, 43));
        NAMED.put("gray", Color.GRAY);
        NAMED.put("black", Color.BLACK);
        NAMED.put("gold", new Color(255, 215, 0));
    }

    private Colors() { }

    static Color of(String name) {
        Color c = NAMED.get(name);
        if (c != null) return c;
        try {
            if (name.startsWith("#")) return Color.decode(name);
        } catch (NumberFormatException e) {
        }
        return Color.getHSBColor((name.hashCode() & 0xffff) / 65535f, 0.7f, 0.9f);
    }
}
