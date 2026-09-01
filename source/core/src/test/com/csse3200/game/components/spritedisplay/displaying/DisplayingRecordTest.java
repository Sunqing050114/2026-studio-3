package com.csse3200.game.components.spritedisplay.displaying;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.junit.jupiter.api.Test;


class DisplayingRecordTest {

    // --- compact constructor default ---

    @Test
    void compactConstructor_defaultsVariantToDisplayingWhenNull() {
        DisplayingRecord record =
                new DisplayingRecord("Health: 10", null, null, null, null, 0, 0, -1, -1, 1f, null);

        assertEquals("Displaying", record.variant());
    }

    @Test
    void compactConstructor_keepsExplicitVariantInsteadOfDefaulting() {
        DisplayingRecord record =
                new DisplayingRecord("Health: 10", null, null, null, null, 0, 0, -1, -1, 1f, "floating");

        assertEquals("floating", record.variant());
    }

    // --- hasSize() ---

    @Test
    void hasSize_falseWhenBothDimensionsDefault() {
        DisplayingRecord record = DisplayingRecord.builder("Health: 10").build();

        assertFalse(record.hasSize());
    }

    @Test
    void hasSize_trueWhenBothDimensionsSet() {
        DisplayingRecord record = DisplayingRecord.builder("Health: 10").size(64, 32).build();

        assertTrue(record.hasSize());
    }

    @Test
    void hasSize_falseWhenOnlyWidthSet() {
        DisplayingRecord record =
                new DisplayingRecord("Health: 10", null, null, null, null, 0, 0, 64, -1, 1f, null);

        assertFalse(record.hasSize());
    }

    @Test
    void hasSize_falseWhenOnlyHeightSet() {
        DisplayingRecord record =
                new DisplayingRecord("Health: 10", null, null, null, null, 0, 0, -1, 32, 1f, null);

        assertFalse(record.hasSize());
    }

    // --- Builder defaults ---

    @Test
    void builder_appliesDefaultsWhenOnlyTextGiven() {
        DisplayingRecord record = DisplayingRecord.builder("Health: 10").build();

        assertEquals("Health: 10", record.text());
        assertNull(record.trigger());
        assertNull(record.skin());
        assertNull(record.fontName());
        assertNull(record.colour());
        assertEquals(1f, record.scale()); // NO_SCALE default
        assertEquals("Displaying", record.variant());
        assertFalse(record.hasSize());
    }

    @Test
    void builder_setsAllFieldsWhenProvided() {
        Skin skin = mock(Skin.class);

        DisplayingRecord record =
                DisplayingRecord.builder("Health: 10")
                        .trigger("healthChanged")
                        .skin(skin)
                        .fontName("pixel-32")
                        .colour("#FF0000")
                        .position(10f, 20f)
                        .size(64, 32)
                        .scale(1.5f)
                        .variant("floating")
                        .build();

        assertEquals("Health: 10", record.text());
        assertEquals("healthChanged", record.trigger());
        assertEquals(skin, record.skin());
        assertEquals("pixel-32", record.fontName());
        assertEquals("#FF0000", record.colour());
        assertEquals(10f, record.x());
        assertEquals(20f, record.y());
        assertTrue(record.hasSize());
        assertEquals(64f, record.width());
        assertEquals(32f, record.height());
        assertEquals(1.5f, record.scale());
        assertEquals("floating", record.variant());
    }

    @Test
    void builder_acceptsNonStringCharSequenceForText() {
        CharSequence text = new StringBuilder("Health: ").append(10);

        DisplayingRecord record = DisplayingRecord.builder(text).build();

        assertEquals("Health: 10", record.text().toString());
    }
}