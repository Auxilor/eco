package com.willfp.eco.proxy.v1_16_R3.fast;

import com.willfp.eco.core.fast.FastItemStack;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.ItemEnchantedBook;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.Items;
import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import net.minecraft.server.v1_16_R3.NBTTagString;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EcoFastItemStack implements FastItemStack {
    private final ItemStack handle;
    private final boolean isCIS;
    private final org.bukkit.inventory.ItemStack bukkit;
    private List<String> loreCache = null;

    public EcoFastItemStack(@NotNull final org.bukkit.inventory.ItemStack itemStack) {
        this.handle = FastItemStackUtils.getNMSStack(itemStack);
        if (itemStack instanceof CraftItemStack craftItemStack) {
            this.isCIS = true;
            this.bukkit = craftItemStack;
        } else {
            this.isCIS = false;
            this.bukkit = itemStack;
        }
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

    @Override
    public void setLore(@Nullable final List<String> lore) {
        loreCache = null;

        List<String> jsonLore = new ArrayList<>();
        if (lore != null) {
            for (String s : lore) {
                jsonLore.add(ComponentSerializer.toString(TextComponent.fromLegacyText(s)));
            }
        }

        NBTTagCompound displayTag = handle.a("display");
        if (!displayTag.hasKey("Lore")) {
            displayTag.set("Lore", new NBTTagList());
        }

        NBTTagList loreTag = displayTag.getList("Lore", CraftMagicNumbers.NBT.TAG_STRING);
        loreTag.clear();
        for (String s : jsonLore) {
            loreTag.add(NBTTagString.a(s));
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
            IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a(s);
            lore.add(CraftChatMessage.fromComponent(component));
        }

        loreCache = lore;
        return lore;
    }

    private List<String> getLoreJSON() {
        NBTTagCompound displayTag = handle.a("display");

        if (displayTag.hasKey("Lore")) {
            NBTTagList loreTag = displayTag.getList("Lore", CraftMagicNumbers.NBT.TAG_STRING);
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
        if (!this.isCIS) {
            bukkit.setItemMeta(CraftItemStack.asCraftMirror(handle).getItemMeta());
        }
    }
}
