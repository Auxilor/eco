package com.willfp.eco.core.display;

import com.willfp.eco.util.StringUtils;
import java.util.List;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LegacyLoreMatcherTest {
    private static Component legacyRoundTrip(final Component line) {
        return StringUtils.toComponent(StringUtils.toLegacy(line));
    }

    @Test
    public void testForeignLineKeepsItsComponentThroughALegacyModule() {
        LoreNode fancy = LoreNode.foreign(Component.text("Fancy", NamedTextColor.GOLD).font(Key.key("custom", "icons")));
        List<Component> rendered = List.of(fancy.component());

        List<LoreNode> result = LegacyLoreMatcher.match(
                List.of(fancy),
                rendered,
                List.of(legacyRoundTrip(fancy.component()), StringUtils.toComponent(Display.PREFIX + "§7New"))
        );

        Assertions.assertSame(fancy, result.get(0));
        Assertions.assertTrue(result.get(1).display());
        Assertions.assertEquals("§7New", StringUtils.toLegacy(result.get(1).component()));
    }

    @Test
    public void testPrependedLinesBecomeDisplayNodesAndExistingNodesAreKept() {
        LoreNode existing = LoreNode.display(Component.text("Existing", NamedTextColor.GRAY));
        List<Component> rendered = List.of(DisplayLines.mark(existing.component(), true));

        List<LoreNode> result = LegacyLoreMatcher.match(
                List.of(existing),
                rendered,
                List.of(StringUtils.toComponent(Display.PREFIX + "§aPrepended"), legacyRoundTrip(rendered.get(0)))
        );

        Assertions.assertTrue(result.get(0).display());
        Assertions.assertSame(existing, result.get(1));
    }

    @Test
    public void testForeignLineWithThePrefixStaysForeign() {
        LoreNode advancedEnchantments = LoreNode.foreign(
                Component.empty()
                        .append(Component.text(Display.PREFIX))
                        .append(Component.text("Enchant description", NamedTextColor.GRAY))
        );
        List<Component> rendered = List.of(advancedEnchantments.component());

        List<LoreNode> result = LegacyLoreMatcher.match(
                List.of(advancedEnchantments),
                rendered,
                List.of(legacyRoundTrip(rendered.get(0)))
        );

        Assertions.assertSame(advancedEnchantments, result.get(0));
        Assertions.assertFalse(result.get(0).display());
    }

    @Test
    public void testRewrittenForeignLineIsKeptInItsNewForm() {
        LoreNode foreign = LoreNode.foreign(Component.text("Old"));
        Component rewritten = Component.text("New");

        List<LoreNode> result = LegacyLoreMatcher.match(List.of(foreign), List.of(foreign.component()), List.of(rewritten));

        Assertions.assertEquals(List.of(LoreNode.foreign(rewritten)), result);
    }

    @Test
    public void testRemovedLinesAreDropped() {
        LoreNode kept = LoreNode.foreign(Component.text("Kept"));
        LoreNode removed = LoreNode.foreign(Component.text("Removed"));

        List<LoreNode> result = LegacyLoreMatcher.match(
                List.of(kept, removed),
                List.of(kept.component(), removed.component()),
                List.of(legacyRoundTrip(kept.component()))
        );

        Assertions.assertEquals(List.of(kept), result);
    }
}
