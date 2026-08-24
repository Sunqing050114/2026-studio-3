package com.csse3200.game.components.maps;

import java.util.Collections;
import java.util.List;

public class MapNode {
    private final String id;
    private final RoomType roomType;
    private final List<String> connections;

    public MapNode(String id, RoomType roomType, List<String> connections) {
        this.id = id;
        this.roomType = roomType;
        this.connections = Collections.unmodifiableList(connections);
    }

    public String getId() {return id;}

    public RoomType getRoomType() {return roomType;}

    public List<String> getConnections() {return connections;}

    @Override
    public String toString() {
        return "MapNode{" + id + ", roomType=" + roomType + ", connections=" + connections + "}";
    }
}
