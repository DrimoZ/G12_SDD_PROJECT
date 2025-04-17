package be.umons.sdd.models;

/**
 * Represents a line in R² given by the equation: a*x + b*y + c = 0.
 */
public class Line2D {
    private final double a;
    private final double b;
    private final double c;
    private static final double EPSILON = 1e-6;
    
    /**
     * Constructs a line passing through two points.
     * @param p1 first point
     * @param p2 second point
     */
    public Line2D(Point2D p1, Point2D p2) {
        double A = p2.y - p1.y;
        double B = p1.x - p2.x;
        double C = -(A * p1.x + B * p1.y);

        double norm = Math.sqrt(A * A + B * B);
        if (norm == 0) {
            System.out.println("[Line2D Constructor] Points must be distinct.");
            System.out.println("p1: " + p1.toString() + ", p2: " + p2.toString());
            throw new IllegalArgumentException("[Line2D Constructor] Points must be distinct.");
        }

        this.a = A / norm;
        this.b = B / norm;
        this.c = C / norm;
    }
    
    /**
     * Constructs a line from its coefficients.
     * @param a coefficient a
     * @param b coefficient b
     * @param c coefficient c
     */
    public Line2D(double a, double b, double c) {
        double norm = Math.sqrt(a * a + b * b);
        if (norm == 0) {
            throw new IllegalArgumentException("[Line2D Constructor] Invalid line coefficients.");
        }

        this.a = a / norm;
        this.b = b / norm;
        this.c = c / norm;
    }
    
    /**
     * Evaluates the line equation at point p.
     * @param p the point to evaluate
     * @return a signed distance value (proportional to the distance)
     */
    public double evaluate(Point2D p) {
        return a * p.x + b * p.y + c;
    }
    
    /**
     * Checks if this line touches (i.e. intersects) the given other line.
     * Two lines touch if they are not parallel, or if they are coincident.
     * @param other The other Line2D object to check for intersection.
     * @return true if the lines touch, false otherwise.
     */
    public boolean touches(Line2D other) {
        // Lines are parallel if their normals are colinear.
        double det = this.a * other.b - other.a * this.b;
        
        if (Math.abs(det) > EPSILON) {
            // Not parallel --> they intersect at one point.
            return true;
        } else {
            // Parallel: they "touch" if they are coincident.
            return Math.abs(this.c - other.c) < EPSILON;
        }
    }

    /**
     * Determines if this line intersects with another line.
     *
     * @param other The other Line2D object to check for intersection.
     * @return true if the lines intersect, false otherwise.
     */
    public boolean intersect(Line2D other) {
        double det = this.a * other.b - other.a * this.b;

        return Math.abs(det) >= EPSILON;
    }

    public double getA() { return a; }
    public double getB() { return b; }
    public double getC() { return c; }
    
    @Override
    public String toString() {
        return String.format("Line2D: %.4fx + %.4fy + %.4f = 0", a, b, c);
    }
}
