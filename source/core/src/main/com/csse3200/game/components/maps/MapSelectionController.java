package com.csse3200.game.components.maps;

import com.csse3200.game.events.EventHandler;

public class MapSelectionController {
    private final MapStateService mapState;
    private final EventHandler events;

    public MapSelectionController(MapStateService mapState) { this(mapState, new EventHandler());}

    public MapSelectionController(MapStateService mapState, EventHandler events) {
        this.mapState = mapState;
        this.events = events;
    }

    public EventHandler getEvents() {
        return events;
    }

    public boolean onNodeClicked(String nodeId) {
        if (nodeId == null) {
            return false;
        }

        if (!mapState.isSelectable(nodeId)) {
            events.trigger("nodeLocked", nodeId);
            return false;
        }

        boolean accepted = mapState.requestMove(nodeId);
        if (accepted) {
            events.trigger("nodeSelected", nodeId);
        } else  {
            events.trigger("nodeSelectionRejected", nodeId);
        }
        return accepted;
    }
}

