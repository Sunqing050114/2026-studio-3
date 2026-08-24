package com.csse3200.game.components.maps;

import java.util.*;

public class FakeMapStateService implements MapStateService {

    private final Map<String, MapNode> nodes = new LinkedHashMap<>();
    private final Map<String, NodeState> states = new HashMap<>();
    private final Map<String, int[]> layout =  new HashMap<>();
    private String currentNodeId;

    public FakeMapStateService() {
        addNode("n0", RoomType.COMBAT, List.of("n1", "n2"), 0, 1);
        addNode("n1", RoomType.COMBAT, List.of("n0", "n3"), 1, 0);
        addNode("n2", RoomType.SHOP, List.of("n0", "n3"), 1, 2);
        addNode("n3", RoomType.COMBAT, List.of("n1", "n2", "boss"), 2, 1);
        addNode("boss", RoomType.FINAL, List.of("n3"), 3, 1);

        currentNodeId = "n0";
        states.put("n0", NodeState.CURRENT);
        states.put("n1", NodeState.AVAILABLE);
        states.put("n2", NodeState.AVAILABLE);
        states.put("n3", NodeState.LOCKED);
        states.put("boss", NodeState.LOCKED);
    }

    private void addNode(String id, RoomType roomType, List<String> connections, int col, int row) {
        nodes.put(id ,new MapNode(id, roomType, new ArrayList<>(connections)));
        layout.put(id , new int[] {col, row});
    }

    public int[] getLayoutPosition(String nodeId) {
        return layout.get(nodeId);
    }

    @Override
    public String getCurrentNodeId() {
        return currentNodeId;
    }

    @Override
    public MapNode getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    @Override
    public NodeState getNodeState(String nodeId) {
        return states.get(nodeId);
    }

    @Override
    public Set<String> getSelectableNodeIds() {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, NodeState> e : states.entrySet()) {
            if (e.getValue() == NodeState.AVAILABLE) {
                result.add(e.getKey());
            }
        }
        return result;
    }

    @Override
    public boolean isSelectable(String nodeId) {
        return nodeId != null && states.get(nodeId) == NodeState.AVAILABLE;
    }

    @Override
    public boolean isLocked(String nodeId) {
        return nodeId != null && states.get(nodeId) == NodeState.LOCKED;
    }

    @Override
    public boolean requestMove(String nodeId) {
        if (!isSelectable(nodeId)) {
            return false;
        }

        states.put(currentNodeId, NodeState.COMPLETED);
        states.put(nodeId, NodeState.CURRENT);
        MapNode newCurrent = nodes.get(nodeId);
        if  (newCurrent != null) {
            for (String connectedId : newCurrent.getConnections()) {
                states.putIfAbsent(connectedId, NodeState.LOCKED);
                if (states.get(connectedId) == NodeState.LOCKED) {
                    states.put(connectedId, NodeState.AVAILABLE);
                }
            }
        }
        currentNodeId = nodeId;
        return true;
    }
}
