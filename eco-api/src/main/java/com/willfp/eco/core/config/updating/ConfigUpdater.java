package com.willfp.eco.core.config.updating;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to put on update methods.
 * <p>
 * All update methods must be public and static,
 * and can accept an EcoPlugin as a parameter.
 * <p>
 * As such, there are only 2 valid update methods:
 * <p>
 * The first:
 * <pre>{@code
 * @ConfigUpdater
 * public static void update() {
 *     // Update code
 * }
 * }</pre>
 * <p>
 * The second:
 * <pre>{@code
 * public static void update(EcoPlugin plugin) {
 *     // Update code
 * }
 * }</pre>
 * <p>
 * If using kotlin, you have to annotate the method with {@code @JvmStatic}
 * in order to prevent null pointer exceptions - this also means that you cannot
 * have config updater methods in companion objects.
 * <p>
 * Config update methods in all classes in a plugin jar will be called
 * on reload.
 * <p>
 * By having a plugin as a parameter, you shouldn't really need getInstance()
 * calls in your code.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ConfigUpdater {
}
