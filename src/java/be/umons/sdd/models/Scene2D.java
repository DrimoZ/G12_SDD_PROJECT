package be.umons.sdd.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 2D scene composed of straight segments.
 * The scene has a specified extent in the X and Y directions.
 */
public class Scene2D {
    private final List<StraightSegment2D> segments;
    private final int extentX;
    private final int extentY;
    
    /**
     * Constructs a Scene2D object with the specified list of segments and extents.
     *
     * @param segments the list of StraightSegment2D objects that make up the scene
     * @param extentX the extent of the scene in the X direction
     * @param extentY the extent of the scene in the Y direction
     */
    public Scene2D(List<StraightSegment2D> segments, int extentX, int extentY) {
        this.segments = segments;
        this.extentX = extentX;
        this.extentY = extentY;
    }
    
    /**
     * Retrieves the list of straight segments in the 2D scene.
     *
     * @return a list of {@link StraightSegment2D} objects representing the segments in the scene.
     */
    public List<StraightSegment2D> getSegments() {
        return segments;
    }

    /**
     * Retrieves the extent of the scene in the X direction.
     *
     * @return the extent of the scene in the X direction.
     */
    public int getExtentX() {
        return extentX;
    }

    /**
     * Retrieves the extent of the scene in the Y direction.
     *
     * @return the extent of the scene in the Y direction.
     */
    public int getExtentY() {
        return extentY; 
    }
    
    /**
     * Creates and returns a copy of the current Scene2D object.
     * This method performs a deep copy of the list of StraightSegment2D objects.
     *
     * @return a new Scene2D object that is a copy of the current instance.
     */
    public Scene2D copy() {
        List<StraightSegment2D> copy = new ArrayList<>();
        for (StraightSegment2D segment : segments) {
            copy.add(segment.copy());
        }
        return new Scene2D(copy, extentX, extentY);
    }

    @Override
    public String toString() {
        return "Scene[" + segments.size() + " segments, extentX=" + extentX + ", extentY=" + extentY + "]";
    }
}