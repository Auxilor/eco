package com.willfp.eco.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LegacyToMiniMessageTest {
    @Test
    public void testColorCodes() {
        Assertions.assertEquals("<gray>Hello</gray>", LegacyToMiniMessage.convert("§7Hello"));
    }

    @Test
    public void testColorResetsDecorations() {
        Assertions.assertEquals(
                "<gray><bold>Bold</bold></gray><green>Plain</green>",
                LegacyToMiniMessage.convert("§7§lBold§aPlain")
        );
    }

    @Test
    public void testHexCodes() {
        Assertions.assertEquals(
                "<color:#ff8800>Orange</color>",
                LegacyToMiniMessage.convert("§x§f§f§8§8§0§0Orange")
        );
    }

    @Test
    public void testResetClosesEverything() {
        Assertions.assertEquals("<red><italic>A</italic></red>B", LegacyToMiniMessage.convert("§c§oA§rB"));
    }

    @Test
    public void testMiniMessageTagsArePassedThrough() {
        Assertions.assertEquals(
                "<sprite:items:item/diamond> <gray>Gem</gray>",
                LegacyToMiniMessage.convert("<sprite:items:item/diamond> §7Gem")
        );
    }

    @Test
    public void testUnknownCodesAreKept() {
        Assertions.assertEquals("§zText", LegacyToMiniMessage.convert("§zText"));
    }

    @Test
    public void testObjectTagsAreStripped() {
        Assertions.assertEquals(
                " Gem ",
                LegacyToMiniMessage.stripObjectTags("<sprite:items:item/diamond> Gem <head:Notch>")
        );
    }
}
