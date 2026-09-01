package com.csse3200.game.components.spritedisplay.clickable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.csse3200.game.components.spritedisplay.clickable.ClickableRecord.ButtonType;
import org.junit.jupiter.api.Test;


class ClickableRecordTest {

    // --- compact constructor defaults (only exercised via the canonical constructor directly,
    // since Builder always supplies non-null variant/args) ---

    @Test
    void compactConstructor_defaultsVariantToClickableWhenNull() {
        ClickableRecord record =
                new ClickableRecord(null, null, 0, 0, null, "attackCard", null, -1, -1, null, null, null);

        assertEquals("Clickable", record.variant());
    }

    @Test
    void compactConstructor_defaultsArgsToEmptyArrayWhenNull() {
        ClickableRecord record =
                new ClickableRecord(null, null, 0, 0, null, "attackCard", null, -1, -1, "drag", null, null);

        assertEquals(0, record.args().length);
    }

    @Test
    void compactConstructor_defaultsLabelToTriggerWhenNull() {
        ClickableRecord record =
                new ClickableRecord(null, null, 0, 0, null, "attackCard", null, -1, -1, "drag", null, null);

        assertEquals("attackCard", record.label());
    }

    @Test
    void compactConstructor_keepsExplicitValuesInsteadOfDefaulting() {
        Object[] args = {1, "poison"};
        ClickableRecord record =
                new ClickableRecord(
                        "Attack", null, 0, 0, "default", "attackCard", null, -1, -1, "drag", args, "custom label");

        assertEquals("drag", record.variant());
        assertEquals(args, record.args());
        assertEquals("custom label", record.label());
    }

    // --- hasSize() ---

    @Test
    void hasSize_falseWhenBothDimensionsDefault() {
        ClickableRecord record = ClickableRecord.builder("attackCard").build();

        assertFalse(record.hasSize());
    }

    @Test
    void hasSize_trueWhenBothDimensionsSet() {
        ClickableRecord record = ClickableRecord.builder("attackCard").size(64, 32).build();

        assertTrue(record.hasSize());
    }

    @Test
    void hasSize_falseWhenOnlyWidthSet() {
        ClickableRecord record =
                new ClickableRecord("Attack", null, 0, 0, null, "attackCard", null, 64, -1, "drag", null, null);

        assertFalse(record.hasSize());
    }

    @Test
    void hasSize_falseWhenOnlyHeightSet() {
        ClickableRecord record =
                new ClickableRecord("Attack", null, 0, 0, null, "attackCard", null, -1, 32, "drag", null, null);

        assertFalse(record.hasSize());
    }

    // --- Builder defaults ---

    @Test
    void builder_appliesDefaultsWhenOnlyTriggerGiven() {
        ClickableRecord record = ClickableRecord.builder("attackCard").build();

        assertEquals("attackCard", record.trigger());
        assertNull(record.text());
        assertNull(record.btnSkin());
        assertNull(record.styleName());
        assertEquals("Clickable", record.variant());
        assertEquals("attackCard", record.label()); // defaults to trigger
        assertEquals(0, record.args().length);
        assertFalse(record.hasSize());
        assertEquals(ButtonType.IMAGE, record.type()); // no text -> IMAGE
    }

    @Test
    void builder_setsAllFieldsWhenProvided() {
        Skin skin = mock(Skin.class);

        ClickableRecord record =
                ClickableRecord.builder("attackCard")
                        .text("Attack")
                        .skin(skin)
                        .position(100.5f, 200.25f)
                        .styleName("default")
                        .size(64, 32)
                        .variant("drag")
                        .args(5, "poison", true)
                        .label("Attack the enemy")
                        .build();

        assertEquals("attackCard", record.trigger());
        assertEquals("Attack", record.text());
        assertEquals(skin, record.btnSkin());
        assertEquals(100.5f, record.x());
        assertEquals(200.25f, record.y());
        assertEquals("default", record.styleName());
        assertEquals("drag", record.variant());
        assertEquals("Attack the enemy", record.label());
        assertTrue(record.hasSize());
        assertEquals(64f, record.width());
        assertEquals(32f, record.height());
        assertArrayEquals(new Object[] {5, "poison", true}, record.args());
    }

    @Test
    void builder_argsCalledWithNothing_producesEmptyArrayNotNull() {
        ClickableRecord record = ClickableRecord.builder("attackCard").args().build();

        assertEquals(0, record.args().length);
    }

    // --- Builder.build() type inference ---

    @Test
    void inferType_noTextNoSkin_returnsImage() {
        ClickableRecord record = ClickableRecord.builder("attackCard").build();

        assertEquals(ButtonType.IMAGE, record.type());
    }

    @Test
    void inferType_noTextWithSkin_stillReturnsImage() {
        // text==null short-circuits before the skin check, regardless of skin being present.
        ClickableRecord record = ClickableRecord.builder("attackCard").skin(mock(Skin.class)).build();

        assertEquals(ButtonType.IMAGE, record.type());
    }

    @Test
    void inferType_textWithoutSkin_returnsText() {
        ClickableRecord record = ClickableRecord.builder("attackCard").text("Attack").build();

        assertEquals(ButtonType.TEXT, record.type());
    }

    @Test
    void inferType_textWithSkin_returnsImageText() {
        ClickableRecord record =
                ClickableRecord.builder("attackCard").text("Attack").skin(mock(Skin.class)).build();

        assertEquals(ButtonType.IMAGE_TEXT, record.type());
    }

    private static void assertArrayEquals(Object[] expected, Object[] actual) {
        org.junit.jupiter.api.Assertions.assertArrayEquals(expected, actual);
    }
}