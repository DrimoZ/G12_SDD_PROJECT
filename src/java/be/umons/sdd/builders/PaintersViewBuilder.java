package be.umons.sdd.builders;

import be.umons.sdd.models.AngularSegment;
import be.umons.sdd.models.BSPNode;
import be.umons.sdd.models.Line2D;
import be.umons.sdd.models.Point2D;
import be.umons.sdd.models.StraightSegment2D;
import be.umons.sdd.models.View360;
import java.util.List;

public class PaintersViewBuilder {
    /**
     * Builds a View360 from the given BSP tree root by applying the painter's algorithm.
     * The painter's algorithm traverses the tree in a depth-first order, but instead of
     * visiting nodes in a predefined order, it visits nodes in the order of their
     * distance from the viewpoint. This is done by recursively traversing the tree,
     * and for each subtree, first visiting the subtree that is furthest from the
     * viewpoint. This has the effect of rendering objects in back-to-front order,
     * which is the order in which they are visible from the viewpoint.
     * 
     * @param root      The root of the BSP tree to traverse.
     * @param viewPoint The point of view to compute the view from.
     * @return          A View360 object containing the visible segments in back-to-front order.
     */
    public static View360 paintersAlgorithm(BSPNode root, Point2D viewPoint) {
        View360 view360 = new View360();
        paintersAlgorithmHelper(root, viewPoint, view360);
        return view360;
    }

    /**
     * Recursive helper method for the painter's algorithm.
     * It scans the given subtree and adds the angular segments that are visible from the viewpoint to the given view.
     * The method is called recursively for the left and right subtrees, depending on the position of the viewpoint relative to the current node's partition line.
     * If the viewpoint is on the partition line, the coplanar objects are not rendered.
     * 
     * @param node      The current node in the BSP tree.
     * @param viewPoint The viewpoint from which the scene is rendered.
     * @param view360   The view to which the angular segments are added.
     */
    private static void paintersAlgorithmHelper(BSPNode node, Point2D viewPoint, View360 view360) {
        // Should Throw an error ? 
        // There can be null nodes if a node has no child for a side ?
        if (node == null) {
            return;
        }

        // Node is leaf = > Scan-convert the object fragments in S(ν)
        if (node.isLeaf()) {
            List<StraightSegment2D> segments = node.getCoplanarObjects();
            for (StraightSegment2D seg : segments) {
                AngularSegment as = computeAngularSegment(seg, viewPoint);
                if (as != null) {
                    view360.addAngularSegment(as);
                }
            }
            return;
        }

        // For internal nodes, determine on which side of the partition the viewpoint lies
        // Should check if partition line exists ?
        int viewPointPosition = classifyPoint(node.getPartition(), viewPoint);
        
        // Viewpoint in positive half-space: 
        if (viewPointPosition > 0) {
            // Process left (negative) subtree first
            paintersAlgorithmHelper(node.getLeft(), viewPoint, view360);
            
            // Process coplanar objects stored at this node
            for (StraightSegment2D seg : node.getCoplanarObjects()) {
                AngularSegment as = computeAngularSegment(seg, viewPoint);
                if (as != null) {
                    view360.addAngularSegment(as);
                }
            }
            
            // Then process right (positive) subtree
            paintersAlgorithmHelper(node.getRight(), viewPoint, view360);

        // Viewpoint in negative half-space: 
        } else if (viewPointPosition < 0) {
            // Process right subtree first
            paintersAlgorithmHelper(node.getRight(), viewPoint, view360);
            
            // Process the node's coplanar objects
            for (StraightSegment2D seg : node.getCoplanarObjects()) {
                AngularSegment as = computeAngularSegment(seg, viewPoint);
                if (as != null) {
                    view360.addAngularSegment(as);
                }
            }
            
            // Finally, process left subtree
            paintersAlgorithmHelper(node.getLeft(), viewPoint, view360);

        // Viewpoint exactly on the partition line:
        } else {
            // do not render the coplanar objects (not visible) 
            // Mayby as a point ???
            paintersAlgorithmHelper(node.getRight(), viewPoint, view360);
            paintersAlgorithmHelper(node.getLeft(), viewPoint, view360);
        }
    }

    /**
     * Classify the position of the viewpoint relative to the BSP node's partition line.
     * Returns 1 if the viewpoint is in the positive half-plane, -1 if in the negative,
     * or 0 if exactly on the partition.
     *
     * @param partition The partition line.
     * @param viewPoint The observer's position.
     * @return          1, -1, or 0.
     */
    private static int classifyPoint(Line2D partition, Point2D viewPoint) {
        double cross = partition.evaluate(viewPoint);

        if (cross > 0) {
            return 1;
        } else if (cross < 0) {
            return -1;
        } else {
            return 0;
        }
    }
    
    /**
     * Computes the angular projection of a StraightSegment2D as seen from the viewpoint.
     * The angular interval (in radians) is computed using Math.atan2.
     * Angles are normalized to [0, 2π). If the segment spans the 0° cut,
     * the interval is adjusted accordingly.
     *
     * @param segmentToProject  The segment to project.
     * @param viewPoint         The observer's position.
     * @return                  An AngularSegment representing the segment's projection.
     */
    private static AngularSegment computeAngularSegment(StraightSegment2D segmentToProject, Point2D viewPoint) {
        Point2D segmentStart = segmentToProject.getStart();
        Point2D segmentEnd = segmentToProject.getEnd();
        
        // Compute angles (in radians)
        double angle1 = Math.atan2(segmentStart.y - viewPoint.y, segmentStart.x - viewPoint.x);
        double angle2 = Math.atan2(segmentEnd.y - viewPoint.y, segmentEnd.x - viewPoint.x);
        
        // Normalize angles to [0, 2π)
        angle1 = (angle1 < 0) ? angle1 + 2 * Math.PI : angle1;
        angle2 = (angle2 < 0) ? angle2 + 2 * Math.PI : angle2;
    
        // Compute both the direct difference and the complementary difference
        double directDiff = Math.abs(angle2 - angle1);
        double complementDiff = 2 * Math.PI - directDiff;
        
        double startAngle, endAngle;
        
        // Choose the minimal angle interval
        if (directDiff <= complementDiff) {
            // No need to invert or normalize because the angles are already normalized ([0, 2π))
            startAngle = Math.min(angle1, angle2);
            endAngle = Math.max(angle1, angle2);
        } else {
            // Use the complementary interval for the minimal arc
            // The startAngle will be the one that is "larger" (closer to 2π)
            if (angle1 > angle2) {
                startAngle = angle1;
                endAngle = angle2 + 2 * Math.PI;
            } else {
                startAngle = angle2;
                endAngle = angle1 + 2 * Math.PI;
            }
        }
        
        return new AngularSegment(startAngle, endAngle, segmentToProject);
    }
    
}
