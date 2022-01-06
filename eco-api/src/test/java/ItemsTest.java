import com.willfp.eco.core.items.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ItemsTest {
    @Test
    public void testParserLookupStrings() {
        Assertions.assertArrayEquals(
                Items.parseLookupString("diamond_sword name:\"test name\""),
                new String[]{"diamond_sword", "name:test name"}
        );
        Assertions.assertArrayEquals(
                Items.parseLookupString("stick 1 name:\"The \\\"Holy\\\" Stick\""),
                new String[]{"stick", "1", "name:The \"Holy\" Stick"}
        );
    }
}
