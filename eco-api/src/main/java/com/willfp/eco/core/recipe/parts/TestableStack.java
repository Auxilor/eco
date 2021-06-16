package com.willfp.eco.core.recipe.parts;

import com.willfp.eco.core.items.TestableItem;
import com.willfp.eco.internal.InternalInterfacing;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TestableStack implements TestableItem {
    /**
     * The item.
     */
    private final TestableItem handle;

    /**
     * The amount.
     */
    @Getter
    private final int amount;

    /**
     * Create a new testable stack.
     *
     * @param item   The item.
     * @param amount The amount.
     */
    public TestableStack(@NotNull final TestableItem item,
                         final int amount) {
        Validate.isTrue(!(item instanceof TestableStack));

        this.handle = item;
        this.amount = amount;
    }

    /**
     * If the item matches the material.
     *
     * @param itemStack The item to test.
     * @return If the item is of the specified material.
     */
    @Override
    public boolean matches(@Nullable final ItemStack itemStack) {
        return itemStack != null && handle.matches(itemStack) && itemStack.getAmount() >= amount;
    }

    @Override
    public ItemStack getItem() {
        ItemStack temp = handle.getItem().clone();
        ItemMeta meta = temp.getItemMeta();
        assert meta != null;

        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        assert lore != null;
        lore.add("");
        String add = InternalInterfacing.getInstance().getLang().getString("multiple-in-craft");
        add = add.replace("%amount%", String.valueOf(amount));
        lore.add(add);
        meta.setLore(lore);
        temp.setItemMeta(meta);
        temp.setAmount(amount);

        return temp;
    }
}
