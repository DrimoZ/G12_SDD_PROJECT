package be.umons.sdd.test;

import be.umons.sdd.builders.PaintersViewBuilder;
import be.umons.sdd.enums.EScenes;
import be.umons.sdd.enums.ETreeBuilder;
import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Point2D;
import be.umons.sdd.models.Scene2D;
import be.umons.sdd.utils.SceneSerializer;
import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TestBSPTree {

    public static void main(String[] args) {

        System.out.println("====================================== BSP TREE COMPARISON ======================================");
        System.out.println("This small program allows you to compare different methods of building a BSP tree.");
        System.out.println("You can either choose a predefined scene or load a custom scene from a file.");
        System.out.println("================================================================================================");
        System.out.println("");

        Scene2D scene = askForSceneInput();
        if (scene != null) {
            printInfo(scene);

            List<BSPMetrics> results = testAllBuilders(scene);
            printResults(results);
        }
        else {
            System.out.println("An error occurred while loading the scene.");
        }
    }
    

    /**
     * Prompts the user to select a scene and returns the corresponding Scene2D object.
     *
     * <p> This method does the following:
     * 1. Displays a list of available predefined scenes to the user, excluding custom scenes.
     * 2. Continuously prompts the user to enter the name of the selected scene.
     * 3. For custom scenes, prompts the user for a file path and reads the scene from the specified file.
     * 4. Attempts to read the scene using SceneSerializer based on user input.
     * 5. Handles invalid input and errors during scene reading by displaying appropriate messages.
     * 6. Returns the successfully loaded Scene2D object.
     *
     * @return the Scene2D object corresponding to the user's selected scene, or null if an error occurs.
     */
    private static Scene2D askForSceneInput() {
        Scene2D scene = null;

        try (Scanner input = new Scanner(System.in)) {
            // Print All Valid scenes
            System.out.println("Available scenes:");
            for (EScenes sceneName : EScenes.values()) {
                if (EScenes.isCustomScene(sceneName.getDisplayName())) continue;
                System.out.println("  " + sceneName.getDisplayName() + " (" + sceneName.getPath() + ")");
            }

            System.out.println("\n");

            while (scene == null) {
                try {
                    System.out.println("Enter the name of the scene: ");
                    
                    // Get scene from EScenes (exlude CUSTOM)
                    EScenes sceneName = EScenes.fromDisplayName(input.nextLine()).get();

                    if (EScenes.isCustomScene(sceneName.getDisplayName())) {
                        System.out.println("Enter the path to the custom scene file (FROM ROOT): ");
                        String path = input.nextLine();
                        File pathToFile = new File(path);
                        scene = SceneSerializer.readScene("Custom Scene", pathToFile);
                    } else {
                        scene = SceneSerializer.readScene(sceneName.getDisplayName(), sceneName.getPath());
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please try again.");
                }
            }

            input.close();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

        return scene;
    }

    /**
     * Prints the information of a given scene and all available builders.
     *
     * @param scene the scene to print information about
     */
    private static void printInfo(Scene2D scene) {
        // Print custom scene info

        System.out.println("");
        System.out.println("========================================== SCENE INFO ==========================================");
        System.out.println("Scene name: " + scene.getName());
        System.out.println("Scene total size: " + scene.getExtentX() * 2 + "x" + scene.getExtentY() * 2);
        System.out.println("Scene segments: " + scene.getSegments().size());
        System.out.println("================================================================================================");
        System.out.println("");

        // Print All builders info
        System.out.println("======================================= BSP TREE BUILDERS ======================================");
        System.out.println("Available builders:");
        for (ETreeBuilder builder : ETreeBuilder.getAllBuilders()) {
            System.out.println("  " + builder.getDisplayName() + " (" + builder.getBuilder() + ")");
        }
        System.out.println("================================================================================================");
        System.out.println("");
    }

    /**
     * Tests all available BSP tree builders on a given scene.
     * For each builder, it builds a BSP tree, measures the time it took to build the tree,
     * paints the tree using the painter's algorithm, and measures the time it took to paint it.
     * The results are stored in a list of BSPMetrics objects which are returned.
     * 
     * @param scene the scene to test the builders on
     * @return a list of BSPMetrics objects, one for each builder
     */
    private static List<BSPMetrics> testAllBuilders(Scene2D scene) {
        System.out.println("");
        System.out.println("Creating trees for all (" + ETreeBuilder.getAllBuilders().size() + ") builders and applying painters algorithm...");
        System.out.println("The process may take a few minutes...");
        System.out.println("");

        return ETreeBuilder.getAllBuilders()
        .parallelStream()
        .map(builder -> {
            // exactly the same code as before:
            long buildStart = System.currentTimeMillis();
            BSPNode root = builder.getBuilder().buildTree(scene.getSegments(), null);
            long buildEnd   = System.currentTimeMillis();

            long paintStart = System.currentTimeMillis();
            PaintersViewBuilder.paintersAlgorithm(root, new Point2D(0, 0));
            long paintEnd   = System.currentTimeMillis();

            return new BSPMetrics(
                builder.getDisplayName(),
                root.size(),
                root.height(),
                buildEnd - buildStart,
                paintEnd - paintStart
            );
        })
        .collect(Collectors.toList());
    }

    /**
     * Prints a table of the results of testing all tree builders on a given scene.
     * The table shows the name of the builder, the size of the tree, the height of
     * the tree, the time it took to build the tree, and the time it took to paint
     * the tree using the painters algorithm.
     *
     * @param results the list of results to display
     */
    private static void printResults(List<BSPMetrics> results) {
        System.out.println("========================================= BSP TREE RESULTS ======================================");
        System.out.println("");
        System.out.println("Painter Algorithm has been applied to all trees using an origin of (0, 0).");
        System.out.println("");

        String format = "%-15s | %-10s | %-12s | %-12s | %-13s%n";
        System.out.printf(format, "Heuristic", "Tree Size", "Tree Height", "Build Time", "Painter Time");
        System.out.println("----------------+------------+--------------+--------------+---------------");

        for (BSPMetrics m : results) {
            System.out.printf(format, m.name, m.size, m.height, m.buildTimeMs + " ms", m.painterTimeMs + " ms");
        }

        System.out.println("");
        System.out.println("================================================================================================");
    }


    static class BSPMetrics {
        String name;
        int size;
        int height;
        long buildTimeMs;
        long painterTimeMs;
    
        public BSPMetrics(String name, int size, int height, long buildTimeMs, long painterTimeMs) {
            this.name = name;
            this.size = size;
            this.height = height;
            this.buildTimeMs = buildTimeMs;
            this.painterTimeMs = painterTimeMs;
        }
    }
}
