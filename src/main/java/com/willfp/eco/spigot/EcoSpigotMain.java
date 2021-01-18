package com.willfp.eco.spigot;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.integrations.IntegrationLoader;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.protocollib.AbstractPacketAdapter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class EcoSpigotMain extends AbstractEcoPlugin {
    /**
     * Create a new instance of eco.
     */
    public EcoSpigotMain() {
        super("eco", 87955, 10043, "com.willfp.eco.proxy", "&a");
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void load() {

    }

    @Override
    public void onReload() {

    }

    @Override
    public void postLoad() {

    }

    @Override
    public List<IntegrationLoader> getIntegrationLoaders() {
        return new ArrayList<>();
    }

    @Override
    public List<AbstractCommand> getCommands() {
        return new ArrayList<>();
    }

    @Override
    public List<AbstractPacketAdapter> getPacketAdapters() {
        return new ArrayList<>();
    }

    @Override
    public List<Listener> getListeners() {
        return new ArrayList<>();
    }

    @Override
    public List<Class<?>> getUpdatableClasses() {
        return new ArrayList<>();
    }
}
