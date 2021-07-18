package com.willfp.eco.spigot.integrations.customitems;

import com.willfp.eco.core.integrations.customitems.CustomItemsWrapper;
import com.willfp.eco.core.items.CustomItem;
import com.willfp.eco.util.NamespacedKeyUtils;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class CustomItemsOraxen implements CustomItemsWrapper {
    @Override
    public void registerAllItems() {
        for (ItemBuilder item : OraxenItems.getItems()) {
            ItemStack stack = item.build();
            String id = OraxenItems.getIdByItem(item);
            NamespacedKey key = NamespacedKeyUtils.create("oraxen", id.toLowerCase());

            new CustomItem(
                    key,
                    test -> {
                        String oraxenId = OraxenItems.getIdByItem(test);
                        if (oraxenId == null) {
                            return false;
                        }
                        return oraxenId.equalsIgnoreCase(id);
                    },
                    stack
            ).register();
        }
    }
}
