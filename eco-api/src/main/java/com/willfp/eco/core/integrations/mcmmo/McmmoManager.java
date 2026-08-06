package com.willfp.eco.core.integrations.mcmmo;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle mcmmo integrations.
 * <p>
 * If no {@link McmmoIntegration} is registered, the bonus drop count is zero and no event is
 * considered fake.
 */
public final class McmmoManager {
    /**
     * A set of all registered integrations.
     */
    private static final IntegrationRegistry<McmmoIntegration> REGISTERED = new IntegrationRegistry<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final McmmoIntegration integration) {
        REGISTERED.register(integration);
    }

    /**
     * Get bonus drop count from block.
     * <p>
     * Unlike the other methods here, this iterates the registry directly rather than safely,
     * summing the bonus drop count reported by every registered integration.
     *
     * @param block The block.
     * @return The total bonus drop count.
     */
    public static int getBonusDropCount(@NotNull final Block block) {
        int finalValue = 0;

        for (McmmoIntegration mcmmoIntegration : REGISTERED) {
            finalValue += mcmmoIntegration.getBonusDropCount(block);
        }

        return finalValue;
    }

    /**
     * Get if event is fake.
     *
     * @param event The event to check.
     * @return If the event is fake.
     */
    public static boolean isFake(@NotNull final Event event) {
        return REGISTERED.anySafely(integration -> integration.isFake(event));
    }

    private McmmoManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
