package com.willfp.eco.spigot;

import com.willfp.eco.core.AbstractPacketAdapter;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.display.Display;
import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.eco.core.integrations.anticheat.AnticheatManager;
import com.willfp.eco.core.integrations.antigrief.AntigriefManager;
import com.willfp.eco.core.integrations.customitems.CustomItemsManager;
import com.willfp.eco.core.integrations.mcmmo.McmmoManager;
import com.willfp.eco.proxy.BlockBreakProxy;
import com.willfp.eco.proxy.SkullProxy;
import com.willfp.eco.spigot.arrows.ArrowDataListener;
import com.willfp.eco.spigot.display.PacketAutoRecipe;
import com.willfp.eco.spigot.display.PacketChat;
import com.willfp.eco.spigot.display.PacketOpenWindowMerchant;
import com.willfp.eco.spigot.display.PacketSetCreativeSlot;
import com.willfp.eco.spigot.display.PacketSetSlot;
import com.willfp.eco.spigot.display.PacketWindowItems;
import com.willfp.eco.spigot.drops.CollatedRunnable;
import com.willfp.eco.spigot.eventlisteners.ArmorChangeEventListeners;
import com.willfp.eco.spigot.eventlisteners.ArmorListener;
import com.willfp.eco.spigot.eventlisteners.DispenserArmorListener;
import com.willfp.eco.spigot.eventlisteners.EntityDeathByEntityListeners;
import com.willfp.eco.spigot.eventlisteners.NaturalExpGainListeners;
import com.willfp.eco.spigot.eventlisteners.PlayerJumpListeners;
import com.willfp.eco.spigot.gui.GUIListener;
import com.willfp.eco.spigot.integrations.anticheat.AnticheatAAC;
import com.willfp.eco.spigot.integrations.anticheat.AnticheatMatrix;
import com.willfp.eco.spigot.integrations.anticheat.AnticheatNCP;
import com.willfp.eco.spigot.integrations.anticheat.AnticheatSpartan;
import com.willfp.eco.spigot.integrations.anticheat.AnticheatVulcan;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefCombatLogXV10;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefCombatLogXV11;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefFactionsUUID;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefGriefPrevention;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefKingdoms;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefLands;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefTowny;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefWorldGuard;
import com.willfp.eco.spigot.integrations.customitems.CustomItemsOraxen;
import com.willfp.eco.spigot.integrations.mcmmo.McmmoIntegrationImpl;
import com.willfp.eco.spigot.recipes.ShapedRecipeListener;
import com.willfp.eco.util.BlockUtils;
import com.willfp.eco.util.SkullUtils;
import com.willfp.eco.util.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class EcoSpigotPlugin extends EcoPlugin {
    @Getter
    private static EcoSpigotPlugin instance;

    public EcoSpigotPlugin() {
        super(87955, 10043, "com.willfp.eco.proxy", "&a");
        instance = this;
        Display.setFinalizeKey(this.getNamespacedKeyFactory().create("finalized"));

        SkullProxy skullProxy = this.getProxy(SkullProxy.class);
        SkullUtils.initialize(skullProxy::setSkullTexture);

        BlockBreakProxy blockBreakProxy = this.getProxy(BlockBreakProxy.class);
        BlockUtils.initialize(blockBreakProxy::breakBlock);


        // Run static init with CIS.
        Inventory inventory = Bukkit.createInventory(null, 9);
        inventory.addItem(new ItemStack(Material.ACACIA_DOOR));
        ItemStack testItem = inventory.getItem(0);
        assert testItem != null;
        FastItemStack.wrap(testItem);
        List<String> testLore = Collections.singletonList(StringUtils.format("&e&lTest&r &a&lTest!&r <gradient:000000>123456789</gradient:ffffff>"));

        FastItemStack.wrap(testItem).setLore(testLore);

        Bukkit.getLogger().info(FastItemStack.wrap(testItem).getLore().toString());
    }

    @Override
    protected void handleEnable() {
        new CollatedRunnable(this);
    }

    @Override
    protected void handleReload() {
        new CollatedRunnable(this);
    }

    @Override
    protected void handleAfterLoad() {
        CustomItemsManager.registerAllItems();
    }

    @Override
    protected List<IntegrationLoader> loadIntegrationLoaders() {
        return Arrays.asList(
                // AntiGrief
                new IntegrationLoader("WorldGuard", () -> AntigriefManager.register(new AntigriefWorldGuard())),
                new IntegrationLoader("GriefPrevention", () -> AntigriefManager.register(new AntigriefGriefPrevention())),
                new IntegrationLoader("FactionsUUID", () -> AntigriefManager.register(new AntigriefFactionsUUID())),
                new IntegrationLoader("Towny", () -> AntigriefManager.register(new AntigriefTowny())),
                new IntegrationLoader("Lands", () -> AntigriefManager.register(new AntigriefLands(this))),
                new IntegrationLoader("Kingdoms", () -> AntigriefManager.register(new AntigriefKingdoms())),
                new IntegrationLoader("CombatLogX", () -> {
                    PluginManager pluginManager = Bukkit.getPluginManager();
                    Plugin combatLogXPlugin = pluginManager.getPlugin("CombatLogX");

                    if (combatLogXPlugin == null) {
                        return;
                    }

                    String pluginVersion = combatLogXPlugin.getDescription().getVersion();
                    if (pluginVersion.startsWith("10")) {
                        AntigriefManager.register(new AntigriefCombatLogXV10());
                    }

                    if (pluginVersion.startsWith("11")) {
                        AntigriefManager.register(new AntigriefCombatLogXV11());
                    }
                }),

                // Anticheat
                new IntegrationLoader("AAC5", () -> AnticheatManager.register(this, new AnticheatAAC())),
                new IntegrationLoader("Matrix", () -> AnticheatManager.register(this, new AnticheatMatrix())),
                new IntegrationLoader("NoCheatPlus", () -> AnticheatManager.register(this, new AnticheatNCP())),
                new IntegrationLoader("Spartan", () -> AnticheatManager.register(this, new AnticheatSpartan())),
                new IntegrationLoader("Vulcan", () -> AnticheatManager.register(this, new AnticheatVulcan())),

                // Custom Items
                new IntegrationLoader("Oraxen", () -> CustomItemsManager.register(new CustomItemsOraxen())),

                // Misc
                new IntegrationLoader("mcMMO", () -> McmmoManager.register(new McmmoIntegrationImpl()))
        );
    }

    @Override
    protected List<AbstractPacketAdapter> loadPacketAdapters() {
        List<AbstractPacketAdapter> adapters = new ArrayList<>(Arrays.asList(
                new PacketAutoRecipe(this),
                new PacketChat(this),
                new PacketSetCreativeSlot(this),
                new PacketSetSlot(this),
                new PacketWindowItems(this)
        ));

        if (!this.getConfigYml().getBool("disable-display-on-villagers")) {
            adapters.add(new PacketOpenWindowMerchant(this));
        }

        return adapters;
    }

    @Override
    protected List<Listener> loadListeners() {
        return Arrays.asList(
                new NaturalExpGainListeners(),
                new ArmorListener(),
                new DispenserArmorListener(),
                new EntityDeathByEntityListeners(this),
                new ShapedRecipeListener(this),
                new PlayerJumpListeners(),
                new GUIListener(this),
                new ArrowDataListener(this),
                new ArmorChangeEventListeners(this)
        );
    }
}
