package be.umons.sdd.interfaces;

import be.umons.sdd.models.Scene2D;

public interface SceneObserver {
    void onSceneSelected(Scene2D scene);
}
