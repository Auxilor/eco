package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Placeholders that can be injected into {@link PlaceholderInjectable} objects.
 */
public sealed interface InjectablePlaceholder extends Placeholder permits PlayerStaticPlaceholder, StaticPlaceholder {
    @Override
    default @NotNull EcoPlugin getPlugin() {
        return Eco.get().getEcoPlugin();
    }
}
