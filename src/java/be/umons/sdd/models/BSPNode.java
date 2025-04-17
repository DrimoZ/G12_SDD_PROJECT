package be.umons.sdd.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in a BSP tree.
 * An internal node contains a partition line and pointers to left (negative side)
 * and right (positive side) subtrees. A leaf stores a list of objects.
 */
public class BSPNode {
    private final Line2D partition;
    private BSPNode left;   // Corresponds to the negative half-plane (d⁻)
    private BSPNode right;  // Corresponds to the positive half-plane (d⁺)
    private final List<StraightSegment2D> coplanarObjects; // Objects lying entirely on the partition
    
    /**
     * Constructs an internal node with the given partition line.
     * @param partition the splitting line
     */
    public BSPNode(Line2D partition) {
        this.partition = partition;
        this.coplanarObjects = new ArrayList<>();
    }
    
    /**
     * Constructs a leaf node.
     * @param objects the objects in this region
     */
    public BSPNode(List<StraightSegment2D> objects) {
        this.partition = null;
        this.coplanarObjects = objects;
    }
    
    public Line2D getPartition() {
        return partition;
    }
    
    public BSPNode getLeft() {
        return left;
    }
    
    public BSPNode getRight() {
        return right;
    }
    
    public void setLeft(BSPNode left) {
        this.left = left;
    }
    
    public void setRight(BSPNode right) {
        this.right = right;
    }
    
    public List<StraightSegment2D> getCoplanarObjects() {
        return coplanarObjects;
    }
    
    public boolean isLeaf() {
        return partition == null;
    }

    
    /**
     * Returns the number of nodes in the subtree rooted at this node.
     * For a leaf node, the size is 1.
     * For an internal node, the size is 1 plus the sum of the sizes of its left and right subtrees.
     * @return the number of nodes in the subtree
     */
    public int size() {
        if (isLeaf()) {
            return 1;
        } else {
            return 1 + left.size() + right.size();
        }
    }

    /**
     * Returns the height of the subtree rooted at this node.
     * For a leaf node, the height is 0.
     * For an internal node, the height is 1 plus the maximum of the heights
     * of its left and right subtrees.
     * @return the height of the subtree
     */
    public int height() {
        if (isLeaf()) {
            return 0;
        } else {
            return 1 + Math.max(left.height(), right.height());
        }
    }
    
    @Override
    public String toString() {
        if (isLeaf()) {
            return "Leaf: " + coplanarObjects.toString();
        } else {
            return "Node(partition=" + partition + ")";
        }
    }
}
