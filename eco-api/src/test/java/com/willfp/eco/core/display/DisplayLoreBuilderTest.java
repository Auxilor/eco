package com.willfp.eco.core.display;

import com.willfp.eco.util.StringUtils;
import java.util.List;
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

    @Test
    public void testRenderMarksDisplayLines() {
        DisplayLoreBuilder lore = DisplayLoreBuilder.ofForeign(List.of());

        lore.append(List.of(Component.text("Eco")));

        Assertions.assertEquals(List.of(Display.PREFIX + "Eco"), legacy(lore.render(true)));
        Assertions.assertEquals(List.of("Eco"), legacy(lore.render(false)));
        Assertions.assertTrue(DisplayLines.isDisplayLine(lore.render(false).get(0)));
    }
}
