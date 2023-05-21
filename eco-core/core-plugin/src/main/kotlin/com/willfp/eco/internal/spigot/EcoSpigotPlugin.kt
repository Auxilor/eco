package com.willfp.eco.internal.spigot

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.data.ExternalDataStore
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.integrations.IntegrationLoader
import com.willfp.eco.core.integrations.afk.AFKManager
import com.willfp.eco.core.integrations.anticheat.AnticheatManager
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.customentities.CustomEntitiesManager
import com.willfp.eco.core.integrations.customitems.CustomItemsManager
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.eco.core.integrations.hologram.HologramManager
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.integrations.shop.ShopManager
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.core.particle.Particles
import com.willfp.eco.core.price.Prices
import com.willfp.eco.internal.data.MavenVersionToStringAdapter
import com.willfp.eco.internal.data.VersionToStringAdapter
import com.willfp.eco.internal.entities.EntityArgParserAdult
import com.willfp.eco.internal.entities.EntityArgParserAttackDamage
import com.willfp.eco.internal.entities.EntityArgParserAttackSpeed
import com.willfp.eco.internal.entities.EntityArgParserBaby
import com.willfp.eco.internal.entities.EntityArgParserCharged
import com.willfp.eco.internal.entities.EntityArgParserEquipment
import com.willfp.eco.internal.entities.EntityArgParserExplosionRadius
import com.willfp.eco.internal.entities.EntityArgParserFlySpeed
import com.willfp.eco.internal.entities.EntityArgParserFollowRange
import com.willfp.eco.internal.entities.EntityArgParserHealth
import com.willfp.eco.internal.entities.EntityArgParserJumpStrength
import com.willfp.eco.internal.entities.EntityArgParserKnockback
import com.willfp.eco.internal.entities.EntityArgParserKnockbackResistance
import com.willfp.eco.internal.entities.EntityArgParserName
import com.willfp.eco.internal.entities.EntityArgParserNoAI
import com.willfp.eco.internal.entities.EntityArgParserSilent
import com.willfp.eco.internal.entities.EntityArgParserSize
import com.willfp.eco.internal.entities.EntityArgParserSpawnReinforcements
import com.willfp.eco.internal.entities.EntityArgParserSpeed
import com.willfp.eco.internal.items.ArgParserColor
import com.willfp.eco.internal.items.ArgParserCustomModelData
import com.willfp.eco.internal.items.ArgParserEnchantment
import com.willfp.eco.internal.items.ArgParserFlag
import com.willfp.eco.internal.items.ArgParserName
import com.willfp.eco.internal.items.ArgParserTexture
import com.willfp.eco.internal.items.ArgParserUnbreakable
import com.willfp.eco.internal.lookup.SegmentParserGroup
import com.willfp.eco.internal.lookup.SegmentParserUseIfPresent
import com.willfp.eco.internal.particle.ParticleFactoryRGB
import com.willfp.eco.internal.price.PriceFactoryEconomy
import com.willfp.eco.internal.price.PriceFactoryXP
import com.willfp.eco.internal.price.PriceFactoryXPLevels
import com.willfp.eco.internal.spigot.arrows.ArrowDataListener
import com.willfp.eco.internal.spigot.data.DataListener
import com.willfp.eco.internal.spigot.data.DataYml
import com.willfp.eco.internal.spigot.data.PlayerBlockListener
import com.willfp.eco.internal.spigot.data.ProfileHandler
import com.willfp.eco.internal.spigot.data.storage.ProfileSaver
import com.willfp.eco.internal.spigot.drops.CollatedRunnable
import com.willfp.eco.internal.spigot.eventlisteners.EntityDeathByEntityListeners
import com.willfp.eco.internal.spigot.eventlisteners.NaturalExpGainListenersPaper
import com.willfp.eco.internal.spigot.eventlisteners.NaturalExpGainListenersSpigot
import com.willfp.eco.internal.spigot.eventlisteners.PlayerJumpListenersPaper
import com.willfp.eco.internal.spigot.eventlisteners.PlayerJumpListenersSpigot
import com.willfp.eco.internal.spigot.eventlisteners.armor.ArmorChangeEventListeners
import com.willfp.eco.internal.spigot.eventlisteners.armor.ArmorListener
import com.willfp.eco.internal.spigot.gui.GUIListener
import com.willfp.eco.internal.spigot.integrations.afk.AFKIntegrationCMI
import com.willfp.eco.internal.spigot.integrations.afk.AFKIntegrationEssentials
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatAAC
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatAlice
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatMatrix
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatNCP
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatSpartan
import com.willfp.eco.internal.spigot.integrations.anticheat.AnticheatVulcan
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefBentoBox
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefCombatLogXV10
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefCombatLogXV11
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefCrashClaim
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefDeluxeCombat
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefFabledSkyBlock
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefFactionsUUID
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefGriefPrevention
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefIridiumSkyblock
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefKingdoms
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefLands
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefPvPManager
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefRPGHorses
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefSuperiorSkyblock2
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefTowny
import com.willfp.eco.internal.spigot.integrations.antigrief.AntigriefWorldGuard
import com.willfp.eco.internal.spigot.integrations.customentities.CustomEntitiesMythicMobs
import com.willfp.eco.internal.spigot.integrations.customitems.CustomItemsCustomCrafting
import com.willfp.eco.internal.spigot.integrations.customitems.CustomItemsDenizen
import com.willfp.eco.internal.spigot.integrations.customitems.CustomItemsExecutableItems
import com.willfp.eco.internal.spigot.integrations.customitems.CustomItemsHeadDatabase
import com.willfp.eco.internal.spigot.integrations.customitems.CustomItemsItemsAdder
import com.willfp.eco.internal.spigot.integrations.customitems.CustomItemsMythicMobs
import com.willfp.eco.internal.spigot.integrations.customitems.CustomItemsOraxen
import com.willfp.eco.internal.spigot.integrations.customitems.CustomItemsScyther
import com.willfp.eco.internal.spigot.integrations.customrecipes.CustomRecipeCustomCrafting
import com.willfp.eco.internal.spigot.integrations.economy.EconomyVault
import com.willfp.eco.internal.spigot.integrations.entitylookup.EntityLookupModelEngine
import com.willfp.eco.internal.spigot.integrations.hologram.HologramCMI
import com.willfp.eco.internal.spigot.integrations.hologram.HologramDecentHolograms
import com.willfp.eco.internal.spigot.integrations.hologram.HologramHolographicDisplays
import com.willfp.eco.internal.spigot.integrations.mcmmo.McmmoIntegrationImpl
import com.willfp.eco.internal.spigot.integrations.multiverseinventories.MultiverseInventoriesIntegration
import com.willfp.eco.internal.spigot.integrations.placeholder.PlaceholderIntegrationPAPI
import com.willfp.eco.internal.spigot.integrations.price.PriceFactoryPlayerPoints
import com.willfp.eco.internal.spigot.integrations.price.PriceFactoryRoyaleEconomy
import com.willfp.eco.internal.spigot.integrations.price.PriceFactoryUltraEconomy
import com.willfp.eco.internal.spigot.integrations.shop.ShopDeluxeSellwands
import com.willfp.eco.internal.spigot.integrations.shop.ShopEconomyShopGUI
import com.willfp.eco.internal.spigot.integrations.shop.ShopShopGuiPlus
import com.willfp.eco.internal.spigot.integrations.shop.ShopZShop
import com.willfp.eco.internal.spigot.metrics.PlayerflowHandler
import com.willfp.eco.internal.spigot.proxy.FastItemStackFactoryProxy
import com.willfp.eco.internal.spigot.proxy.PacketHandlerProxy
import com.willfp.eco.internal.spigot.recipes.CraftingRecipeListener
import com.willfp.eco.internal.spigot.recipes.StackedRecipeListener
import com.willfp.eco.internal.spigot.recipes.listeners.ComplexInComplex
import com.willfp.eco.internal.spigot.recipes.listeners.ComplexInVanilla
import com.willfp.eco.internal.spigot.recipes.stackhandlers.ShapedCraftingRecipeStackHandler
import com.willfp.eco.internal.spigot.recipes.stackhandlers.ShapelessCraftingRecipeStackHandler
import com.willfp.eco.util.ClassUtils
import me.TechsCode.UltraEconomy.UltraEconomy
import me.qKing12.RoyaleEconomy.MultiCurrency.MultiCurrencyHandler
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

abstract class EcoSpigotPlugin : EcoPlugin() {
    abstract val dataYml: DataYml
    protected abstract val profileHandler: ProfileHandler
    protected var bukkitAudiences: BukkitAudiences? = null

    init {
        Items.registerArgParser(ArgParserEnchantment)
        Items.registerArgParser(ArgParserColor)
        Items.registerArgParser(ArgParserTexture)
        Items.registerArgParser(ArgParserCustomModelData)
        Items.registerArgParser(ArgParserFlag)
        Items.registerArgParser(ArgParserUnbreakable)
        Items.registerArgParser(ArgParserName)

        Entities.registerArgParser(EntityArgParserName)
        Entities.registerArgParser(EntityArgParserNoAI)
        Entities.registerArgParser(EntityArgParserAttackDamage)
        Entities.registerArgParser(EntityArgParserAttackSpeed)
        Entities.registerArgParser(EntityArgParserFlySpeed)
        Entities.registerArgParser(EntityArgParserFollowRange)
        Entities.registerArgParser(EntityArgParserHealth)
        Entities.registerArgParser(EntityArgParserJumpStrength)
        Entities.registerArgParser(EntityArgParserKnockback)
        Entities.registerArgParser(EntityArgParserKnockbackResistance)
        Entities.registerArgParser(EntityArgParserSize)
        Entities.registerArgParser(EntityArgParserSpawnReinforcements)
        Entities.registerArgParser(EntityArgParserSpeed)
        Entities.registerArgParser(EntityArgParserBaby)
        Entities.registerArgParser(EntityArgParserAdult)
        Entities.registerArgParser(EntityArgParserCharged)
        Entities.registerArgParser(EntityArgParserExplosionRadius)
        Entities.registerArgParser(EntityArgParserSilent)
        Entities.registerArgParser(EntityArgParserEquipment)

        Prices.registerPriceFactory(PriceFactoryEconomy)
        Prices.registerPriceFactory(PriceFactoryXPLevels)
        Prices.registerPriceFactory(PriceFactoryXP)

        Particles.registerParticleFactory(ParticleFactoryRGB)

        CraftingRecipeListener.registerListener(ComplexInComplex)
        CraftingRecipeListener.registerListener(ComplexInVanilla)

        StackedRecipeListener.registerHandler(ShapedCraftingRecipeStackHandler)
        StackedRecipeListener.registerHandler(ShapelessCraftingRecipeStackHandler)

        SegmentParserGroup.register()
        SegmentParserUseIfPresent.register()

        CustomItemsManager.registerProviders()

        ExternalDataStore.registerAdapter(VersionToStringAdapter)
        // Handle with shadow.
        val className = listOf(
            "org",
            "apache",
            "maven",
            "artifact",
            "versioning",
            "DefaultArtifactVersion"
        ).joinToString(".")
        if (ClassUtils.exists(className)) {
            ExternalDataStore.registerAdapter(MavenVersionToStringAdapter(className))
        }
    }

    override fun handleEnable() {
        this.logger.info("Scanning for conflicts...")
        val conflicts = ConflictFinder.searchForConflicts(this)
        for (conflict in conflicts) {
            this.logger.warning(conflict.conflictMessage)
        }
        if (conflicts.isNotEmpty()) {
            this.logger.warning(
                "You can fix the conflicts by either removing the conflicting plugins, " +
                        "or by asking on the support discord to have them patched!"
            )
            this.logger.warning(
                "Only remove potentially conflicting plugins if you see " +
                        "Loader Constraint Violation / LinkageError anywhere"
            )
        } else {
            this.logger.info("No conflicts found!")
        }

        CustomItemsManager.registerProviders() // Do it again here

        // Register events for ShopSellEvent
        for (integration in ShopManager.getRegisteredIntegrations()) {
            val listener = integration.sellEventAdapter
            if (listener != null) {
                this.eventManager.registerListener(listener)
            }
        }

        // Init FIS
        this.getProxy(FastItemStackFactoryProxy::class.java).create(ItemStack(Material.AIR)).unwrap()

        // Preload categorized persistent data keys
        profileHandler.initialize()

        // Init adventure
        if (!Prerequisite.HAS_PAPER.isMet) {
            bukkitAudiences = BukkitAudiences.create(this)
        }
    }

    override fun handleDisable() {
        this.logger.info("Saving player data...")
        val start = System.currentTimeMillis()
        profileHandler.save()
        this.logger.info("Saved player data! Took ${System.currentTimeMillis() - start}ms")
        Eco.get().adventure?.close()
    }

    override fun createTasks() {
        CollatedRunnable(this)

        this.scheduler.runLater(3) {
            profileHandler.migrateIfNeeded()
        }

        ProfileSaver(this, profileHandler).startTicking()

        this.scheduler.runTimer(
            this.configYml.getInt("display-frame-ttl").toLong(),
            this.configYml.getInt("display-frame-ttl").toLong(),
        ) { getProxy(PacketHandlerProxy::class.java).clearDisplayFrames() }

        if (this.configYml.getBool("playerflow")) {
            PlayerflowHandler(this.scheduler).startTicking()
        }
    }

    override fun handleAfterLoad() {
        CustomItemsManager.registerAllItems()
        CustomEntitiesManager.registerAllEntities()
        ShopManager.registerEcoProvider()
    }

    override fun loadIntegrationLoaders(): List<IntegrationLoader> {
        return listOf(
            // AntiGrief
            IntegrationLoader("IridiumSkyblock") { AntigriefManager.register(AntigriefIridiumSkyblock()) },
            IntegrationLoader("DeluxeCombat") { AntigriefManager.register(AntigriefDeluxeCombat()) },
            IntegrationLoader("SuperiorSkyblock2") { AntigriefManager.register(AntigriefSuperiorSkyblock2()) },
            IntegrationLoader("BentoBox") { AntigriefManager.register(AntigriefBentoBox()) },
            IntegrationLoader("WorldGuard") { AntigriefManager.register(AntigriefWorldGuard()) },
            IntegrationLoader("GriefPrevention") { AntigriefManager.register(AntigriefGriefPrevention()) },
            IntegrationLoader("FactionsUUID") { AntigriefManager.register(AntigriefFactionsUUID()) },
            IntegrationLoader("Towny") { AntigriefManager.register(AntigriefTowny()) },
            IntegrationLoader("Lands") { AntigriefManager.register(AntigriefLands(this)) },
            IntegrationLoader("Kingdoms") { AntigriefManager.register(AntigriefKingdoms()) },
            IntegrationLoader("RPGHorses") { AntigriefManager.register(AntigriefRPGHorses()) },
            IntegrationLoader("CrashClaim") { AntigriefManager.register(AntigriefCrashClaim()) },
            IntegrationLoader("CombatLogX") {
                val pluginManager = Bukkit.getPluginManager()
                val combatLogXPlugin = pluginManager.getPlugin("CombatLogX") ?: return@IntegrationLoader

                @Suppress("DEPRECATION")
                val pluginVersion = combatLogXPlugin.description.version
                if (pluginVersion.startsWith("10")) {
                    AntigriefManager.register(AntigriefCombatLogXV10())
                }
                if (pluginVersion.startsWith("11")) {
                    AntigriefManager.register(AntigriefCombatLogXV11())
                }
            },
            IntegrationLoader("PvPManager") { AntigriefManager.register(AntigriefPvPManager()) },
            IntegrationLoader("FabledSkyblock") { AntigriefManager.register(AntigriefFabledSkyBlock()) },

            // Anticheat
            IntegrationLoader("AAC5") { AnticheatManager.register(AnticheatAAC()) },
            IntegrationLoader("Matrix") { AnticheatManager.register(AnticheatMatrix()) },
            IntegrationLoader("NoCheatPlus") { AnticheatManager.register(AnticheatNCP()) },
            IntegrationLoader("Spartan") { AnticheatManager.register(AnticheatSpartan()) },
            IntegrationLoader("Vulcan") { AnticheatManager.register(AnticheatVulcan()) },
            IntegrationLoader("Alice") { AnticheatManager.register(AnticheatAlice()) },

            // Custom Entities
            IntegrationLoader("MythicMobs") { CustomEntitiesManager.register(CustomEntitiesMythicMobs()) },

            // Custom Items
            IntegrationLoader("Oraxen") { CustomItemsManager.register(CustomItemsOraxen(this)) },
            IntegrationLoader("ItemsAdder") { CustomItemsManager.register(CustomItemsItemsAdder()) },
            IntegrationLoader("HeadDatabase") { CustomItemsManager.register(CustomItemsHeadDatabase(this)) },
            IntegrationLoader("ExecutableItems") { CustomItemsManager.register(CustomItemsExecutableItems()) },
            IntegrationLoader("CustomCrafting") {
                CustomItemsManager.register(CustomItemsCustomCrafting())
                CraftingRecipeListener.registerValidator(CustomRecipeCustomCrafting())
            },
            IntegrationLoader("MythicMobs") { CustomItemsManager.register(CustomItemsMythicMobs(this)) },
            IntegrationLoader("Scyther") { CustomItemsManager.register(CustomItemsScyther()) },
            IntegrationLoader("Denizen") { CustomItemsManager.register(CustomItemsDenizen()) },

            // Shop
            IntegrationLoader("ShopGUIPlus") { ShopManager.register(ShopShopGuiPlus()) },
            IntegrationLoader("zShop") { ShopManager.register(ShopZShop()) },
            IntegrationLoader("DeluxeSellwands") { ShopManager.register(ShopDeluxeSellwands()) },
            IntegrationLoader("EconomyShopGUI") { ShopManager.register(ShopEconomyShopGUI()) },
            IntegrationLoader("EconomyShopGUI-Premium") { ShopManager.register(ShopEconomyShopGUI()) },

            // Hologram
            IntegrationLoader("HolographicDisplays") { HologramManager.register(HologramHolographicDisplays(this)) },
            IntegrationLoader("CMI") { HologramManager.register(HologramCMI()) },
            IntegrationLoader("DecentHolograms") { HologramManager.register(HologramDecentHolograms()) },
            //IntegrationLoader("GHolo") { HologramManager.register(HologramGHolo()) },

            // AFK
            IntegrationLoader("Essentials") { AFKManager.register(AFKIntegrationEssentials()) },
            IntegrationLoader("CMI") { AFKManager.register(AFKIntegrationCMI()) },

            // Economy
            IntegrationLoader("Vault") {
                val rsp = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)
                if (rsp != null) {
                    EconomyManager.register(EconomyVault(rsp.provider))
                }
            },

            // Price
            IntegrationLoader("UltraEconomy") {
                for (currency in UltraEconomy.getAPI().currencies) {
                    Prices.registerPriceFactory(PriceFactoryUltraEconomy(currency))
                }
            },
            IntegrationLoader("PlayerPoints") { Prices.registerPriceFactory(PriceFactoryPlayerPoints()) },
            IntegrationLoader("RoyaleEconomy") {
                for (currency in MultiCurrencyHandler.getCurrencies()) {
                    Prices.registerPriceFactory(PriceFactoryRoyaleEconomy(currency))
                }
            },

            // Placeholder
            IntegrationLoader("PlaceholderAPI") { PlaceholderManager.addIntegration(PlaceholderIntegrationPAPI()) },

            // Misc
            IntegrationLoader("mcMMO") { McmmoManager.register(McmmoIntegrationImpl()) },
            IntegrationLoader("Multiverse-Inventories") {
                this.eventManager.registerListener(
                    MultiverseInventoriesIntegration(this)
                )
            },
            IntegrationLoader("ModelEngine") { EntityLookupModelEngine.register() }
        )
    }

    override fun loadListeners(): List<Listener> {
        val listeners = mutableListOf(
            ArmorListener(),
            EntityDeathByEntityListeners(this),
            CraftingRecipeListener(),
            StackedRecipeListener(this),
            GUIListener(this),
            ArrowDataListener(this),
            ArmorChangeEventListeners(this),
            DataListener(this, profileHandler),
            PlayerBlockListener(this),
            ServerLocking
        )

        if (Prerequisite.HAS_PAPER.isMet) {
            listeners.add(PlayerJumpListenersPaper())
            listeners.add(NaturalExpGainListenersPaper())
        } else {
            listeners.add(PlayerJumpListenersSpigot())
            listeners.add(NaturalExpGainListenersSpigot())
        }

        return listeners
    }

    override fun loadPacketListeners(): List<PacketListener> {
        return this.getProxy(PacketHandlerProxy::class.java).getPacketListeners(this)
    }
}
