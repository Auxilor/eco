package com.willfp.eco.internal.compat

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.proxy.exceptions.ProxyError

private const val BASE_PACKAGE = "com.willfp.eco.internal.compat.modern"
private val isModern = Prerequisite.HAS_PAPER.isMet && Prerequisite.HAS_1_21.isMet

internal annotation class ModernCompatibilityProxy(
    val location: String
)

private val cache = mutableMapOf<Class<*>, Any>()

object ModernCompatibilityScope {
    inline fun <reified T> loadProxy(): T {
        return loadCompatibilityProxy(T::class.java)
    }

    inline fun <reified T> useProxy(block: T.() -> Any?) {
        val proxy = loadProxy<T>()

        with(proxy) {
            block()
        }
    }
}

fun <R> ifModern(block: ModernCompatibilityScope.() -> R) {
    if (!isModern) {
        return
    }

    block(ModernCompatibilityScope)
}

fun <T> loadCompatibilityProxy(clazz: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return cache.getOrPut(clazz) {
        loadProxyUncached(clazz)
    } as T
}

private fun loadProxyUncached(clazz: Class<*>): Any {
    val proxy = clazz.getAnnotation(ModernCompatibilityProxy::class.java)
    val location = proxy?.location ?: throw IllegalArgumentException("Class ${clazz.name} is not a proxy")
    val className = "$BASE_PACKAGE.$location"

    try {
        val found = Class.forName(className)

        val constructor = found.getConstructor()
        val instance = constructor.newInstance()

        if (!clazz.isInstance(instance)) {
            throw ProxyError(
                "Modern compatibility proxy class $className does not implement ${clazz.name}",
                ClassCastException()
            )
        }

        return instance
    } catch (e: ClassNotFoundException) {
        throw ProxyError("Could not find modern compatibility proxy class $className", e)
    } catch (e: NoSuchMethodException) {
        throw ProxyError("Could not find no-args constructor for modern compatibility proxy class $className", e)
    }
}
