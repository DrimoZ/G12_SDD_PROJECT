package be.umons.sdd.main.listeners;

import be.umons.sdd.builders.AbstractBSPTreeBuilder;

/**
 * Listener interface for receiving notifications when a tree builder is loaded.
 */
public interface TreeBuilderLoadedListener {
    void onBuilderLoaded(AbstractBSPTreeBuilder builder);
}