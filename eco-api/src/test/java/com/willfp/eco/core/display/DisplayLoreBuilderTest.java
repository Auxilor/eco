package com.willfp.eco.core.display;

import com.willfp.eco.util.StringUtils;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DisplayLoreBuilderTest {
    private static List<String> legacy(final List<Component> lore) {
        return lore.stream().map(StringUtils::toLegacy).toList();
    }

    @Test
    public void testPrependAndAppendKeepModuleOrder() {
        DisplayLoreBuilder lore = DisplayLoreBuilder.ofForeign(List.of(Component.text("Foreign")));

        lore.prepend(List.of(Component.text("First prepend")));
        lore.append(List.of(Component.text("Append")));
        lore.prepend(List.of(Component.text("Second prepend")));

        Assertions.assertEquals(
                List.of("Second prepend", "First prepend", "Foreign", "Append"),
                legacy(lore.getNodes().stream().map(LoreNode::component).toList())
        );
        Assertions.assertArrayEquals(new int[]{0, 1, 3}, lore.getDisplayIndices());
    }

    @Test
    public void testForeignLinesAreTheSameObjects() {
        Component foreign = Component.text("Foreign");
        DisplayLoreBuilder lore = DisplayLoreBuilder.ofForeign(List.of(foreign));

        lore.prepend(List.of(Component.text("Eco")));

        Assertions.assertSame(foreign, lore.getForeignLines().get(0));
        Assertions.assertSame(foreign, lore.render(true).get(1));
    }

    private static Supplier<List<Component>> counting(final AtomicInteger reads,
                                                      final Component... lore) {
        return () -> {
            reads.incrementAndGet();
            return List.of(lore);
        };
    }

    @Test
    public void testUnreadLoreStaysUnread() {
        AtomicInteger reads = new AtomicInteger();
        DisplayLoreBuilder lore = DisplayLoreBuilder.ofForeign(counting(reads, Component.text("Foreign")));

        lore.append(List.of());
        lore.prepend(List.of());
        lore.removeStaleLines();

        Assertions.assertArrayEquals(new int[0], lore.getDisplayIndices());
        Assertions.assertTrue(lore.isSynced());
        Assertions.assertEquals(0, reads.get());
    }

    @Test
    public void testUnreadLoreIsReadOnce() {
        AtomicInteger reads = new AtomicInteger();
        Component foreign = Component.text("Foreign");
        DisplayLoreBuilder lore = DisplayLoreBuilder.ofForeign(counting(reads, foreign));

        Assertions.assertSame(foreign, lore.getForeignLines().get(0));
        lore.append(List.of(Component.text("Eco")));
        lore.prepend(List.of(Component.text("Eco")));
        lore.removeStaleLines();

        Assertions.assertSame(foreign, lore.render(false).get(1));
        Assertions.assertArrayEquals(new int[]{0, 2}, lore.getDisplayIndices());
        Assertions.assertFalse(lore.isSynced());
        Assertions.assertEquals(1, reads.get());
    }

    @Test
    public void testUnreadLoreKeepsLineOrderWhenAdded() {
        DisplayLoreBuilder lore = DisplayLoreBuilder.ofForeign(counting(new AtomicInteger(), Component.text("Foreign")));

        lore.prepend(List.of(Component.text("Before")));
        lore.append(List.of(Component.text("After")));

        Assertions.assertEquals(
                List.of("Before", "Foreign", "After"),
                legacy(lore.getNodes().stream().map(LoreNode::component).toList())
        );
    }

    @Test
    public void testStaleLinesAreStillRemovedFromUnreadLore() {
        DisplayLoreBuilder lore = DisplayLoreBuilder.ofForeign(counting(new AtomicInteger(), Component.text(Display.PREFIX + "Eco")));

        lore.append(List.of(Component.text("Eco")));
        lore.removeStaleLines();

        Assertions.assertArrayEquals(new int[]{0}, lore.getDisplayIndices());
        Assertions.assertEquals(1, lore.getNodes().size());
    }

    @Test
    public void testRenderMarksDisplayLines() {
        DisplayLoreBuilder lore = DisplayLoreBuilder.ofForeign(List.of());

        lore.append(List.of(Component.text("Eco")));

        Assertions.assertEquals(List.of(Display.PREFIX + "Eco"), legacy(lore.render(true)));
        Assertions.assertEquals(List.of("Eco"), legacy(lore.render(false)));
        Assertions.assertTrue(DisplayLines.isDisplayLine(lore.render(false).get(0)));
    }
}
