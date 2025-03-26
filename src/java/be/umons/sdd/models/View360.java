package be.umons.sdd.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete 360° view from a given viewpoint.
 * The view is assembled as a list of AngularSegments in back-to-front (painter's)
 * order.
 */
public class View360 {
    private final List<AngularSegment> angularSegments;

    public View360() {
        angularSegments = new ArrayList<>();
    }

    public void addAngularSegment(AngularSegment as) {
        angularSegments.add(as);
    }

    public List<AngularSegment> getAngularSegments() {
        return angularSegments;
    }
    
    @Override
    public String toString() {
        return "View360" + angularSegments;
    }
}
