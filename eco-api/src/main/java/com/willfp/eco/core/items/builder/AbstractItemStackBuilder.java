package com.willfp.eco.core.items.builder;

import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.util.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Abstract item builder class to build items.
 *
 * @param <T> The meta type.
 * @param <U> Itself.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractItemStackBuilder<T extends ItemMeta, U extends AbstractItemStackBuilder<T, U>> implements ItemBuilder {
    /**
     * The ItemMeta used while building.
     */
    private T meta;

    /**
     * The ItemStack.
     */
    private final ItemStack base;

    /**
     * Create a new ItemStackBuilder.
     *
     * @param material The material.
     */
    protected AbstractItemStackBuilder(@NotNull final Material material) {
        this(new ItemStack(material));
    }

    /**
     * Create a new ItemStackBuilder to modify an existing item.
     *
     * @param item The item to start with.
     */
    protected AbstractItemStackBuilder(@NotNull final TestableItem item) {
        this(item.getItem());
    }

    /**
     * Create a new ItemStackBuilder to modify an existing item.
     *
     * @param base The ItemStack to start with.
     */
    protected AbstractItemStackBuilder(@NotNull final ItemStack base) {
        if (base.getType() == Material.AIR) {
            base.setType(Material.STONE); // Prevents NPEs.
        }

        this.base = base;
        this.meta = (T) base.getItemMeta();
        assert meta != null;
    }

    @Override
    public U setAmount(final int amount) {
        Validate.isTrue(amount >= 1 && amount <= base.getMaxStackSize());
        base.setAmount(amount);
        return (U) this;
    }

    @Override
    public U setAmount(@NotNull final Supplier<Integer> amount) {
        return setAmount(amount.get());
    }

    @Override
    public U addEnchantment(@NotNull final Enchantment enchantment,
                            final int level) {
        //noinspection ConstantConditions
        if (enchantment == null) {
            return (U) this;
        }

        meta.addEnchant(enchantment, level, true);
        return (U) this;
    }

    @Override
    public U addEnchantment(@NotNull final Supplier<Enchantment> enchantment,
                            @NotNull final Supplier<Integer> level) {
        return addEnchantment(enchantment.get(), level.get());
    }

    @Override
    public U setDisplayName(@NotNull final String name) {
        meta.setDisplayName(StringUtils.format(name));
        return (U) this;
    }

    @Override
    public U setDisplayName(@NotNull final Supplier<String> name) {
        String result = name.get();

        return result == null ? (U) this : setDisplayName(name.get());
    }

    @Override
    public U addLoreLine(@NotNull final String line) {
        base.setItemMeta(meta);

        FastItemStack fis = FastItemStack.wrap(base);

        List<String> lore = fis.getLore();
        lore.add(StringUtils.format(line));
        fis.setLore(lore);

        meta = (T) base.getItemMeta();

        return (U) this;
    }

    @Override
    public U addLoreLine(@NotNull final Supplier<String> line) {
        String result = line.get();

        return result == null ? (U) this : addLoreLine(line.get());
    }

    @Override
    public U addLoreLines(@NotNull final List<String> lines) {
        base.setItemMeta(meta);

        FastItemStack fis = FastItemStack.wrap(base);

        List<String> lore = fis.getLore();

        for (String line : lines) {
            lore.add(StringUtils.format(line));
        }

        fis.setLore(lore);

        meta = (T) base.getItemMeta();

        return (U) this;
    }

    @Override
    public U addLoreLines(@NotNull final Supplier<List<String>> lines) {
        List<String> result = lines.get();

        return result == null ? (U) this : addLoreLines(lines.get());
    }

    @Override
    public U addItemFlag(@NotNull final ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);

        return (U) this;
    }

    @Override
    public U addItemFlag(@NotNull final Supplier<ItemFlag[]> itemFlags) {
        ItemFlag[] result = itemFlags.get();

        return result == null ? (U) this : addItemFlag(result);
    }

    @Override
    public <A, B> U writeMetaKey(@NotNull final NamespacedKey key,
                                 @NotNull final PersistentDataType<A, B> type,
                                 @NotNull final B value) {
        meta.getPersistentDataContainer().set(key, type, value);

        return (U) this;
    }

    @Override
    public <A, B> U writeMetaKey(@NotNull final Supplier<NamespacedKey> key,
                                 @NotNull final Supplier<PersistentDataType<A, B>> type,
                                 @NotNull final Supplier<B> value) {
        return writeMetaKey(key.get(), type.get(), value.get());
    }

    @Override
    public U setUnbreakable(final boolean unbreakable) {
        meta.setUnbreakable(unbreakable);

        return (U) this;
    }

    @Override
    public U setUnbreakable(@NotNull final Supplier<Boolean> unbreakable) {
        Boolean result = unbreakable.get();

        return result == null ? (U) this : setUnbreakable(unbreakable);
    }

    @Override
    public U setCustomModelData(@Nullable final Integer data) {
        meta.setCustomModelData(data);

        return (U) this;
    }

    @Override
    public U setCustomModelData(@NotNull final Supplier<Integer> data) {
        Integer result = data.get();

        return result == null ? (U) this : setCustomModelData(result);
    }

    @Override
    public ItemStack build() {
        base.setItemMeta(meta);

        return base;
    }

    /**
     * Get the base ItemStack.
     *
     * @return The base ItemStack.
     */
    protected ItemStack getBase() {
        return base;
    }

    /**
     * Get the meta.
     *
     * @return The meta.
     */
    protected T getMeta() {
        return meta;
    }
}
