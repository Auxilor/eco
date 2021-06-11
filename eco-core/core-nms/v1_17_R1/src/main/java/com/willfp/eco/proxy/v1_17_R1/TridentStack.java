package com.willfp.eco.proxy.v1_17_R1;

import com.willfp.eco.proxy.proxies.TridentStackProxy;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class TridentStack implements TridentStackProxy {
    @Override
    public ItemStack getTridentStack(@NotNull final Trident trident) {
        return trident.getItem();
    }
}
