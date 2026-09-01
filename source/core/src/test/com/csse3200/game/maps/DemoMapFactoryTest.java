package com.csse3200.game.maps;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.extensions.GameExtension;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
public class DemoMapFactoryTest {

  @Test
  void layersRunFromStartToFinalNode() {
    List<List<Integer>> layers = DemoMapFactory.getLayers();

    assertEquals(List.of(DemoMapFactory.getStartNodeId()), layers.get(0));
    assertEquals(1, layers.get(layers.size() - 1).size());
  }

  @Test
  void hasExactlyOneFinalNode() {
    MapGraph graph = DemoMapFactory.create(1234L);

    long finalNodes =
        graph.getNodes().values().stream().filter(n -> n.getRoomType() == RoomType.FINAL).count();

    assertEquals(1, finalNodes);
  }

  @Test
  void finalNodeIsReachableFromTheStart() {
    MapGraph graph = DemoMapFactory.create(1234L);
    List<List<Integer>> layers = DemoMapFactory.getLayers();
    Integer finalId = layers.get(layers.size() - 1).get(0);

    assertTrue(reachable(graph, DemoMapFactory.getStartNodeId()).contains(finalId));
  }

  /** Connections only point forwards, otherwise the player can walk back to a room they skipped. */
  @Test
  void connectionsOnlyPointForwards() {
    MapGraph graph = DemoMapFactory.create(1234L);

    for (MapNode node : graph.getNodes().values()) {
      for (MapNode connected : node.getConnections()) {
        assertFalse(
            connected.getConnections().contains(node),
            "Node " + connected.getNodeId() + " links back to " + node.getNodeId());
      }
    }
  }

  @Test
  void sameSeedGivesSameRoomTypes() {
    MapGraph first = DemoMapFactory.create(99L);
    MapGraph second = DemoMapFactory.create(99L);

    for (Integer id : first.getNodes().keySet()) {
      assertEquals(first.getNode(id).getRoomType(), second.getNode(id).getRoomType());
    }
  }

  /** Node ids reachable from the given start, following connections forwards. */
  private Set<Integer> reachable(MapGraph graph, Integer startId) {
    Set<Integer> seen = new HashSet<>();
    Deque<MapNode> queue = new ArrayDeque<>(new ArrayList<>(List.of(graph.getNode(startId))));

    while (!queue.isEmpty()) {
      MapNode node = queue.pop();

      if (!seen.add(node.getNodeId())) {
        continue;
      }

      queue.addAll(node.getConnections());
    }

    return seen;
  }
}
