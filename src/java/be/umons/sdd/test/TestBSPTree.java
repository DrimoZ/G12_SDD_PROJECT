package be.umons.sdd.test;

import be.umons.sdd.builders.TellerBSPTreeBuilder;
import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Scene2D;
import be.umons.sdd.utils.SceneSerializer;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * TestBSPTree demonstrates the deterministic BSP tree construction
 * and prints the tree layout to the console.
 */
public class TestBSPTree {

    public static void main(String[] args) {
        try {
            Scene2D scene = SceneSerializer.readScene("first/octangle.txt");
            System.out.println("Scene loaded: " + scene.toString());

            // Build the BSP tree from the scene objects.
            // BSPNode root = new DeterministicBSPTreeBuilder().buildTree(scene.getSegments(), null);
            BSPNode root = new TellerBSPTreeBuilder(0.001).buildTree(scene.getSegments(), null);
            System.out.println( "BSP Tree built: " + root.toString());
            // Print the BSP tree layout to a file.
            try (PrintWriter pw = new PrintWriter("bspTree.txt")) {
                printBSPTreeToFile(root, "", true, pw);
            } catch (IOException e) {
                System.err.println("Error writing BSP tree file: " + e.getMessage());
                return;
            }
        } catch (IOException e) {
            System.err.println("Error reading scene file: " + e.getMessage());
            return;
        }

        System.out.println("Visualization files 'scene.txt' and 'bspTree.txt' have been generated.");    
    }
    
    /**
     * Recursively prints the BSP tree layout to the provided PrintWriter using ASCII branch markers.
     * For internal nodes, only the splitting line and the count of coplanar objects are shown.
     * For leaf nodes, only the count of stored objects is displayed.
     *
     * @param node   the BSP node to print
     * @param prefix the prefix string used for indentation
     * @param isTail true if this node is the last child in its level
     * @param pw     the PrintWriter to write to
     */
    private static void printBSPTreeToFile(BSPNode node, String prefix, boolean isTail, PrintWriter pw) {
        if (node == null) return;
        
        String connector = isTail ? "+-- " : "|-- ";
        
        if (node.isLeaf()) {
            // Display leaf node with count of coplanar objects.
            pw.println(prefix + connector + "Leaf (" + node.getCoplanarObjects().size() + " objects)");
        } else {
            // Display internal node with its splitting line.
            pw.println(prefix + connector + "Node: " + node.getPartition());
            // Display count of coplanar objects at this node.
            // pw.println(prefix + (isTail ? "    " : "|   ") + "[Coplanar: " + node.getCoplanarObjects().size() + " objects]");
            
            // Prepare prefix for children.
            String childPrefix = prefix + (isTail ? "    " : "|   ");
            // Print left (negative half-plane) child.
            printBSPTreeToFile(node.getLeft(), childPrefix, false, pw);
            // Print right (positive half-plane) child, marking it as the tail.
            printBSPTreeToFile(node.getRight(), childPrefix, true, pw);
        }
    }
}
