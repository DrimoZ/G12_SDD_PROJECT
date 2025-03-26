package be.umons.sdd.main.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum EScenes {
    ELLIPSESLARGE("Ellispes Large", "/ellipses/ellipsesLarge.txt"),
    ELLIPSESMEDIUM("Ellispes Medium", "/ellipses/ellipsesMedium.txt"),
    ELLIPSESSMALL("Ellispes Small", "/ellipses/ellipsesSmall.txt"),

    OCTANGLE("Octangle", "/first/octangle.txt"),
    OCTOGONE("Octogone", "/first/octogone.txt"),

    RANDOMHUGE("Random Huge", "/random/randomHuge.txt"),
    RANDOMLARGE("Random Large", "/random/randomLarge.txt"),
    RANDOMMEDIUM("Random Medium", "/random/randomMedium.txt"),
    RANDOMSMALL("Random Small", "/random/randomSmall.txt"),

    RECTANGLEHUGE("Rectangle Huge", "/rectangles/rectanglesHuge.txt"),
    RECTANGLELARGE("Rectangle Large", "/rectangles/rectanglesLarge.txt"),
    RECTANGLEMEDIUM("Rectangle Medium", "/rectangles/rectanglesMedium.txt"),
    RECTANGLESMALL("Rectangle Small", "/rectangles/rectanglesSmall.txt"),

    CUSTOM("Custom", "/custom");

    private final String displayName;
    private final String path;

    /**
     * Constructeur de l'énumération.
     *
     * @param displayName Le nom affichable de la scène.
     * @param path        Le chemin vers le fichier de configuration de la scène.
     */
    EScenes(String displayName, String path) {
        this.displayName = displayName;
        this.path = path;
    }

    /**
     * Retourne le nom affichable de la scène.
     *
     * @return Le nom affichable.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Retourne le chemin vers le fichier de configuration de la scène.
     *
     * @return Le chemin du fichier.
     */
    public String getPath() {
        return path;
    }

    /**
     * Recherche une scène à partir de son nom affichable (non sensible à la casse).
     *
     * @param name Le nom affichable à rechercher.
     * @return Un Optional contenant la scène si trouvée, sinon un Optional vide.
     */
    public static Optional<EScenes> fromDisplayName(String name) {
        for (EScenes scene : values()) {
            if (scene.getDisplayName().equalsIgnoreCase(name)) {
                return Optional.of(scene);
            }
        }
        return Optional.empty();
    }

    /**
     * Recherche une scène à partir de son chemin.
     *
     * @param path Le chemin à rechercher.
     * @return Un Optional contenant la scène si trouvée, sinon un Optional vide.
     */
    public static Optional<EScenes> fromPath(String path) {
        for (EScenes scene : values()) {
            if (scene.getPath().equals(path)) {
                return Optional.of(scene);
            }
        }
        return Optional.empty();
    }

    /**
     * Retourne la liste des noms affichables de toutes les scènes.
     *
     * @return Une liste de chaînes de caractères représentant les noms affichables.
     */
    public static List<String> getAllDisplayNames() {
        List<String> names = new ArrayList<>();
        for (EScenes scene : values()) {
            names.add(scene.getDisplayName());
        }
        return names;
    }

    public static boolean isCustomScene(String sceneName) {
        return CUSTOM.getDisplayName().equalsIgnoreCase(sceneName);
    }

    /**
     * Retourne le nom affichable de la scène.
     *
     * @return Le nom affichable.
     */
    @Override
    public String toString() {
        return displayName;
    }
}
