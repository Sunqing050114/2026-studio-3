package com.csse3200.game.components.maps;

import java.util.Set;

public interface MapStateService {
    String getCurrentNodeId();

    MapNode getNode(String nodeId);

    NodeState getNodeState(String nodeId);

    Set<String> getSelectableNodeIds();

    boolean isSelectable(String nodeId);

    boolean isLocked(String nodeId);

    boolean requestMove(String nodeId);
}
