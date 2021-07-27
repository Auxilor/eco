package com.willfp.eco.proxy;


import com.willfp.eco.core.proxy.AbstractProxy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ChatComponentProxy extends AbstractProxy {
    /**
     * Modify hover {@link org.bukkit.inventory.ItemStack}s using EnchantDisplay#displayEnchantments.
     *
     * @param object The NMS ChatComponent to modify.
     * @param player The player.
     * @return The modified ChatComponent.
     */
    Object modifyComponent(@NotNull Object object,
                           @NotNull Player player);
}
