package com.willfp.eco.internal.spigot.proxy.common.packet.display

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import java.util.UUID
import net.minecraft.network.HashedStack

object CursorOriginals {
    private val originals: Cache<Pair<UUID, Int>, HashedStack> = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(Duration.ofMinutes(5))
        .build()

    fun put(player: UUID, displayedHash: Int, original: HashedStack) {
        originals.put(player to displayedHash, original)
    }

    fun get(player: UUID, displayedHash: Int): HashedStack? =
        originals.getIfPresent(player to displayedHash)
}
