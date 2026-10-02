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
    public void testLegacyColorResetsMiniMessageTags() {
        Assertions.assertEquals("<bold><reset><gray>Text</gray>", LegacyToMiniMessage.convert("<bold>§7Text"));
    }

    @Test
    public void testClosingTagResetByLegacyCodeIsDropped() {
        Assertions.assertEquals("<bold><reset><red>A</red>B", LegacyToMiniMessage.convert("<bold>§cA</bold>B"));
    }

    @Test
    public void testLegacyColorKeepsFontAndHoverTags() {
        Assertions.assertEquals(
                "<font:uniform><hover:show_text:'Tip'><bold><reset><font:uniform><hover:show_text:'Tip'><gray>A</gray></hover>B</font>",
                LegacyToMiniMessage.convert("<font:uniform><hover:show_text:'Tip'><bold>§7A</hover>B</font>")
        );
    }

    @Test
    public void testLegacyResetKeepsFontTag() {
        Assertions.assertEquals(
                "<font:uniform><red><reset><font:uniform>A</font>",
                LegacyToMiniMessage.convert("<font:uniform><red>§rA</font>")
        );
    }

    @Test
    public void testMiniMessageColorClosesLegacyDecorations() {
        Assertions.assertEquals("<bold></bold><red>X", LegacyToMiniMessage.convert("§l<red>X"));
    }

    @Test
    public void testQuotedArgumentsAreConvertedOnTheirOwn() {
        Assertions.assertEquals(
                "<hover:show_text:'<red>Hi</red>'>Hover</hover>",
                LegacyToMiniMessage.convert("<hover:show_text:'§cHi'>Hover</hover>")
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
