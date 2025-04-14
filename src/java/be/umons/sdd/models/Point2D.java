package be.umons.sdd.models;

/**
 * Represents a point in R².
 */
public class Point2D {
    public double x;
    public double y;
    
    /**
     * Constructs a 2D point.
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates and returns a copy of this Point2D object.
     *
     * @return a new Point2D object with the same x and y coordinates as this object.
     */
    public Point2D copy() {
        return new Point2D(x, y);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}