import com.willfp.eco.core.integrations.hologram.Billboard;
import com.willfp.eco.core.integrations.hologram.HologramOptions;
import com.willfp.eco.core.integrations.hologram.TextAlignment;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HologramOptionsTest {
    @Test
    void defaultsAreSensible() {
        HologramOptions o = HologramOptions.defaults();
        assertEquals(Billboard.CENTER, o.getBillboard());
        assertEquals(TextAlignment.CENTER, o.getAlignment());
        assertEquals(1.0f, o.getScale(), 0.0001f);
        assertFalse(o.hasTextShadow());
        assertFalse(o.isSeeThrough());
        assertTrue(o.isVisibleByDefault());
        assertTrue(o.getContents().isEmpty());
    }

    @Test
    void builderSetsValuesAndContentsAreCopied() {
        List<String> lines = new java.util.ArrayList<>(List.of("a", "b"));
        HologramOptions o = HologramOptions.builder()
                .contents(lines)
                .billboard(Billboard.FIXED)
                .scale(2.5f)
                .textShadow(true)
                .visibleByDefault(false)
                .build();
        assertEquals(Billboard.FIXED, o.getBillboard());
        assertEquals(2.5f, o.getScale(), 0.0001f);
        assertTrue(o.hasTextShadow());
        assertFalse(o.isVisibleByDefault());
        assertEquals(List.of("a", "b"), o.getContents());

        // Mutating the source list must not affect the options.
        lines.add("c");
        assertEquals(2, o.getContents().size());
        // Returned list must be unmodifiable.
        assertThrows(UnsupportedOperationException.class, () -> o.getContents().add("x"));
    }
}
