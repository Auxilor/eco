package com.willfp.eco.proxy.v1_16_R3.fast;

import com.willfp.eco.core.fast.FastItemStack;
import net.minecraft.server.v1_16_R3.ItemEnchantedBook;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.Items;
import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EcoFastItemStack implements FastItemStack {
    private final ItemStack handle;

    public EcoFastItemStack(@NotNull final org.bukkit.inventory.ItemStack itemStack) {
        this.handle = FastItemStackUtils.getNMSStack(itemStack);
    }

    @Override
    public Map<Enchantment, Integer> getEnchantmentsOnItem(final boolean checkStored) {
        NBTTagList enchantmentNBT = checkStored && handle.getItem() == Items.ENCHANTED_BOOK ? ItemEnchantedBook.d(handle) : handle.getEnchantments();
        Map<Enchantment, Integer> foundEnchantments = new HashMap<>();

        for (NBTBase base : enchantmentNBT) {
            NBTTagCompound compound = (NBTTagCompound) base;
            String key = compound.getString("id");
            int level = '\uffff' & compound.getShort("lvl");

            Enchantment found = Enchantment.getByKey(CraftNamespacedKey.fromStringOrNull(key));
            if (found != null) {
                foundEnchantments.put(found, level);
            }
        }
        return foundEnchantments;
    }

    @Override
    public int getLevelOnItem(@NotNull final Enchantment enchantment,
                              final boolean checkStored) {
        NBTTagList enchantmentNBT = checkStored && handle.getItem() == Items.ENCHANTED_BOOK ? ItemEnchantedBook.d(handle) : handle.getEnchantments();

        for (NBTBase base : enchantmentNBT) {
            NBTTagCompound compound = (NBTTagCompound) base;
            String key = compound.getString("id");
            if (!key.equals(enchantment.getKey().toString())) {
                continue;
            }

            return '\uffff' & compound.getShort("lvl");
        }
        return 0;
    }
}
