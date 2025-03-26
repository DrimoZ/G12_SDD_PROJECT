package be.umons.sdd.builders;

import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Line2D;
import be.umons.sdd.models.StraightSegment2D;
import java.util.Collections;
import java.util.List;

/**
 * A BSP tree builder that uses a random permutation of objects before constructing a deterministic BSP tree.
 */
public class RandomBSPTreeBuilder extends AbstractBSPTreeBuilder {

    /**
     * Builds a BSP tree by first randomly shuffling the objects,
     * then delegating to the deterministic builder.
     * 
     * @param objects the list of scene objects.
     * @param parentLine the parent's splitting line (null for root).
     * @return the BSP tree node representing the region.
     */
    @Override
    public BSPNode buildTree(List<StraightSegment2D> objects, Line2D parentLine) {
        // Create a copy to avoid modifying the original list.
        List<StraightSegment2D> shuffled = new java.util.ArrayList<>(objects);
        Collections.shuffle(shuffled);
        return new DeterministicBSPTreeBuilder().buildTree(shuffled, parentLine);
    }
}
