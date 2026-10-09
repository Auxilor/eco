package com.willfp.eco.internal.spigot.data.profiles.impl

import com.willfp.eco.core.data.Profile
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.internal.spigot.data.profiles.ProfileHandler
import com.willfp.eco.internal.spigot.data.profiles.isSavedLocally
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.function.UnaryOperator

abstract class EcoProfile(
    val uuid: UUID,
    private val handler: ProfileHandler
) : Profile {
    private val data = ConcurrentHashMap<PersistentDataKey<*>, Any>()

    override fun <T : Any> write(key: PersistentDataKey<T>, value: T) {
        // Queued inside compute so writes to the same key reach the writer in the order they land.
        this.data.compute(key) { _, _ ->
            handler.profileWriter.write(uuid, key, value)
            value
        }
    }

    override fun <T : Any> compute(key: PersistentDataKey<T>, function: UnaryOperator<T>): T {
        while (true) {
            // Load outside compute: a fetch can block, and read() writes to the same map.
            read(key)

            @Suppress("UNCHECKED_CAST")
            val result = this.data.computeIfPresent(key) { _, current ->
                val value = function.apply(current as T)
                handler.profileWriter.write(uuid, key, value)
                value
            } as T?

            // Null only if the key was invalidated between the read and the compute; load it again.
            if (result != null) {
                return result
            }
        }
    }

    override fun <T : Any> read(key: PersistentDataKey<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (this.data.containsKey(key)) {
            return this.data[key] as T
        }

        // A profile still in data.yml is carried across before it is read, so a read never sees
        // half a profile. Players are resolved on login instead, off the main thread; this is the
        // path for everything else - offline lookups, placeholders, admin commands.
        handler.ensureMigrated(uuid)

        this.data[key] = if (key.isSavedLocally) {
            handler.localHandler.read(uuid, key)
        } else {
            handler.defaultHandler.read(uuid, key)
        } ?: key.defaultValue

        return read(key)
    }

    /**
     * Drop the cached value of [key], so the next read fetches it from the handler again.
     */
    fun invalidate(key: PersistentDataKey<*>) {
        this.data.remove(key)
    }

    /**
     * Drop every cached value that is shared with other servers, keeping the ones only this server
     * stores.
     */
    fun invalidateShared() {
        this.data.keys.removeIf { !it.isSavedLocally }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EcoProfile) {
            return false
        }

        return this.uuid == other.uuid
    }

    override fun hashCode(): Int {
        return this.uuid.hashCode()
    }
}
