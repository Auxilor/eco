import com.willfp.eco.core.placeholder.context.PlaceholderContext;
import com.willfp.eco.core.placeholder.context.PlaceholderContextSupplier;
import com.willfp.eco.core.price.Price;
import com.willfp.eco.core.price.PriceFactory;
import com.willfp.eco.core.price.Prices;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PricesTest {

    /** Sentinel price carrying the tag of the factory that produced it. All Price methods are default. */
    private static final class TaggedPrice implements Price {
        private final int tag;

        private TaggedPrice(final int tag) {
            this.tag = tag;
        }
    }

    private static final class TestFactory implements PriceFactory {
        private final String name;
        private final int tag;

        private TestFactory(final String name, final int tag) {
            this.name = name;
            this.tag = tag;
        }

        @Override
        public List<String> getNames() {
            return List.of(name);
        }

        @Override
        public Price create(final PlaceholderContext baseContext,
                            final PlaceholderContextSupplier<Double> function) {
            return new TaggedPrice(tag);
        }
    }

    @Test
    void unregisterAllowsReregistrationWithNewInstance() {
        TestFactory first = new TestFactory("reloadcur", 1);
        Prices.registerPriceFactory(first);

        // Simulate reload: drop the old factory, register a brand-new instance under the same name.
        Prices.unregisterPriceFactory(first);
        TestFactory second = new TestFactory("reloadcur", 2);

        assertDoesNotThrow(() -> Prices.registerPriceFactory(second));

        Price created = Prices.create("0", "reloadcur");
        assertInstanceOf(TaggedPrice.class, created);
        assertEquals(2, ((TaggedPrice) created).tag);
    }

    @Test
    void unregisterByInstanceIsNoOpWhenNameOwnedByDifferentFactory() {
        TestFactory owner = new TestFactory("sharedname", 1);
        Prices.registerPriceFactory(owner);

        // A different instance must NOT be able to evict the real owner.
        TestFactory other = new TestFactory("sharedname", 2);
        Prices.unregisterPriceFactory(other);

        Price created = Prices.create("0", "sharedname");
        assertInstanceOf(TaggedPrice.class, created);
        assertEquals(1, ((TaggedPrice) created).tag);
    }

    @Test
    void defaultFactoryIsOverriddenByLaterRegistration() {
        TestFactory builtIn = new TestFactory("defaultcur", 1);
        Prices.registerDefaultPriceFactory(builtIn);

        TestFactory fromPlugin = new TestFactory("defaultcur", 2);
        assertDoesNotThrow(() -> Prices.registerPriceFactory(fromPlugin));

        Price created = Prices.create("0", "defaultcur");
        assertInstanceOf(TaggedPrice.class, created);
        assertEquals(2, ((TaggedPrice) created).tag);
    }

    @Test
    void nameTakenOverFromDefaultIsNoLongerOverridable() {
        Prices.registerDefaultPriceFactory(new TestFactory("takenovercur", 1));
        Prices.registerPriceFactory(new TestFactory("takenovercur", 2));

        assertThrows(
                IllegalStateException.class,
                () -> Prices.registerPriceFactory(new TestFactory("takenovercur", 3))
        );
    }

    @Test
    void defaultDoesNotOverrideExistingFactory() {
        Prices.registerPriceFactory(new TestFactory("claimedcur", 1));
        Prices.registerDefaultPriceFactory(new TestFactory("claimedcur", 2));

        Price created = Prices.create("0", "claimedcur");
        assertInstanceOf(TaggedPrice.class, created);
        assertEquals(1, ((TaggedPrice) created).tag);
    }

    @Test
    void clashingFactoryStillThrows() {
        Prices.registerPriceFactory(new TestFactory("clashcur", 1));

        assertThrows(
                IllegalStateException.class,
                () -> Prices.registerPriceFactory(new TestFactory("clashcur", 2))
        );
    }
}
