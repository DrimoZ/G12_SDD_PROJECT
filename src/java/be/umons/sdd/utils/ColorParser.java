package be.umons.sdd.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorParser {
    // Mapping of French color names to java.awt.Color.
    private static final Map<String, Color> COLOR_MAP = new HashMap<>();
    static {
        COLOR_MAP.put("Bleu", Color.BLUE);
        COLOR_MAP.put("Rouge", Color.RED);
        COLOR_MAP.put("Orange", Color.ORANGE);
        COLOR_MAP.put("Jaune", Color.YELLOW);
        COLOR_MAP.put("Noir", Color.BLACK);
        COLOR_MAP.put("Violet", Color.MAGENTA);
        COLOR_MAP.put("Marron", new Color(139, 69, 19));
        COLOR_MAP.put("Vert", Color.GREEN);
        COLOR_MAP.put("Gris", Color.GRAY);
        COLOR_MAP.put("Rose", Color.PINK);
    }

    /**
     * Retrieves a Color object based on the provided color name.
     *
     * @param colorName the name of the color to retrieve
     * @return the Color object associated with the given color name, or null if the color name is not found
     */
    public static Color getColor(String colorName) {
        return COLOR_MAP.get(colorName);
    }

    /**
     * Returns the name of the given color.
     *
     * This method searches through a predefined map of color names and their corresponding
     * Color objects. If a match is found, the name of the color is returned. If no match
     * is found, null is returned.
     *
     * @param color the Color object for which the name is to be retrieved
     * @return the name of the color if found, otherwise null
     */
    public static String getColorName(Color color) {
        for (Map.Entry<String, Color> entry : COLOR_MAP.entrySet()) {
            if (entry.getValue().equals(color)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
