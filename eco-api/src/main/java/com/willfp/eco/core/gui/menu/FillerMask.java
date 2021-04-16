package com.willfp.eco.core.gui.menu;

import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.internal.gui.FillerSlot;
import com.willfp.eco.util.ListUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FillerMask {
    @Getter
    private final List<List<Slot>> mask;

    public FillerMask(@NotNull final Material material,
                      @NotNull final String... pattern) {
        if (material == Material.AIR) {
            throw new IllegalArgumentException("Material cannot be air!");
        }

        mask = ListUtils.create2DList(6, 9);

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§r");
        itemStack.setItemMeta(meta);

        int row = 0;

        for (String patternRow : pattern) {
            int column = 0;
            if (patternRow.length() != 9) {
                throw new IllegalArgumentException("Invalid amount of columns in pattern!");
            }
            for (char c : patternRow.toCharArray()) {
                if (c == '0') {
                    mask.get(row).set(column, null);
                } else if (c == '1') {
                    mask.get(row).set(column, new FillerSlot(itemStack));
                } else {
                    throw new IllegalArgumentException("Invalid character in pattern! (Must only be 0 and 1)");
                }

                column++;
            }
            row++;
        }
    }
}
