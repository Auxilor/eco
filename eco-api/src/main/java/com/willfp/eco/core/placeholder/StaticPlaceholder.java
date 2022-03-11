package com.willfp.eco.core.placeholder;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A placeholder that cannot be registered, and exists purely in injection.
 */
public final class StaticPlaceholder implements Placeholder {
    /**
     * The name of the placeholder.
     */
    private final String identifier;

    /**
     * The function to retrieve the output of the placeholder.
     */
    private final Supplier<String> function;

    /**
     * Create a new player placeholder.
     *
     * @param identifier The identifier.
     * @param function   The function to retrieve the value.
     */
    public StaticPlaceholder(@NotNull final String identifier,
                             @NotNull final Supplier<String> function) {
        this.identifier = identifier;
        this.function = function;
    }

    /**
     * Get the value of the placeholder.
     *
     * @return The value.
     */
    public String getValue() {
        return function.get();
    }

    @Override
    public EcoPlugin getPlugin() {
        return Eco.getHandler().getEcoPlugin();
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StaticPlaceholder that)) {
            return false;
        }
        return Objects.equals(this.getIdentifier(), that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getIdentifier());
    }
}
