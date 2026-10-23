package com.willfp.eco.core.gui

import com.willfp.eco.core.gui.menu.Menu
import org.bukkit.entity.Player
import java.util.UUID
import java.util.WeakHashMap
import kotlin.reflect.KProperty

/** Delegates menu state to a fixed variable. */
interface MenuStateVar<T> {
    operator fun get(player: Player): T
    operator fun set(player: Player, value: T)
}

/** Menu state variable with null as default. */
private class NullableMenuStateVar<T : Any>(
    private val menu: Menu,
    private val key: String
) : MenuStateVar<T?> {
    override operator fun get(player: Player): T? =
        menu.getState(player, key)

    override operator fun set(player: Player, value: T?) =
        menu.setState(player, key, value)
}

/** Menu state variable with a fixed default. */
private class NotNullMenuStateVar<T : Any>(
    private val menu: Menu,
    private val key: String,
    private val default: T
) : MenuStateVar<T> {
    override operator fun get(player: Player): T =
        menu.getState(player, key) ?: default

    override operator fun set(player: Player, value: T) =
        menu.setState(player, key, value)
}

/** Create a menu state variable. */
fun <T : Any> menuStateVar(
    menu: Menu,
    key: String
): MenuStateVar<T?> = NullableMenuStateVar(menu, key)

/** Create a menu state variable with a [default] value. */
fun <T : Any> menuStateVar(
    menu: Menu,
    key: String,
    default: T
): MenuStateVar<T> = NotNullMenuStateVar(menu, key, default)

/** Instant delegate state variable. */
fun <T : Any> menuStateVar(
    key: String
) = lazyWithReceiver<Menu, MenuStateVar<T?>> {
    menuStateVar(this, key)
}

/** Instant delegate state variable with a [default]. */
fun <T : Any> menuStateVar(
    key: String,
    default: T
) = lazyWithReceiver<Menu, MenuStateVar<T>> {
    menuStateVar(this, key, default)
}

/** Instant delegate state variable with automatic key generation. */
fun <T : Any> menuStateVar() = lazyWithReceiver<Menu, MenuStateVar<T?>> {
    menuStateVar(this, UUID.randomUUID().toString())
}

/**
 * Instant delegate state variable with a [default] and automatic key
 * generation.
 */
fun <T : Any> menuStateVar(default: T) = lazyWithReceiver<Menu, MenuStateVar<T>> {
    menuStateVar(this, UUID.randomUUID().toString(), default)
}

/** Lazy value cached per receiver, adapted from https://stackoverflow.com/a/38084930/11427550. */
class LazyWithReceiver<T, R> internal constructor(val initializer: T.() -> R) {
    private val values = WeakHashMap<T, R>()

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(ref: T, property: KProperty<*>): R = synchronized(values) {
        return values.getOrPut(ref) { ref.initializer() }
    }
}

/** Lazy initializer with a receiver, useful for extension properties. */
fun <T, R> lazyWithReceiver(getter: T.() -> R) = LazyWithReceiver<T, R>() {
    this.getter()
}
