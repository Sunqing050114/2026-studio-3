package com.csse3200.game.maps;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.extensions.GameExtension;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/** Tests for MapSelectionController using a small hand-built MapGraph. */
@ExtendWith(GameExtension.class)
class MapSelectionControllerTest {

  private MapGraph mapGraph;
  private MapSelectionController controller;
  private AtomicReference<Integer> selected;
  private AtomicReference<Integer> locked;
  private AtomicReference<Integer> completed;

  /**
   * Creates a node with the given id, room type and state.
   *
   * @param id node identifier
   * @param roomType room type
   * @param nodeState initial node state
   * @return the created node
   */
  private MapNode node(Integer id, RoomType roomType, NodeState nodeState) {
    MapNode n = new MapNode(id, roomType);
    n.setState(nodeState);
    return n;
  }

  @BeforeEach
  void setUp() {
    mapGraph = new MapGraph();

    // 0 (CURRENT) -- 1 (AVAILABLE) -- 3 (LOCKED) -- 4 (LOCKED)
    //             \- 2 (AVAILABLE) -/
    mapGraph.addNode(node(0, RoomType.COMBAT, NodeState.CURRENT));
    mapGraph.addNode(node(1, RoomType.COMBAT, NodeState.AVAILABLE));
    mapGraph.addNode(node(2, RoomType.SHOP, NodeState.AVAILABLE));
    mapGraph.addNode(node(3, RoomType.COMBAT, NodeState.LOCKED));
    mapGraph.addNode(node(4, RoomType.FINAL, NodeState.LOCKED));

    mapGraph.connectNodes(0, 1);
    mapGraph.connectNodes(0, 2);
    mapGraph.connectNodes(1, 3);
    mapGraph.connectNodes(2, 3);
    mapGraph.connectNodes(3, 4);

    controller = new MapSelectionController(mapGraph);

    selected = new AtomicReference<>();
    locked = new AtomicReference<>();
    completed = new AtomicReference<>();
    controller.getEvents().addListener("nodeSelected", (Integer id) -> selected.set(id));
    controller.getEvents().addListener("nodeLocked", (Integer id) -> locked.set(id));
    controller.getEvents().addListener("nodeCompleted", (Integer id) -> completed.set(id));
  }

  @Test
  void lockedNodeSendsNoMoveAndFiresLocked() {
    boolean accepted = controller.onNodeClicked(3); // LOCKED at start

    assertFalse(accepted);
    assertEquals(3, locked.get());
    assertNull(selected.get());
    assertEquals(NodeState.LOCKED, mapGraph.getNode(3).getState(), "player should not have moved");
  }

  @Test
  void unknownNodeFiresLockedAndDoesNotMove() {
    boolean accepted = controller.onNodeClicked(999);

    assertFalse(accepted);
    assertEquals(999, locked.get());
    assertNull(selected.get());
  }

  @Test
  void nullClickIsIgnored() {
    assertFalse(controller.onNodeClicked(null));
    assertNull(selected.get());
    assertNull(locked.get());
  }

  @Test
  void completedNodeFiresCompletedNotLocked() {
    mapGraph.getNode(1).setState(NodeState.COMPLETED);

    boolean accepted = controller.onNodeClicked(1);

    assertFalse(accepted);
    assertEquals(1, completed.get());
    assertNull(locked.get());
    assertNull(selected.get());
  }

  @Test
  void roomTypeIsExposedForBossNode() {
    assertEquals(RoomType.FINAL, mapGraph.getNode(4).getRoomType());
  }

  // Blocked until MapGraph can seed a starting CURRENT node:
  // - selectableNodeCommitsMoveAndFiresSelected
  // - lockedNodeBecomesSelectableAfterEncounterCompletes
}
