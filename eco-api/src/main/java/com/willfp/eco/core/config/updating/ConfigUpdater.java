package com.willfp.eco.core.config.updating;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
