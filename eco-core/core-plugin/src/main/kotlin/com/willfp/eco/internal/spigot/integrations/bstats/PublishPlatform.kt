package com.willfp.eco.internal.spigot.integrations.bstats

enum class PublishPlatform(private val markerLabel: String, val displayName: String) {
    SPIGOT("SPIGOT", "SpigotMC"),
    POLYMART("VoxelShop", "VoxelShop"),
    BUILTBYBIT("BUILTBYBIT", "BuiltByBit"),
    MODRINTH("MODRINTH", "Modrinth"),
    MCMODELS("MCMODELS", "MCModels"),
    ECOHUB("EcoHub", "EcoHub"),
    OTHER("OTHER", "Other");

    companion object {
        fun fromMarkerLabel(label: String): PublishPlatform =
            entries.firstOrNull { it.markerLabel == label } ?: OTHER
    }
}
