package com.willfp.eco.internal.spigot.integrations.custombiomes

import com.dfsek.terra.bukkit.world.BukkitAdapter
import com.willfp.eco.core.integrations.custombiomes.CustomBiome
import com.willfp.eco.core.integrations.custombiomes.CustomBiomesIntegration
import org.bukkit.Location

class CustomBiomesTerra: CustomBiomesIntegration {
    override fun getPluginName(): String {
        return "Terra"
    }

    override fun getBiome(location: Location?): CustomBiome? {
        if (location == null || location.world == null) {
            return null
        }

        val terraLocation = BukkitAdapter.adapt(location) ?: return null
        val terraWorld = BukkitAdapter.adapt(location.world!!) ?: return null
        val biomeProvider = terraWorld.biomeProvider ?: return null
        val biome = biomeProvider.getBiome(terraLocation, terraWorld.seed) ?: return null

        return CustomBiome(biome.id)
    }
}