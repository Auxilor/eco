package com.willfp.eco.internal.spigot.eventlisteners

import org.bukkit.entity.Entity
import java.util.UUID

class LastDamagerTracker(
    private val windowTicks: Long,
    private val clock: () -> Long
) {
    private data class Record(val damager: Entity, val tick: Long)

    private val records = mutableMapOf<UUID, Record>()

    fun record(victim: UUID, damager: Entity) {
        records[victim] = Record(damager, clock())
    }

    fun resolve(victim: UUID): Entity? {
        val record = records[victim] ?: return null
        return if (clock() - record.tick <= windowTicks) record.damager else null
    }

    fun forget(victim: UUID) {
        records.remove(victim)
    }

    fun purgeExpired() {
        val now = clock()
        records.entries.removeIf { now - it.value.tick > windowTicks }
    }
}
