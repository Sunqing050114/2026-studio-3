package com.csse3200.game.components.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BattleControllerTest {
  private BattleController controller;
  private Method handleMethod;

  @BeforeEach
  void setUp() throws ReflectiveOperationException {
    Constructor<BattleController> constructor = BattleController.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    controller = constructor.newInstance();

    handleMethod = BattleController.class.getDeclaredMethod("handle", BattleEvent.class);
    handleMethod.setAccessible(true);
  }

  @Test
  void shouldStartInSetupWithNoCurrentEnemy() {
    assertEquals(BattlePhase.SETUP, controller.getCurrentPhase());
    assertEquals(-1, controller.getCurrentEnemyIndex());
  }

  @Test
  void shouldApplyValidTransitions() {
    handle(BattleEvent.SETUP_COMPLETE);
    assertEquals(BattlePhase.REVEAL_INTENTS, controller.getCurrentPhase());

    handle(BattleEvent.INTENTS_REVEALED);
    assertEquals(BattlePhase.PLAYER_START, controller.getCurrentPhase());

    handle(BattleEvent.PLAYER_TURN_STARTED);
    assertEquals(BattlePhase.PLAYER_TURN, controller.getCurrentPhase());

    handle(BattleEvent.PLAYER_ATTACK_SELECTED);
    assertEquals(BattlePhase.PLAYER_ATTACK, controller.getCurrentPhase());
  }

  @Test
  void shouldRejectInvalidTransitionWithoutChangingPhase() {
    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> handle(BattleEvent.PLAYER_ATTACK_SELECTED));

    assertEquals(
        "Invalid battle transition: SETUP-->PLAYER_ATTACK_SELECTED", exception.getMessage());
    assertEquals(BattlePhase.SETUP, controller.getCurrentPhase());
  }

  private void handle(BattleEvent event) {
    try {
      handleMethod.invoke(controller, event);
    } catch (InvocationTargetException exception) {
      Throwable cause = exception.getCause();
      if (cause instanceof RuntimeException runtimeException) {
        throw runtimeException;
      }
      throw new RuntimeException(cause);
    } catch (ReflectiveOperationException exception) {
      throw new RuntimeException(exception);
    }
  }
}
