package com.willfp.eco.proxy.v1_16_R3.fast;

import com.willfp.eco.internal.fast.AbstractFastItemStackHandler;
import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import net.minecraft.server.v1_16_R3.NBTTagString;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FastItemStackHandler implements AbstractFastItemStackHandler {
    /**
     * The ItemStack.
     */
    private final net.minecraft.server.v1_16_R3.ItemStack itemStack;

    /**
     * The nbt tag.
     */
    private final NBTTagCompound tag;

    /**
     * Create new FastItemStack handler.
     *
     * @param itemStack The ItemStack.
     */
    public FastItemStackHandler(@NotNull final net.minecraft.server.v1_16_R3.ItemStack itemStack) {
        this.itemStack = itemStack;

        this.tag = itemStack.getOrCreateTag();
    }


    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        NBTTagList enchantmentNBT = itemStack.getEnchantments();
        HashMap<Enchantment, Integer> foundEnchantments = new HashMap<>();

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
    public int getEnchantmentLevel(@NotNull final Enchantment enchantment) {
        NBTTagList enchantmentNBT = itemStack.getEnchantments();

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
    public List<String> getLore() {
        if (tag.hasKey("Lore")) {
            NBTTagList list = tag.getList("Lore", CraftMagicNumbers.NBT.TAG_STRING);
            List<String> lore = new ArrayList<>(list.size());

            for (int index = 0; index < list.size(); index++) {
                String line = list.getString(index);
                lore.add(CraftChatMessage.fromJSONComponent(line));
            }

            return lore;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void setLore(@NotNull final List<String> lore) {
        NBTTagList tagList = new NBTTagList();
        for (String value : lore) {
            tagList.add(NBTTagString.a(CraftChatMessage.fromJSONComponent(value)));
        }

        setDisplayTag("Lore", tagList);
    }

    @Override
    public Set<ItemFlag> getItemFlags() {
        return null;
    }

    @Override
    public void setItemFlags(@NotNull final Set<ItemFlag> flags) {

    }

    @Override
    public <T, Z> void writePersistentKey(@NotNull final NamespacedKey key,
                                          @NotNull final PersistentDataType<T, Z> type,
                                          @NotNull final Z value) {

    }

    @Override
    public <T, Z> Z readPersistentKey(@NotNull final NamespacedKey key,
                                      @NotNull final PersistentDataType<T, Z> type) {
        return null;
    }

    @Override
    public Set<NamespacedKey> getPersistentKeys() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return CraftChatMessage.fromComponent(itemStack.getName());
    }

    @Override
    public void setDisplayName(@NotNull final String name) {
        itemStack.a(CraftChatMessage.fromStringOrNull(name));
    }

    @Override
    public void apply() {
        itemStack.setTag(tag);
    }

    private NBTTagCompound getDisplayTag(@NotNull final NBTTagCompound base) {
        return base.getCompound("display");
    }

    private void setDisplayTag(@NotNull final String key,
                               @NotNull final NBTBase value) {
        final NBTTagCompound display = tag.getCompound("display");

        display.set(key, value);

        tag.set("display", display);
    }
}
