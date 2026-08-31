package com.csse3200.game.components.battle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.ServiceLocator;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

@ExtendWith(GameExtension.class)
class BattleDisplayTest {
  private Entity entity;
  private Table root;

  @BeforeEach
  void setUp() {
    Stage stage = mock(Stage.class);
    RenderService renderService = mock(RenderService.class);
    when(renderService.getStage()).thenReturn(stage);
    ServiceLocator.registerRenderService(renderService);

    entity = new Entity().addComponent(new BattleDisplay());
    entity.create();

    ArgumentCaptor<Actor> actorCaptor = ArgumentCaptor.forClass(Actor.class);
    verify(stage).addActor(actorCaptor.capture());
    root = (Table) actorCaptor.getValue();
  }

  @Test
  void shouldPublishDefendSelectionWhenDefendButtonChanges() {
    AtomicInteger selections = listenFor("defendCardSelected");

    findButton("Defend Card").fire(new ChangeEvent());

    assertEquals(1, selections.get());
  }

  @Test
  void shouldPublishEndTurnSelectionWhenEndTurnButtonChanges() {
    AtomicInteger selections = listenFor("endTurnSelected");

    findButton("End Turn").fire(new ChangeEvent());

    assertEquals(1, selections.get());
  }

  private AtomicInteger listenFor(String eventName) {
    AtomicInteger events = new AtomicInteger();
    entity.getEvents().addListener(eventName, events::incrementAndGet);
    return events;
  }

  private TextButton findButton(String text) {
    for (Actor actor : root.getChildren()) {
      if (actor instanceof TextButton button && text.contentEquals(button.getText())) {
        return button;
      }
    }
    assertNotNull(null, "Could not find button: " + text);
    return null;
  }
}
