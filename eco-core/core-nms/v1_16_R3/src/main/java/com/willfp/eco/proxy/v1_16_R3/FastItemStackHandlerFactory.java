package com.willfp.eco.proxy.v1_16_R3;

import com.willfp.eco.internal.fast.AbstractFastItemStackHandler;
import com.willfp.eco.proxy.proxies.FastItemStackHandlerFactoryProxy;
import com.willfp.eco.proxy.v1_16_R3.fast.FastItemStackHandler;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class FastItemStackHandlerFactory implements FastItemStackHandlerFactoryProxy {
    /**
     * The field to get the handle without a copy.
     */
    private static final Field HANDLE_FIELD;

    @Override
    public AbstractFastItemStackHandler create(@NotNull final ItemStack itemStack) {
        return new FastItemStackHandler(get(itemStack));
    }

    /**
     * Get from field.
     *
     * @param itemStack The ItemStack.
     * @return The NMS ItemStack.
     */
    public net.minecraft.server.v1_16_R3.ItemStack get(@NotNull final ItemStack itemStack) {
        Validate.isTrue(itemStack instanceof CraftItemStack, "Must be a CraftItemStack!");

        try {
            return (net.minecraft.server.v1_16_R3.ItemStack) HANDLE_FIELD.get(itemStack);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return null;
    }

    static {
        Field handle = null;
        try {
            handle = CraftItemStack.class.getDeclaredField("handle");
            handle.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        HANDLE_FIELD = handle;
    }
}
