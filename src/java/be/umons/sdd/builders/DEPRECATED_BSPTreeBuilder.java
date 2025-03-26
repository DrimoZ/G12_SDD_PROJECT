package be.umons.sdd.builders;

import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Line2D;
import be.umons.sdd.models.Point2D;
import be.umons.sdd.models.StraightSegment2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A BSP tree builder that constructs a deterministic BSP tree in R².
 * The splitting line chosen at each node must "touch" the parent's splitting line (if any)
 * and should produce as balanced a partition as possible.
 */
public class DEPRECATED_BSPTreeBuilder {
    private static final double EPSILON = 1e-6;
    
    /**
     * Builds a deterministic BSP tree from a set of scene objects that are randomly shuffled.
     *
     * @param objects the set of scene objects in the current region.
     * @return the BSP tree node representing the region.
     */
    public static BSPNode buildRandom(List<StraightSegment2D> objects) {
        Collections.shuffle(objects);
        return buildDeterministic(objects, null);
    }

    /**
     * Builds a deterministic BSP tree from a set of scene objects.
     * 
     * @param objects the set of scene objects in the current region.
     * @param parentLine the parent's splitting line (null for the root).
     * @return the BSP tree node representing the region.
     */
    public static BSPNode buildDeterministic(List<StraightSegment2D> objects, Line2D parentLine) {
        // Base case: if there is 0 or 1 object, create a leaf.
        if (objects == null || objects.isEmpty() || objects.size() <= 1) {
            return new BSPNode(new ArrayList<>(objects));
        }
        
        // 1. Evaluate candidate splitting lines.
        List<Candidate> candidateList = evaluateCandidates(objects, parentLine);
        
        // 2. Select the best candidate.
        Line2D bestLine;
        if (candidateList.isEmpty()) {
            // If no candidate satisfies the continuity constraint, use the first object's support.
            bestLine = objects.get(0).getSupportLine();
        } 
        else {
            bestLine = selectBestCandidate(candidateList).supportLine;
        }
        
        // 3. Create current node with the chosen splitting line.
        BSPNode node = new BSPNode(bestLine);
        
        // 4. Partition the objects according to bestLine.
        PartitionResult partition = partitionObjects(objects, bestLine);
        
        // Save coplanar objects in the node.
        node.getCoplanarObjects().addAll(partition.coplanarList);
        
        // 5. Recursively build subtrees.
        // Note: Pass bestLine as parentLine for continuity.
        node.setLeft(buildDeterministic(partition.negativeList, bestLine));
        node.setRight(buildDeterministic(partition.positiveList, bestLine));
        
        return node;
    }
    
    /**
     * Evaluates each object's support line as a candidate splitting line.
     * The candidate is only accepted if it touches the parent's splitting line (if given)
     * and its "balance" (the absolute difference between the counts in the positive and negative half-planes)
     * is computed.
     * 
     * @param objects the list of scene objects.
     * @param parentLine the parent's splitting line, or null if none.
     * @return a list of Candidate objects.
     */
    private static List<Candidate> evaluateCandidates(List<StraightSegment2D> objects, Line2D parentLine) {
        List<Candidate> candidates = new ArrayList<>();
        
        for (StraightSegment2D obj : objects) {
            Line2D candidateLine = obj.getSupportLine();

            // Enforce continuity constraint: if parent's line exists, candidate must touch it.
            if (parentLine != null && !candidateLine.touches(parentLine)) {
                continue;
            }

            // Compute balance: count how many objects lie entirely in the positive and negative half-planes.
            int countPositive = 0;
            int countNegative = 0;

            for (StraightSegment2D other : objects) {
                Point2D center = other.getCenter();
                double eval = candidateLine.evaluate(center);

                if (eval > EPSILON) {
                    countPositive++;
                } 
                else if (eval < -EPSILON) {
                    countNegative++;
                }
                // If eval is near zero, consider the object as coplanar.
            }
            double balance = Math.abs(countPositive - countNegative);
            
            // Check if the object qualifies as a free split:
            // (i.e., if parent's line exists and both endpoints lie on the parent's line)
            boolean freeSplit = false;

            if (parentLine != null) {
                freeSplit = (Math.abs(parentLine.evaluate(obj.getLeftEndpoint())) < EPSILON && Math.abs(parentLine.evaluate(obj.getRightEndpoint())) < EPSILON);
            }

            if (freeSplit) {
                // Prioritize free splits by giving them an extremely favorable balance.
                balance = -Double.MAX_VALUE;
            }
            candidates.add(new Candidate(candidateLine, balance));
        }
        
        return candidates;
    }
    
    /**
     * Selects the candidate with the minimum balance value.
     * 
     * @param candidates the list of candidates.
     * @return the best candidate.
     */
    private static Candidate selectBestCandidate(List<Candidate> candidates) {
        Candidate best = candidates.get(0);

        for (Candidate cand : candidates) {
            if (cand.balance < best.balance) {
                best = cand;
            }
        }
        return best;
    }
    
    /**
     * Partitions the given scene objects into three groups with respect to a splitting line:
     * - coplanarList: objects whose centers evaluate to near zero (they lie on the line),
     * - positiveList: objects whose centers lie in the positive half-plane,
     * - negativeList: objects whose centers lie in the negative half-plane.
     * 
     * For objects that cross the line, the method splits them and adds the appropriate fragments.
     * 
     * @param objects the list of scene objects to partition.
     * @param splittingLine the splitting line.
     * @return a PartitionResult containing the three groups.
     */
    private static PartitionResult partitionObjects(List<StraightSegment2D> objects, Line2D splittingLine) {
        List<StraightSegment2D> coplanarList = new ArrayList<>();
        List<StraightSegment2D> positiveList = new ArrayList<>();
        List<StraightSegment2D> negativeList = new ArrayList<>();
        
        for (StraightSegment2D obj : objects) {

            Point2D center = obj.getCenter();
            double eval = splittingLine.evaluate(center);

            if (Math.abs(eval) < EPSILON) {
                coplanarList.add(obj);
            } 
            else if (eval > 0) {
                // Check if object crosses the line by evaluating its endpoints.
                double e1 = splittingLine.evaluate(obj.getLeftEndpoint());
                double e2 = splittingLine.evaluate(obj.getRightEndpoint());

                if (e1 * e2 < -EPSILON) {
                    // Object crosses: split it.
                    StraightSegment2D[] fragments = obj.split(splittingLine);

                    if (fragments[0] != null) {
                        positiveList.add(fragments[0]);
                    }
                    if (fragments[1] != null) {
                        negativeList.add(fragments[1]);
                    }

                } 
                else {
                    positiveList.add(obj);
                }
            } 
            else { // eval < 0
                double e1 = splittingLine.evaluate(obj.getLeftEndpoint());
                double e2 = splittingLine.evaluate(obj.getRightEndpoint());

                if (e1 * e2 < -EPSILON) {
                    StraightSegment2D[] fragments = obj.split(splittingLine);
                    
                    if (fragments[1] != null) {
                        negativeList.add(fragments[1]);
                    }
                    if (fragments[0] != null) {
                        positiveList.add(fragments[0]);
                    }

                } 
                else {
                    negativeList.add(obj);
                }
            }
        }
        
        return new PartitionResult(coplanarList, positiveList, negativeList);
    }

    /**
     * A helper class to store the result of partitioning scene objects.
     */
    private static class PartitionResult {
        List<StraightSegment2D> coplanarList;
        List<StraightSegment2D> positiveList;
        List<StraightSegment2D> negativeList;
        
        PartitionResult(List<StraightSegment2D> coplanarList, List<StraightSegment2D> positiveList, List<StraightSegment2D> negativeList) {
            this.coplanarList = coplanarList;
            this.positiveList = positiveList;
            this.negativeList = negativeList;
        }
    }
    
    /**
     * A candidate splitting line associated with a scene object.
     */
    private static class Candidate {
        public Line2D supportLine;
        public double balance;
        
        Candidate(Line2D supportLine, double balance) {
            this.supportLine = supportLine;
            this.balance = balance;
        }
    }
}