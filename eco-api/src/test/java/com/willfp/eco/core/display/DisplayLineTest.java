package com.willfp.eco.core.display;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DisplayLineTest {
    @Test
    public void testPlainDisplayLine() {
        Assertions.assertTrue(
                DisplayLines.isDisplayLine(Component.text(Display.PREFIX + "§7Display line"))
        );
    }

    @Test
    public void testWrappedDisplayLine() {
        // Lore lines are wrapped in an empty parent to force italics off.
        Component line = Component.empty()
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(Display.PREFIX + "§7Display line"));

        Assertions.assertTrue(DisplayLines.isDisplayLine(line));
    }

    @Test
    public void testForeignLoreLineIsNotADisplayLine() {
        Assertions.assertFalse(
                DisplayLines.isDisplayLine(Component.text("§7Enchantment description"))
        );

        Assertions.assertFalse(
                DisplayLines.isDisplayLine(
                        Component.empty().append(Component.text("Enchantment description", NamedTextColor.GRAY))
                )
        );
    }

    @Test
    public void testEcoItemBaseLoreIsADisplayLine() {
        // The shape eco writes persisted display lore in: the prefix owns the coloured text
        // after it, wrapped to force italics off.
        Component line = Component.empty()
                .decoration(TextDecoration.ITALIC, false)
                .append(
                        Component.text(Display.PREFIX)
                                .append(Component.text("This cobblestone is enchanted", NamedTextColor.GRAY))
                );

        Assertions.assertTrue(DisplayLines.isDisplayLine(line));
    }

    @Test
    public void testPrefixBesideTheLineIsNotADisplayLine() {
        // The shape Bukkit's ItemMeta#setLore(List) writes a legacy string in, which is how
        // AdvancedEnchantments writes its enchantment descriptions: the prefix is a leaf
        // sitting beside the rest of the line rather than owning it.
        Component line = Component.empty()
                .append(Component.text(Display.PREFIX))
                .append(Component.text("* ", NamedTextColor.YELLOW))
                .append(Component.text("Chance to harvest in 3x3 area.", NamedTextColor.DARK_GRAY));

        Assertions.assertFalse(DisplayLines.isDisplayLine(line));
    }

    @Test
    public void testNonTextLineIsNotADisplayLine() {
        Assertions.assertFalse(DisplayLines.isDisplayLine(Component.translatable("item.eco.test")));
        Assertions.assertFalse(DisplayLines.isDisplayLine(Component.empty()));
    }
}
