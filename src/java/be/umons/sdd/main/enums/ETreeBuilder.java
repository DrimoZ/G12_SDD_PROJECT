package be.umons.sdd.main.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum ETreeBuilder {
    DETERMINISTIC("Deterministic"),
    RANDOM("Random"),
    TELLER("Teller");


    private final String displayName;

    /**
     * Constructeur de l'énumération.
     *
     * @param displayName Le nom affichable de la scène.
     * @param path        Le chemin vers le fichier de configuration de la scène.
     */
    ETreeBuilder(String displayName) {
        this.displayName = displayName;
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
     * Recherche une scène à partir de son nom affichable (non sensible à la casse).
     *
     * @param name Le nom affichable à rechercher.
     * @return Un Optional contenant la scène si trouvée, sinon un Optional vide.
     */
    public static Optional<ETreeBuilder> fromDisplayName(String name) {
        for (ETreeBuilder builder : values()) {
            if (builder.getDisplayName().equalsIgnoreCase(name)) {
                return Optional.of(builder);
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
        for (ETreeBuilder builder : values()) {
            names.add(builder.getDisplayName());
        }
        return names;
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
