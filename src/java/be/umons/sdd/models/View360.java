package be.umons.sdd.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete 360° view from a given viewpoint.
 * The view is assembled as a list of AngularSegments in back-to-front (painter's)
 * order.
 */
public class View360 {
    private List<AngularSegment> angularSegments;

    public View360() {
        angularSegments = new ArrayList<>();
    }

    public void addAngularSegment(AngularSegment as) {
        if (as == null) {
            throw new IllegalArgumentException("AngularSegment cannot be null.");
        }

        angularSegments.add(as);


        // if (angularSegments.isEmpty()) {
        //     angularSegments.add(as);
        //     return;
        // }

        // List<AngularSegment> newList = new ArrayList<>();
        // for (AngularSegment existing : angularSegments) {

        //     // If existing is fully contained in the new segment, do nothing.
        //     if (existing.getStartAngle() >= as.getStartAngle() && existing.getEndAngle() <= as.getEndAngle()) {
        //     }

        //     // If existing is not contained in the new segment, add it into the new list.
        //     else if (existing.getStartAngle() >= as.getEndAngle() || existing.getEndAngle() <= as.getStartAngle()) {
        //         newList.add(existing);
        //     }

        //     // If new segment is fully contained in existing segment, create 2 new segments.
        //     else if (existing.getStartAngle() < as.getStartAngle() && existing.getEndAngle() > as.getEndAngle()) {
        //         AngularSegment segment1 = new AngularSegment(existing.getStartAngle(), as.getStartAngle(), existing.getSegment());
        //         AngularSegment segment2 = new AngularSegment(as.getEndAngle(), existing.getEndAngle(), existing.getSegment());
        //         newList.add(segment1);
        //         newList.add(segment2);
        //     }

        //     // If part of existing is not contained in the new segment, add that part into the new list.
        //     else if (existing.getStartAngle() < as.getStartAngle() && existing.getEndAngle() > as.getStartAngle()) {
        //         AngularSegment segment = new AngularSegment(as.getStartAngle(), existing.getEndAngle(), existing.getSegment());
        //         newList.add(segment);
        //     }

        //     else if (existing.getStartAngle() < as.getEndAngle() && existing.getEndAngle() > as.getEndAngle()) {
        //         AngularSegment segment = new AngularSegment(existing.getStartAngle(), as.getEndAngle(), existing.getSegment());
        //         newList.add(segment);
        //     }

        //     else {
        //         throw new IllegalArgumentException("Error in View360.addAngularSegment()");
        //     }
        // }

        // newList.add(as);
        // angularSegments = newList;
    }

    public List<AngularSegment> getAngularSegments() {
        System.out.println(angularSegments.size());

        for (AngularSegment as : angularSegments) {
            System.out.println(as.toString());
        }

        return angularSegments;
    }
    
    @Override
    public String toString() {
        return "View360" + angularSegments;
    }
}

