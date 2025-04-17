package be.umons.sdd.enums;

import be.umons.sdd.builders.BSPTreeBuilder;
import be.umons.sdd.builders.DeterministicBSPTreeBuilder;
import be.umons.sdd.builders.RandomBSPTreeBuilder;
import be.umons.sdd.builders.TellerBSPTreeBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum ETreeBuilder {
    SELECT_BUILDER("Select a Builder", null),
    DETERMINISTIC("Deterministic", new DeterministicBSPTreeBuilder()),
    RANDOM("Random", new RandomBSPTreeBuilder()),
    TELLER("Teller", new TellerBSPTreeBuilder(0.5));


    private final String displayName;
    private final BSPTreeBuilder builder;

    /**
     * Constructeur de l'énumération.
     *
     * @param displayName Le nom affichable de la scène.
     * @param path        Le chemin vers le fichier de configuration de la scène.
     */
    ETreeBuilder(String displayName, BSPTreeBuilder builder) {
        this.displayName = displayName;
        this.builder = builder;
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
     * Returns the BSP tree builder associated with this enum constant.
     *
     * @return The BSPTreeBuilder instance.
     */
    public BSPTreeBuilder getBuilder() {
        return builder;
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
     * Returns a list of all the BSP tree builders available, excluding the null one.
     * 
     * @return A list of BSPTreeBuilder instances.
     */
    public static List<ETreeBuilder> getAllBuilders() {
        List<ETreeBuilder> builders = new ArrayList<>();
        for (ETreeBuilder builder : values()) {
            if (builder.builder == null) continue;
            
            builders.add(builder);
        }
        return builders;
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
