package be.umons.sdd.models;

import be.umons.sdd.utils.ColorParser;
import java.awt.Color;

/**
 * A straight segment in the plane.
 * Its support line is the line passing through its two endpoints.
 */
public class StraightSegment2D {
    private static final double EPSILON = 1e-6;

    private final Point2D start;
    private final Point2D end;
    private final Color color;
    
    /**
     * Constructs a straight segment.
     * @param start the starting point
     * @param end the ending point
     * @param color the color of the segment
     */
    public StraightSegment2D(Point2D start, Point2D end, Color color) {
        if (start == null || end == null || color == null) {
            throw new IllegalArgumentException("All parameters must be non-null.");
        }

        if (start.equals(end)) {
            throw new IllegalArgumentException("Start and end points must be distinct.");
        }

        this.start = start;
        this.end = end;
        this.color = color;
    }

    public StraightSegment2D(double x1, double y1, double x2, double y2, String colorName) {
        if (x1 == x2 && y1 == y2) {
            throw new IllegalArgumentException("Start and end points must be distinct.");
        }

        this.start = new Point2D(x1, y1);
        this.end = new Point2D(x2, y2);
        this.color = ColorParser.getColor(colorName);
    }

    public Line2D getSupportLine() {
        return new Line2D(start, end);
    }
    
    public Point2D getCenter() {
        return new Point2D((start.x + end.x) / 2.0, (start.y + end.y) / 2.0);
    }

    public Point2D getStart() {
        return start;
    }

    public Point2D getEnd() {
        return end;
    }

    public Color getColor() {
        return color;
    }
    
    public StraightSegment2D[] split(Line2D splitter) {
        double evalStart = splitter.evaluate(start);
        double evalEnd = splitter.evaluate(end);

        // If both endpoints are on the same side, no split occurs.
        if (evalStart * evalEnd > 0) {

            // Return the object in the appropriate half-plane.
            if (evalStart > 0) {
                return new StraightSegment2D[] { this, null };
            } else {
                return new StraightSegment2D[] { null, this };
            }
        }

        // If both evaluations are nearly zero, consider the segment as coplanar.
        if (Math.abs(evalStart) < EPSILON && Math.abs(evalEnd) < EPSILON) {
            return new StraightSegment2D[] { this, null };
        }

        // Otherwise, compute the intersection point.
        double t = evalStart / (evalStart - evalEnd);
        double ix = start.x + t * (end.x - start.x);
        double iy = start.y + t * (end.y - start.y);
        Point2D intersection = new Point2D(ix, iy);
        
        StraightSegment2D segPositive;
        StraightSegment2D segNegative;

        if (evalStart > 0) {
            segPositive = new StraightSegment2D(start, intersection, color);
            segNegative = new StraightSegment2D(intersection, end, color);
        } else {
            segPositive = new StraightSegment2D(intersection, end, color);
            segNegative = new StraightSegment2D(start, intersection, color);
        }

        // Remove degenerate segments.
        if (segPositive.getStart().equals(segPositive.getEnd())) segPositive = null;
        if (segNegative.getStart().equals(segNegative.getEnd())) segNegative = null;

        return new StraightSegment2D[] { segPositive, segNegative };
    }
    
    public Point2D getLeftEndpoint() {
        // For simplicity, define "left" as the endpoint with the smaller x-coordinate.
        return start.x <= end.x ? start : end;
    }
    
    public Point2D getRightEndpoint() {
        return start.x > end.x ? start : end;
    }

    /**
     * Creates and returns a copy of this StraightSegment2D object.
     *
     * @return a new StraightSegment2D object that is a copy of this instance.
     */
    public StraightSegment2D copy() {
        return new StraightSegment2D(start.copy(), end.copy(), color);
    }
    
    @Override
    public String toString() {
        return "StraightSegment[" + start + " -> " + end + ", " + color.toString() + "]";
    }
}
