package be.umons.sdd.builders;

import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Line2D;
import be.umons.sdd.models.StraightSegment2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A deterministic BSP tree builder.
 */
public class DeterministicBSPTreeBuilder extends AbstractBSPTreeBuilder {

    /**
     * Builds a deterministic BSP tree from a set of scene objects.
     * 
     * @param objects the list of scene objects.
     * @param parentLine the parent's splitting line (null for root).
     * @return the BSP tree node representing the region.
     */
    @Override
    public BSPNode buildTree(List<StraightSegment2D> objects, Line2D parentLine) {

        // Base case: if 0 or 1 object, create a leaf.
        if (objects == null || objects.isEmpty() || objects.size() <= 1) {
            return new BSPNode(new ArrayList<>(objects));
        }
        
        // Evaluate candidate splitting lines.
        List<Candidate> candidateList = evaluateCandidates(objects, parentLine);
        
        Line2D bestLine;
        if (candidateList.isEmpty()) {
            bestLine = objects.get(0).getSupportLine();
        } else {
            bestLine = selectBestCandidate(candidateList).supportLine;
        }
        
        // Create current node.
        BSPNode node = new BSPNode(bestLine);
        
        // Partition objects.
        PartitionResult partition = partitionObjects(objects, bestLine);
        node.getCoplanarObjects().addAll(partition.coplanarList);
        
        // Recursively build subtrees.
        node.setLeft(buildTree(partition.negativeList, bestLine));
        node.setRight(buildTree(partition.positiveList, bestLine));
        
        return node;
    }
}
