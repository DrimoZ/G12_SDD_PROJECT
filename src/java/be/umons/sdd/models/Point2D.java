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

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Point2D point2D = (Point2D) o;
        return Double.compare(point2D.x, x) == 0 && Double.compare(point2D.y, y) == 0;
    }
}