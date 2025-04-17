package be.umons.sdd.builders;

import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Line2D;
import be.umons.sdd.models.Point2D;
import be.umons.sdd.models.StraightSegment2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for BSP tree builders.
 * It provides common helper methods for candidate evaluation and partitioning.
 */
public abstract class BSPTreeBuilder {

    protected static final double EPSILON = 1e-6;
    
    /**
     * Builds a BSP tree from a list of objects with an optional parent splitting line.
     * 
     * @param objects the list of scene objects (StraightSegment2D) to partition.
     * @param parentLine the parent's splitting line (null for the root).
     * @return the BSP tree node representing the region.
     */
    public abstract BSPNode buildTree(List<StraightSegment2D> objects, Line2D parentLine);
    
    /**
     * Evaluates candidate splitting lines from the given objects.
     * Only candidates whose support line touches the parent's line (if provided) are accepted.
     * The candidate "balance" is the absolute difference between the number of objects
     * lying entirely in the positive and negative half-planes.
     * 
     * @param objects the list of objects.
     * @param parentLine the parent's splitting line, or null if none.
     * @return a list of Candidate objects.
     */
    protected List<Candidate> evaluateCandidates(List<StraightSegment2D> objects, Line2D parentLine) {
        List<Candidate> candidates = new ArrayList<>();
        for (StraightSegment2D obj : objects) {
            Line2D candidateLine = obj.getSupportLine();
            if (parentLine != null && !candidateLine.touches(parentLine)) {
                continue;
            }
            
            int countPositive = 0;
            int countNegative = 0;

            for (StraightSegment2D other : objects) {
                Point2D center = other.getCenter();
                double eval = candidateLine.evaluate(center);

                if (eval > EPSILON) {
                    countPositive++;
                } else if (eval < -EPSILON) {
                    countNegative++;
                }
            }

            double balance = Math.abs(countPositive - countNegative);

            // Check free split condition.
            boolean freeSplit = false;
            if (parentLine != null) {
                freeSplit = (Math.abs(parentLine.evaluate(obj.getLeftEndpoint())) < EPSILON &&
                             Math.abs(parentLine.evaluate(obj.getRightEndpoint())) < EPSILON);
            }
            if (freeSplit) {
                // Prioritize free splits.
                balance = -Double.MAX_VALUE; 
            }

            candidates.add(new Candidate(candidateLine, balance));
        }

        return candidates;
    }
    
    /**
     * Selects the candidate with the minimal balance value.
     * 
     * @param candidates list of candidates.
     * @return the best candidate.
     */
    protected Candidate selectBestCandidate(List<Candidate> candidates) {
        Candidate best = candidates.get(0);
        for (Candidate cand : candidates) {
            if (cand.balance < best.balance) {
                best = cand;
            }
        }
        return best;
    }
    
    /**
     * Partitions the objects into three groups relative to a splitting line:
     * - coplanarList: objects whose centers evaluate nearly zero.
     * - positiveList: objects entirely in the positive half-plane.
     * - negativeList: objects entirely in the negative half-plane.
     * If an object crosses the line, it is split.
     * 
     * @param objects the list of objects.
     * @param splittingLine the splitting line.
     * @return a PartitionResult containing the three groups.
     */
    protected PartitionResult partitionObjects(List<StraightSegment2D> objects, Line2D splittingLine) {
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
                double e1 = splittingLine.evaluate(obj.getLeftEndpoint());
                double e2 = splittingLine.evaluate(obj.getRightEndpoint());

                if (e1 * e2 < -EPSILON) {
                    StraightSegment2D[] fragments = obj.split(splittingLine);

                    if (fragments[0] != null) positiveList.add(fragments[0]);
                    if (fragments[1] != null) negativeList.add(fragments[1]);
                } else {
                    positiveList.add(obj);
                }
            } else { // eval < 0
                double e1 = splittingLine.evaluate(obj.getLeftEndpoint());
                double e2 = splittingLine.evaluate(obj.getRightEndpoint());

                if (e1 * e2 < -EPSILON) {
                    StraightSegment2D[] fragments = obj.split(splittingLine);

                    if (fragments[1] != null) negativeList.add(fragments[1]);
                    if (fragments[0] != null) positiveList.add(fragments[0]);
                } else {
                    negativeList.add(obj);
                }
            }
        }
        
        return new PartitionResult(coplanarList, positiveList, negativeList);
    }
    
    /**
     * Helper class to encapsulate partition results.
     */
    protected static class PartitionResult {
        public final List<StraightSegment2D> coplanarList;
        public final List<StraightSegment2D> positiveList;
        public final List<StraightSegment2D> negativeList;
        
        public PartitionResult(List<StraightSegment2D> coplanarList,
                               List<StraightSegment2D> positiveList,
                               List<StraightSegment2D> negativeList) {
            this.coplanarList = coplanarList;
            this.positiveList = positiveList;
            this.negativeList = negativeList;
        }
    }
    
    /**
     * Helper class representing a candidate splitting line.
     */
    protected static class Candidate {
        public final Line2D supportLine;
        public final double balance;
        
        public Candidate(Line2D supportLine, double balance) {
            this.supportLine = supportLine;
            this.balance = balance;
        }
    }
}
