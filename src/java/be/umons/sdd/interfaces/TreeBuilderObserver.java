package be.umons.sdd.interfaces;

import be.umons.sdd.builders.BSPTreeBuilder;

public interface TreeBuilderObserver {
    void onTreeBuilderSelected(BSPTreeBuilder treeBuilder);
}
