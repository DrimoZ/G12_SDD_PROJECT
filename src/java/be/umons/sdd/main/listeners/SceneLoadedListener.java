package be.umons.sdd.main.listeners;

import be.umons.sdd.models.Scene2D;

/**
 * Listener interface for receiving notifications when a 2D scene is loaded.
 */
public interface SceneLoadedListener {
    void onSceneLoaded(Scene2D scene);
}
