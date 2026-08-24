package com.csse3200.game.components.maps;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


class MapSelectionControllerTest {

    private FakeMapStateService state;
    private MapSelectionController controller;
    private AtomicReference<String> selected;
    private AtomicReference<String> locked;

    @BeforeEach
    void setUp() {
        state = new FakeMapStateService();
        controller = new MapSelectionController(state);

        selected = new AtomicReference<>();
        locked = new AtomicReference<>();
        controller.getEvents().addListener("nodeSelected", (String id) -> selected.set(id));
        controller.getEvents().addListener("nodeLocked", (String id) -> locked.set(id));
    }

    @Test
    void selectableNodeCommitsMoveAndFiresSelected() {
        boolean accepted = controller.onNodeClicked("n1");

        assertTrue(accepted);
        assertEquals("n1", selected.get());
        assertNull(locked.get());
        assertEquals("n1", state.getCurrentNodeId(), "player should have moved");
        assertEquals(NodeState.CURRENT, state.getNodeState("n1"));
        assertEquals(NodeState.COMPLETED, state.getNodeState("n0"), "old current node completes");
    }

    @Test
    void lockedNodeSendsNoMoveAndFiresLocked() {
        boolean accepted = controller.onNodeClicked("n3"); // LOCKED at start

        assertFalse(accepted);
        assertEquals("n3", locked.get());
        assertNull(selected.get(), "no selection should be sent for a locked node");
        assertEquals("n0", state.getCurrentNodeId(), "player should not have moved");
        assertEquals(NodeState.LOCKED, state.getNodeState("n3"));
    }

    @Test
    void unknownNodeIsTreatedAsLockedNotSelected() {
        boolean accepted = controller.onNodeClicked("does-not-exist");

        assertFalse(accepted);
        assertNull(selected.get());
        assertEquals("n0", state.getCurrentNodeId());
    }

    @Test
    void nullClickIsIgnored() {
        assertFalse(controller.onNodeClicked(null));
        assertNull(selected.get());
        assertNull(locked.get());
    }

    @Test
    void lockedNodeBecomesSelectableAvterMovingAdjacent() {
        assertFalse(state.isSelectable("n3"));

        assertTrue(controller.onNodeClicked("n1"));
        assertTrue(state.isSelectable("n3"));
        assertTrue(controller.onNodeClicked("n3"));
        assertEquals("n3", state.getCurrentNodeId());
    }

    @Test
    void roomTypeIsExposedForFinalNode() {
        controller.onNodeClicked("n1");
        controller.onNodeClicked("n3");
        controller.onNodeClicked("boss");

        assertEquals(RoomType.FINAL, state.getNode("boss").getRoomType());
        assertEquals("boss", state.getCurrentNodeId());
    }
}
