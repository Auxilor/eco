package com.willfp.eco.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@UtilityClass
public class ReflectionUtils {
    /**
     * Instance of unsafe.
     */
    private static final Unsafe UNSAFE;

    /**
     * Set a final field.
     *
     * @param field The field.
     * @param value The value.
     */
    public void setFinalField(@NotNull final Field field,
                              @NotNull final Object value) {
        field.setAccessible(true);
        final Object staticFieldBase = UNSAFE.staticFieldBase(field);
        final long staticFieldOffset = UNSAFE.staticFieldOffset(field);
        UNSAFE.putObject(staticFieldBase, staticFieldOffset, value);
    }


    static {
        Unsafe unsafeInProgress;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafeInProgress = (Unsafe) unsafeField.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            unsafeInProgress = null;
        }
        UNSAFE = unsafeInProgress;
    }
}
