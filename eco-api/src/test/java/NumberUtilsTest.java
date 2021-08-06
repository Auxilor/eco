import com.willfp.eco.util.NumberUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumberUtilsTest {
    @Test
    public void testFormatDouble() {
        Assertions.assertEquals(NumberUtils.format(3.0D), "3");
        Assertions.assertEquals(NumberUtils.format(3.2D), "3.20");
    }

    @Test
    public void testLog2() {
        Assertions.assertEquals(NumberUtils.log2(2), 1);
    }

    @Test
    public void testNumerals() {
        Assertions.assertEquals(NumberUtils.fromNumeral("IV"), 4);
        Assertions.assertEquals(NumberUtils.fromNumeral("IX"), 9);
        Assertions.assertEquals(NumberUtils.toNumeral(14), "XIV");
        Assertions.assertEquals(NumberUtils.toNumeral(21), "XXI");
    }
}
