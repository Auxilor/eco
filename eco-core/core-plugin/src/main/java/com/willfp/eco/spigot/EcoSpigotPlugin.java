package com.willfp.eco.spigot;

import com.willfp.eco.core.AbstractPacketAdapter;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.AbstractCommand;
import com.willfp.eco.core.config.base.LangYml;
import com.willfp.eco.core.data.Data;
import com.willfp.eco.core.display.Display;
import com.willfp.eco.core.display.DisplayModule;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.eco.core.integrations.anticheat.AnticheatManager;
import com.willfp.eco.core.integrations.antigrief.AntigriefManager;
import com.willfp.eco.core.integrations.mcmmo.McmmoManager;
import com.willfp.eco.internal.InternalInterfacing;
import com.willfp.eco.proxy.proxies.BlockBreakProxy;
import com.willfp.eco.proxy.proxies.SkullProxy;
import com.willfp.eco.proxy.proxies.TridentStackProxy;
import com.willfp.eco.spigot.config.DataJson;
import com.willfp.eco.spigot.display.PacketAutoRecipe;
import com.willfp.eco.spigot.display.PacketChat;
import com.willfp.eco.spigot.display.PacketOpenWindowMerchant;
import com.willfp.eco.spigot.display.PacketSetCreativeSlot;
import com.willfp.eco.spigot.display.PacketSetSlot;
import com.willfp.eco.spigot.display.PacketWindowItems;
import com.willfp.eco.spigot.drops.CollatedRunnable;
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
import com.willfp.eco.spigot.integrations.antigrief.AntigriefCombatLogX;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefFactionsUUID;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefGriefPrevention;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefKingdoms;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefLands;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefTowny;
import com.willfp.eco.spigot.integrations.antigrief.AntigriefWorldGuard;
import com.willfp.eco.spigot.integrations.mcmmo.McmmoIntegrationImpl;
import com.willfp.eco.spigot.recipes.ShapedRecipeListener;
import com.willfp.eco.util.BlockUtils;
import com.willfp.eco.util.SkullUtils;
import com.willfp.eco.util.TridentUtils;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EcoSpigotPlugin extends EcoPlugin {
    /**
     * Instance of eco.
     */
    @Getter
    private static EcoSpigotPlugin instance;

    /**
     * data.json.
     */
    private final DataJson dataJson;

    /**
     * Create a new instance of eco.
     */
    public EcoSpigotPlugin() {
        super("eco", 87955, 10043, "com.willfp.eco.proxy", "&a");
        instance = this;
        Display.setFinalizeKey(this.getNamespacedKeyFactory().create("finalized"));

        SkullProxy skullProxy = InternalProxyUtils.getProxy(SkullProxy.class);
        SkullUtils.initialize(skullProxy::setSkullTexture);

        BlockBreakProxy blockBreakProxy = InternalProxyUtils.getProxy(BlockBreakProxy.class);
        BlockUtils.initialize(blockBreakProxy::breakBlock);

        TridentStackProxy tridentStackProxy = InternalProxyUtils.getProxy(TridentStackProxy.class);
        TridentUtils.initialize(tridentStackProxy::getTridentStack);

        this.dataJson = new DataJson(this);
        Data.init(this.dataJson);

        InternalInterfacing.setInterfacing(new InternalInterfacing() {
            @Override
            public LangYml getLang() {
                return EcoSpigotPlugin.getInstance().getLangYml();
            }
        });
    }

    @Override
    public void enable() {
        new CollatedRunnable(this);
    }

    @Override
    public void disable() {
        try {
            Data.save(this.dataJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {

    }

    @Override
    public void onReload() {
        new CollatedRunnable(this);
    }

    @Override
    public void postLoad() {

    }

    @Override
    public List<IntegrationLoader> getIntegrationLoaders() {
        return Arrays.asList(
                // AntiGrief
                new IntegrationLoader("WorldGuard", () -> AntigriefManager.register(new AntigriefWorldGuard())),
                new IntegrationLoader("GriefPrevention", () -> AntigriefManager.register(new AntigriefGriefPrevention())),
                new IntegrationLoader("FactionsUUID", () -> AntigriefManager.register(new AntigriefFactionsUUID())),
                new IntegrationLoader("Towny", () -> AntigriefManager.register(new AntigriefTowny())),
                new IntegrationLoader("Lands", () -> AntigriefManager.register(new AntigriefLands(this))),
                new IntegrationLoader("Kingdoms", () -> AntigriefManager.register(new AntigriefKingdoms())),
                new IntegrationLoader("CombatLogX", () -> AntigriefManager.register(new AntigriefCombatLogX())),

                // Anticheat
                new IntegrationLoader("AAC5", () -> AnticheatManager.register(this, new AnticheatAAC())),
                new IntegrationLoader("Matrix", () -> AnticheatManager.register(this, new AnticheatMatrix())),
                new IntegrationLoader("NoCheatPlus", () -> AnticheatManager.register(this, new AnticheatNCP())),
                new IntegrationLoader("Spartan", () -> AnticheatManager.register(this, new AnticheatSpartan())),
                new IntegrationLoader("Vulcan", () -> AnticheatManager.register(this, new AnticheatVulcan())),

                // Misc
                new IntegrationLoader("mcMMO", () -> McmmoManager.register(new McmmoIntegrationImpl()))
        );
    }

    @Override
    public List<AbstractCommand> getCommands() {
        return new ArrayList<>();
    }

    @Override
    public List<AbstractPacketAdapter> getPacketAdapters() {
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
    public List<Listener> getListeners() {
        return Arrays.asList(
                new NaturalExpGainListeners(),
                new ArmorListener(),
                new DispenserArmorListener(),
                new EntityDeathByEntityListeners(this),
                new ShapedRecipeListener(this),
                new PlayerJumpListeners(),
                new GUIListener(this)
        );
    }

    @Override
    public List<Class<?>> getUpdatableClasses() {
        return new ArrayList<>();
    }

    @Override
    @Nullable
    protected DisplayModule createDisplayModule() {
        return null;
    }
}
