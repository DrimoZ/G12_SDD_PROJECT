package be.umons.sdd.interfaces;

import be.umons.sdd.models.Point2D;

public interface ObserverObserver {
    void onObserverSelected(Point2D position, double startAngle, double endAngle);
}
