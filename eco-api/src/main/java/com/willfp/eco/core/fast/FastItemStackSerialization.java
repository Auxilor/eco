package com.willfp.eco.core.fast;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
@ApiStatus.Internal
public class FastItemStackSerialization implements FastItemStack {

    static {
        ConfigurationSerialization.registerClass(FastItemStack.class);
    }

    private final FastItemStack delegate;

    FastItemStackSerialization(FastItemStack delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasEnchantmentsOnItem() {
        return this.delegate.hasEnchantmentsOnItem();
    }

    @Override
    public Map<Enchantment, Integer> getEnchantmentsOnItem(boolean checkStored) {
        return this.delegate.getEnchantmentsOnItem(checkStored);
    }

    @Override
    public int getLevelOnItem(@NotNull Enchantment enchantment, boolean checkStored) {
        return this.delegate.getLevelOnItem(enchantment,checkStored);
    }

    @Override
    public boolean hasLore() {
        return this.delegate.hasLore();
    }

    @Override
    public void setLore(@Nullable List<String> lore) {
        this.delegate.setLore(lore);
    }

    @Override
    public List<String> getLore() {
        return this.delegate.getLore();
    }

    @Override
    public boolean hasItemFlags() {
        return this.delegate.hasItemFlags();
    }

    @Override
    public void setItemFlags(Set<ItemFlag> itemFlags) {
        this.delegate.setItemFlags(itemFlags);
    }

    @Override
    public Set<ItemFlag> getItemFlags() {
        return this.delegate.getItemFlags();
    }

    @Override
    public boolean hasDisplayName() {
        return this.delegate.hasDisplayName();
    }

    @Override
    public void setDisplayName(String displayName) {
        this.delegate.setDisplayName(displayName);
    }

    @Override
    public String getDisplayName() {
        return this.delegate.getDisplayName();
    }

    @Override
    public void setUnbreakable(boolean unbreakable) {
        this.delegate.setUnbreakable(unbreakable);
    }

    @Override
    public boolean isUnbreakable() {
        return this.delegate.isUnbreakable();
    }

    @Override
    public void setType(Material material) {
        this.delegate.setType(material);
    }

    @Override
    public Material getType() {
        return this.delegate.getType();
    }

    @Override
    public boolean isStackable() {
        return this.delegate.isStackable();
    }

    @Override
    public void setAmount(int amount) {
        this.delegate.setAmount(amount);
    }

    @Override
    public int getAmount() {
        return this.delegate.getAmount();
    }

    @Override
    public void shrink(int amount) {
        this.delegate.shrink(amount);
    }

    @Override
    public int getDurability() {
        return this.delegate.getDurability();
    }

    @Override
    public void setDurability(int durability) {
        this.delegate.setDurability(durability);
    }

    @Override
    public boolean isSimilar(FastItemStack itemStack) {
        return this.delegate.isSimilar(itemStack);
    }

    @Override
    public ItemStack unwrap() {
        return this.delegate.unwrap();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return null; //TODO
    }

    @NotNull
    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return this.delegate.getPersistentDataContainer();
    }

    static FastItemStack deserialize(Map<String,Object> map) {
        return null; //TODO
    }
}
