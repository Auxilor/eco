package com.willfp.eco.core.config.updating;

import java.lang.annotation.*;

/**
 * This annotation should not be used.
 *
 * @deprecated Part of the reflective reload system that has been removed in 6.77.0.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Deprecated(since = "6.67.0", forRemoval = true)
public @interface ConfigUpdater {
}
