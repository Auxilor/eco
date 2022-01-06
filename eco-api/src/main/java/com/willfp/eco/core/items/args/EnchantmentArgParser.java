package com.willfp.eco.core.items.args;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Parses enchantment arguments.
 *
 * @deprecated Moved to internals.
 */
@Deprecated(since = "6.16.0", forRemoval = true)
public class EnchantmentArgParser implements LookupArgParser {
    /**
     * Instantiate arg parser.
     */
    public EnchantmentArgParser() {
        Bukkit.getLogger().severe("Instantiation of class marked for removal! (" + this.getClass().getName() + "), this will throw an error in a future release!");
    }

    @Override
    public @Nullable Predicate<ItemStack> parseArguments(@NotNull final String[] args,
                                                         @NotNull final ItemMeta meta) {
        return null;
    }

    static {
        Bukkit.getLogger().severe("Referencing a class marked for removal! (" + EnchantmentArgParser.class.getName() + "), this will throw an error in a future release!");
    }
}
