import com.willfp.eco.util.NumberUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumberUtilsTest {
    @Test
    public void testFormatDouble() {
        Assertions.assertEquals("3", NumberUtils.format(3.0D));
        //Assertions.assertEquals("3.20", NumberUtils.format(3.2D));
    }

    @Test
    public void testLog2() {
        Assertions.assertEquals(1, NumberUtils.log2(2));
    }

    @Test
    public void testNumerals() {
        Assertions.assertEquals(4, NumberUtils.fromNumeral("IV"));
        Assertions.assertEquals(9, NumberUtils.fromNumeral("IX"));
        Assertions.assertEquals("XIV", NumberUtils.toNumeral(14));
        Assertions.assertEquals("XXI", NumberUtils.toNumeral(21));
    }
}
