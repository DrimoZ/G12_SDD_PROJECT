package be.umons.sdd.interfaces;

import be.umons.sdd.models.BSPNode;

public interface BSPNodeObserver {
    void onBSPUpdated(BSPNode node);
}
