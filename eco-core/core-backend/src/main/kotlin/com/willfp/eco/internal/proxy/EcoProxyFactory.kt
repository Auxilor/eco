package com.willfp.eco.internal.proxy

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.proxy.ProxyConstants
import com.willfp.eco.core.proxy.ProxyFactory
import com.willfp.eco.core.proxy.exceptions.ProxyError
import com.willfp.eco.core.proxy.exceptions.UnsupportedVersionError
import java.net.URLClassLoader
import java.util.IdentityHashMap

class EcoProxyFactory(
    private val plugin: EcoPlugin
) : ProxyFactory {
    private val proxyClassLoader: ClassLoader = plugin::class.java.classLoader
    private val cache: MutableMap<Class<out Any>, Any> = IdentityHashMap()

    override fun <T : Any> getProxy(proxyClass: Class<T>): T {
        try {
            val cachedProxy: T? = attemptCache(proxyClass)
            if (cachedProxy != null) {
                return cachedProxy
            }

            val className =
                this.plugin.proxyPackage + "." + ProxyConstants.NMS_VERSION + "." + proxyClass.simpleName.replace(
                    "Proxy",
                    ""
                )
            val clazz = this.plugin::class.java.classLoader.loadClass(className)
            val instance = clazz.getDeclaredConstructor().newInstance()
            if (proxyClass.isAssignableFrom(clazz) && proxyClass.isInstance(instance)) {
                val proxy = proxyClass.cast(instance)
                cache[proxyClass] = proxy
                return proxy
            }
        } catch (e: Exception) {
            throw proxyErrorFrom(e)
        }

        throw proxyErrorFrom(IllegalArgumentException("Class doesn't seem to be a proxy."))
    }

    private fun proxyErrorFrom(e: Exception): Throwable {
        plugin.logger.severe("Fatal error with proxies! This plugin can't load.")

        return if (!ProxyConstants.SUPPORTED_VERSIONS.contains(ProxyConstants.NMS_VERSION)) {
            ProxyError(
                "Could not initialize proxy.",
                UnsupportedVersionError()
            )
        } else {
            ProxyError(
                "Could not initialize proxy. Are you running a supported server version?",
                e
            )
        }
    }

    private fun <T : Any> attemptCache(proxyClass: Class<T>): T? {
        val proxy = cache[proxyClass] ?: return null

        if (proxyClass.isInstance(proxy)) {
            return proxyClass.cast(proxy)
        }

        return null
    }

    fun clean() {
        if (proxyClassLoader is URLClassLoader) {
            proxyClassLoader.close()
        }

        cache.clear()
    }
}
