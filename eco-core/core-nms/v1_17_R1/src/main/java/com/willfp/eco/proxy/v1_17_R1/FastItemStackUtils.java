package com.willfp.eco.proxy.v1_17_R1;

import lombok.experimental.UtilityClass;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

@UtilityClass
public class FastItemStackUtils {
    private static final Field field;

    public static net.minecraft.world.item.ItemStack getNMSStack(@NotNull final ItemStack itemStack) {
        if (!(itemStack instanceof CraftItemStack)) {
            return CraftItemStack.asNMSCopy(itemStack);
        } else {
            try {
                return (net.minecraft.world.item.ItemStack) field.get(itemStack);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    static {
        Field temp = null;

        try {
            Field handleField = CraftItemStack.class.getDeclaredField("handle");
            handleField.setAccessible(true);
            temp = handleField;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        assert temp != null;
        Validate.notNull(temp, "Error occurred in initialization!");

        field = temp;
    }
}
