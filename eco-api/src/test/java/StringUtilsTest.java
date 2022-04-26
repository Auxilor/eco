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

    @Test
    public void testProgressBar() {
        Assertions.assertEquals(
                "&a--------",
                StringUtils.createProgressBar(
                        '-',
                        8,
                        1,
                        "&a", "&e", "&c"
                )
        );

        Assertions.assertEquals(
                "&a-------&e-",
                StringUtils.createProgressBar(
                        '-',
                        8,
                        0.99,
                        "&a", "&e", "&c"
                )
        );

        Assertions.assertEquals(
                "&e-&c-------",
                StringUtils.createProgressBar(
                        '-',
                        8,
                        0.0001,
                        "&a", "&e", "&c"
                )
        );

        Assertions.assertEquals(
                "&a----&e-&c---",
                StringUtils.createProgressBar(
                        '-',
                        8,
                        0.5,
                        "&a", "&e", "&c"
                )
        );
    }
}
