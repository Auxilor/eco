package com.willfp.eco.core.gui.menu;

import com.willfp.eco.internal.gui.FillerSlot;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class FillerMask {
    @Getter
    private final FillerSlot[][] mask;

    public FillerMask(@NotNull final Material material,
                      @NotNull final Menu menu,
                      @NotNull final String... pattern) {
        if (pattern.length != menu.getRows()) {
            throw new IllegalArgumentException("Invalid amount of rows specified in pattern!");
        }

        if (material == Material.AIR) {
            throw new IllegalArgumentException("Material cannot be air!");
        }

        mask = new FillerSlot[menu.getRows()][9];

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        meta.setDisplayName("");
        itemStack.setItemMeta(meta);

        int row = 0;

        for (String patternRow : pattern) {
            int column = 0;
            if (pattern.length != 9) {
                throw new IllegalArgumentException("Invalid amount of columns in pattern!");
            }
            for (char c : patternRow.toCharArray()) {
                if (c == '0') {
                    mask[row][column] = null;
                } else if (c == '1') {
                    mask[row][column] = new FillerSlot(itemStack);
                } else {
                    throw new IllegalArgumentException("Invalid character in pattern! (Must only be 0 and 1)");
                }
            }
            row++;
        }
    }
}
