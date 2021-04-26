package com.willfp.eco.spigot.display;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.willfp.eco.core.AbstractPacketAdapter;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.proxy.proxies.VillagerTradeProxy;
import com.willfp.eco.spigot.InternalProxyUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PacketOpenWindowMerchant extends AbstractPacketAdapter {
    /**
     * Instantiate a new listener for {@link PacketType.Play.Server#OPEN_WINDOW_MERCHANT}.
     *
     * @param plugin The plugin to listen through.
     */
    public PacketOpenWindowMerchant(@NotNull final EcoPlugin plugin) {
        super(plugin, PacketType.Play.Server.OPEN_WINDOW_MERCHANT, ListenerPriority.MONITOR, true);
    }

    @Override
    public void onSend(@NotNull final PacketContainer packet,
                       @NotNull final Player player,
                       @NotNull final PacketEvent event) {
        List<MerchantRecipe> recipes = new ArrayList<>();

        if (((EcoPlugin) this.getPlugin()).getConfigYml().getBool("villager-display-fix")) {
            for (MerchantRecipe recipe : packet.getMerchantRecipeLists().read(0)) {
                ItemStack result = recipe.getResult();
                ItemMeta meta = result.getItemMeta();
                if (meta != null) {
                    meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
                    meta.getPersistentDataContainer().remove(NamespacedKey.fromString("ecoenchants:ecoenchantlore-skip"));
                    result.setItemMeta(meta);
                }
            }
        }

        for (MerchantRecipe recipe : packet.getMerchantRecipeLists().read(0)) {
            MerchantRecipe newRecipe = InternalProxyUtils.getProxy(VillagerTradeProxy.class).displayTrade(recipe);
            recipes.add(newRecipe);
        }

        packet.getMerchantRecipeLists().write(0, recipes);
    }
}
