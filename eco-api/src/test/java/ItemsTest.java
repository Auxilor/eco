import com.willfp.eco.util.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ItemsTest {
    @Test
    public void testParserLookupStrings() {
        Assertions.assertArrayEquals(
                StringUtils.parseTokens("diamond_sword name:\"test name\""),
                new String[]{"diamond_sword", "name:test name"}
        );
        Assertions.assertArrayEquals(
                StringUtils.parseTokens("stick 1 name:\"The \\\"Holy\\\" Stick\""),
                new String[]{"stick", "1", "name:The \"Holy\" Stick"}
        );
    }
}
