import com.willfp.eco.util.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {
    @Test
    public void testSplitAround() {
        Assertions.assertArrayEquals(
                new String[]{"one", "two"},
                StringUtils.splitAround("one ? two", "?")
        );
        Assertions.assertArrayEquals(
                new String[]{"one? two"},
                StringUtils.splitAround("one? two", "?")
        );
        Assertions.assertArrayEquals(
                new String[]{"one", "two", "three"},
                StringUtils.splitAround("one ? two ? three", "?")
        );
        Assertions.assertArrayEquals(
                new String[]{"one", "two"},
                StringUtils.splitAround("one || two", "||")
        );
        Assertions.assertArrayEquals(
                new String[]{"one|| two"},
                StringUtils.splitAround("one|| two", "||")
        );
    }
}
