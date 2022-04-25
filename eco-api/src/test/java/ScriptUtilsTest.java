import com.willfp.eco.util.ScriptUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScriptUtilsTest {
    @Test
    public void testScriptUtils() {
        Assertions.assertEquals(
                "Test",
                ScriptUtils.eval("\"Test\"")
        );
        Assertions.assertNull(ScriptUtils.eval("empasd___a !&&b1923"));
        Assertions.assertEquals(
                10,
                ScriptUtils.eval("8 + 2")
        );
        Assertions.assertEquals(
                "XVIII",
                ScriptUtils.eval("com.willfp.eco.util.NumberUtils.toNumeral(18)")
        );
    }

    @Test
    public void testBindings() {
        Assertions.assertEquals(
                10,
                ScriptUtils.eval("x + y", (bindings) -> {
                    bindings.put("x", 2);
                    bindings.put("y", 8);
                })
        );
        Assertions.assertEquals(
                12,
                ScriptUtils.eval("x + y", (bindings) -> {
                    bindings.put("x", 3);
                    bindings.put("y", 9);
                })
        );
    }
}
