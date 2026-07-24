package com.willfp.eco.internal.spigot.hologram

import com.willfp.eco.core.integrations.hologram.Hologram
import com.willfp.eco.core.integrations.hologram.HologramOptions
import com.willfp.eco.internal.spigot.proxies.NativeHologramHandle
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.Location
import org.bukkit.entity.Player

class EcoHologram(
    val handle: NativeHologramHandle,
    location: Location,
    private val options: HologramOptions,
    private val tracker: HologramTracker
) : Hologram {
    private val visibleByDefault = options.isVisibleByDefault

    // When visibleByDefault: this is the opt-out (hidden) set.
    // When not: this is the opt-in (shown) set.
    private val exceptions: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

    @Volatile
    private var contents: List<String> = options.contents

    @Volatile
    private var removed = false

    // Cloned so later mutation of the Location the caller passed in doesn't silently
    // desync this hologram's position (consistent with setLocation, which also clones).
    @Volatile
    private var loc: Location = location.clone()

    init {
        tracker.register(this)
    }

    fun currentContents(): List<String> = contents

    fun getWorldLocation(): Location = loc.clone()

    /** Whether [player] is eligible to see this hologram (ignores distance; tracker handles range). */
    fun shouldSee(player: Player): Boolean {
        if (removed) return false
        val inSet = exceptions.contains(player.uniqueId)
        return if (visibleByDefault) !inSet else inSet
    }

    override fun remove() {
        if (removed) return
        removed = true
        tracker.unregister(this)
    }

    override fun setContents(contents: MutableList<String>) {
        this.contents = ArrayList(contents)
        tracker.pushContents(this)
    }

    override fun hide(player: Player) {
        if (visibleByDefault) {
            exceptions.add(player.uniqueId)
        } else {
            exceptions.remove(player.uniqueId)
        }
        tracker.refreshFor(player)
    }

    override fun show(player: Player) {
        if (visibleByDefault) {
            exceptions.remove(player.uniqueId)
        } else {
            exceptions.add(player.uniqueId)
        }
        tracker.refreshFor(player)
    }

    override fun setLocation(location: Location) {
        this.loc = location.clone()
        tracker.pushLocation(this)
    }

    override fun getLocation(): Location = loc.clone()
}
