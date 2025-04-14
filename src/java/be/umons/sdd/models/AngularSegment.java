package be.umons.sdd.models;

/**
 * Represents a fragment (here, a StraightSegment2D) as seen from the observer,
 * projected onto the 360° view. The angular interval is expressed in radians.
 */
public class AngularSegment {
    private final double startAngle; // in radians
    private final double endAngle;   // in radians
    private final StraightSegment2D segment;

    public AngularSegment(double startAngle, double endAngle, StraightSegment2D segment) {
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.segment = segment;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public double getEndAngle() {
        return endAngle;
    }

    public StraightSegment2D getSegment() {
        return segment;
    }
    
    @Override
    public String toString() {
        // Convert to degrees for readability.
        return "AngularSegment[" + startAngle + " (" + Math.toDegrees(startAngle) + "°), " + endAngle + " (" + Math.toDegrees(endAngle) + "°)]";
    }
}