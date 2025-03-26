package be.umons.sdd.builders;

import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Line2D;
import be.umons.sdd.models.StraightSegment2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds a BSP tree using the Teller heuristic.
 */
public class TellerBSPTreeBuilder extends AbstractBSPTreeBuilder {

    private final double tau;  // Threshold for the ratio sigma.
    
    /**
     * Constructs a TellerBSPTreeBuilder with a given threshold tau.
     * Recommended default is 0.5.
     * 
     * @param tau the threshold value for sigma.
     */
    public TellerBSPTreeBuilder(double tau) {
        this.tau = tau;
    }

    /**
     * Builds the BSP tree using Teller's heuristic.
     * 
     * @param objects the list of scene objects (StraightSegment2D)
     * @param parentLine the parent's splitting line (null for root)
     * @return the BSP tree node representing the region.
     */
    @Override
    public BSPNode buildTree(List<StraightSegment2D> objects, Line2D parentLine) {

        // Base case: if 0 or 1 object, create a leaf.
        if (objects == null || objects.isEmpty() || objects.size() <= 1) {
            return new BSPNode(new ArrayList<>(objects));
        }
        
        // Evaluate Teller candidates.
        List<TellerCandidate> candidates = evaluateCandidatesTeller(objects, parentLine);
        Line2D bestLine;

        if (candidates.isEmpty()) {
            bestLine = objects.get(0).getSupportLine();
        } 
        else {
            // Partition candidates into two groups:
            // Group A: candidates with sigma >= tau.
            List<TellerCandidate> groupA = new ArrayList<>();
            List<TellerCandidate> groupB = new ArrayList<>();

            for (TellerCandidate cand : candidates) {
                if (cand.sigma >= tau) {
                    groupA.add(cand);
                } else {
                    groupB.add(cand);
                }
            }

            // Select best candidate based on group.
            if (!groupA.isEmpty()) {
                // Select candidate with maximum sigma.
                TellerCandidate best = groupA.get(0);
                for (TellerCandidate cand : groupA) {
                    if (cand.sigma > best.sigma) {
                        best = cand;
                    }
                }
                bestLine = best.supportLine;
            } else {
                // Select candidate with minimum f_d.
                TellerCandidate best = groupB.get(0);
                for (TellerCandidate cand : groupB) {
                    if (cand.f < best.f) {
                        best = cand;
                    }
                }
                bestLine = best.supportLine;
            }
        }
        
        // Create current node with chosen splitting line.
        BSPNode node = new BSPNode(bestLine);
        
        // Partition the objects.
        PartitionResult partition = partitionObjects(objects, bestLine);
        node.getCoplanarObjects().addAll(partition.coplanarList);
        
        // Recursively build subtrees, passing bestLine as parent's line for continuity.
        node.setLeft(buildTree(partition.negativeList, bestLine));
        node.setRight(buildTree(partition.positiveList, bestLine));
        
        return node;
    }

    /**
     * Evaluates Teller candidates for splitting using Teller's heuristic.
     * For each object in the current region, its support line is considered.
     * f (the number of objects cut by that line) and sigma (the ratio f/|S|) are computed.
     * The candidate is only accepted if it touches the parent's splitting line (if defined).
     * 
     * @param objects the list of objects.
     * @param parentLine the parent's splitting line, or null.
     * @return a list of TellerCandidate objects.
     */
    private List<TellerCandidate> evaluateCandidatesTeller(List<StraightSegment2D> objects, Line2D parentLine) {
        List<TellerCandidate> candidates = new ArrayList<>();
        int total = objects.size();

        for (StraightSegment2D obj : objects) {
            Line2D candidateLine = obj.getSupportLine();

            // Skip if candidateLine does not intersect parentLine (Parallel or coincident).
            if (parentLine != null && !candidateLine.intersect(parentLine)) {
                continue;
            }
            
            // Count f: number of objects cut by candidateLine.
            int f = 0;
            for (StraightSegment2D other : objects) {
                // Determine if other is cut by candidateLine by checking endpoints.
                double evalLeft = candidateLine.evaluate(other.getLeftEndpoint());
                double evalRight = candidateLine.evaluate(other.getRightEndpoint());

                // If the endpoints are on different sides of the line, the segment is cut. (left and right have different signs)
                if (evalLeft * evalRight < -EPSILON) {
                    f++;
                }
            }

            // Compute sigma: ratio f / |S|.
            double sigma = (double) f / total;

            candidates.add(new TellerCandidate(candidateLine, f, sigma));
        }
        return candidates;
    }

    /**
     * A helper class for Teller candidates.
     */
    private static class TellerCandidate {
        public final Line2D supportLine;
        public final int f;         // Number of objects cut by this support line.
        public final double sigma;  // Ratio f / |S|
        
        public TellerCandidate(Line2D supportLine, int f, double sigma) {
            this.supportLine = supportLine;
            this.f = f;
            this.sigma = sigma;
        }
    }
}
