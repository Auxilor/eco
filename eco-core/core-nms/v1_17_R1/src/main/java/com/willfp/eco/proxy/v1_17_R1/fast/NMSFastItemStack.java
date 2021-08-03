package com.willfp.eco.proxy.v1_17_R1.fast;

import com.willfp.eco.internal.fast.EcoFastItemStack;
import com.willfp.eco.util.StringUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NMSFastItemStack extends EcoFastItemStack<ItemStack> {
    private List<String> loreCache = null;

    public NMSFastItemStack(@NotNull final org.bukkit.inventory.ItemStack itemStack) {
        super(FastItemStackUtils.getNMSStack(itemStack), itemStack);
    }

    @Override
    public Map<Enchantment, Integer> getEnchantmentsOnItem(final boolean checkStored) {
        ListTag enchantmentNBT = checkStored && this.getHandle().getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(this.getHandle()) : this.getHandle().getEnchantmentTags();
        Map<Enchantment, Integer> foundEnchantments = new HashMap<>();

        for (Tag base : enchantmentNBT) {
            CompoundTag compound = (CompoundTag) base;
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
        ListTag enchantmentNBT = checkStored && this.getHandle().getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(this.getHandle()) : this.getHandle().getEnchantmentTags();

        for (Tag base : enchantmentNBT) {
            CompoundTag compound = (CompoundTag) base;
            String key = compound.getString("id");
            if (!key.equals(enchantment.getKey().toString())) {
                continue;
            }

            return '\uffff' & compound.getShort("lvl");
        }
        return 0;
    }

    @Override
    public void setLore(@Nullable final List<String> lore) {
        loreCache = null;

        List<String> jsonLore = new ArrayList<>();
        if (lore != null) {
            for (String s : lore) {
                jsonLore.add(StringUtils.legacyToJson(s));
            }
        }

        CompoundTag displayTag = this.getHandle().getOrCreateTagElement("display");
        if (!displayTag.contains("Lore")) {
            displayTag.put("Lore", new ListTag());
        }

        ListTag loreTag = displayTag.getList("Lore", CraftMagicNumbers.NBT.TAG_STRING);
        loreTag.clear();
        for (String s : jsonLore) {
            loreTag.add(StringTag.valueOf(s));
        }

        apply();
    }

    @Override
    public List<String> getLore() {
        if (loreCache != null) {
            return loreCache;
        }

        List<String> lore = new ArrayList<>();

        for (String s : this.getLoreJSON()) {
            lore.add(StringUtils.jsonToLegacy(s));
        }

        loreCache = lore;

        return lore;
    }

    private List<String> getLoreJSON() {
        CompoundTag displayTag = this.getHandle().getOrCreateTagElement("display");

        if (displayTag.contains("Lore")) {
            ListTag loreTag = displayTag.getList("Lore", CraftMagicNumbers.NBT.TAG_STRING);
            List<String> lore = new ArrayList<>(loreTag.size());
            for (int i = 0; i < loreTag.size(); i++) {
                lore.add(loreTag.getString(i));
            }
            return lore;
        } else {
            return new ArrayList<>();
        }
    }

    public void apply() {
        if (!(this.getBukkit() instanceof CraftItemStack)) {
            this.getBukkit().setItemMeta(CraftItemStack.asCraftMirror(this.getHandle()).getItemMeta());
        }
    }
}
