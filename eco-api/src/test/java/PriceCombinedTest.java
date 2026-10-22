import com.willfp.eco.core.price.Price;
import com.willfp.eco.core.price.impl.PriceCombined;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PriceCombinedTest {
    private static final class FakePrice implements Price {
        private final String id;
        private final double value;
        private final boolean affordable;
        private double paid = 0;

        private FakePrice(final String id, final double value, final boolean affordable) {
            this.id = id;
            this.value = value;
            this.affordable = affordable;
        }

        @Override
        public boolean canAfford(final Player player, final double multiplier) {
            return affordable;
        }

        @Override
        public void pay(final Player player, final double multiplier) {
            paid += value * multiplier;
        }

        @Override
        public double getValue(final Player player, final double multiplier) {
            return value * multiplier;
        }

        @Override
        public String getIdentifier() {
            return id;
        }
    }

    @Test
    void paysEveryPrice() {
        FakePrice money = new FakePrice("money", 100, true);
        FakePrice levels = new FakePrice("levels", 5, true);
        PriceCombined combined = new PriceCombined(List.of(money, levels));

        combined.pay(null, 2);

        assertEquals(200, money.paid);
        assertEquals(10, levels.paid);
    }

    @Test
    void affordableOnlyWhenEveryPriceIs() {
        FakePrice money = new FakePrice("money", 100, true);
        FakePrice levels = new FakePrice("levels", 5, false);

        assertFalse(new PriceCombined(List.of(money, levels)).canAfford(null, 1));
        assertTrue(new PriceCombined(List.of(money)).canAfford(null, 1));
    }

    @Test
    void valueIsFirstPrice() {
        PriceCombined combined = new PriceCombined(List.of(
                new FakePrice("money", 100, true),
                new FakePrice("levels", 5, true)
        ));

        assertEquals(300, combined.getValue(null, 3));
        assertEquals("eco:combined:money,levels", combined.getIdentifier());
    }

    @Test
    void rejectsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new PriceCombined(List.of()));
    }
}
