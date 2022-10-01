package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.gui.slot.functional.SlotHandler;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A slot loaded in from config.
 */
public class ConfigSlot extends CustomSlot {
    /**
     * The config of the slot.
     */
    private final Config config;

    /**
     * Cached handlers, for performance.
     */
    private final Map<String, List<CommandToDispatch>> handlers = new HashMap<>();

    /**
     * Create a new config slot.
     *
     * @param config The config.
     */
    public ConfigSlot(@NotNull final Config config) {
        this.config = config;

        SlotBuilder builder = Slot.builder(Items.lookup(config.getString("item")));

        for (ClickType clickType : ClickType.values()) {
            builder.onClick(
                    clickType,
                    dispatchCommandHandler(
                            clickType.name().toLowerCase(Locale.ROOT)
                                    .replace("_", "-")
                                    + "-click"
                    )
            );
        }

        init(builder.build());
    }

    /**
     * Create a slot handler for dispatching commands.
     *
     * @param configKey The config key.
     * @return The handler.
     */
    private SlotHandler dispatchCommandHandler(@NotNull final String configKey) {
        if (!handlers.containsKey(configKey)) {
            List<CommandToDispatch> commands = new ArrayList<>();

            for (String command : config.getStrings(configKey)) {
                if (command.startsWith("console:")) {
                    commands.add(new CommandToDispatch(
                            StringUtils.removePrefix("console:", command),
                            true
                    ));
                } else {
                    commands.add(new CommandToDispatch(
                            command,
                            false
                    ));
                }
            }

            handlers.put(configKey, commands);
        }

        List<CommandToDispatch> toDispatch = handlers.get(configKey);

        return (event, slot, menu) -> {
            Player player = (Player) event.getWhoClicked();

            for (CommandToDispatch dispatch : toDispatch) {
                dispatch.dispatch(player);
            }
        };
    }

    /**
     * Signifies a command to dispatch.
     *
     * @param command The command.
     * @param console If the command should be run as console.
     */
    private record CommandToDispatch(
            @NotNull String command,
            boolean console
    ) {
        /**
         * Dispatch command.
         *
         * @param player The player.
         */
        void dispatch(@NotNull final Player player) {
            if (console()) {
                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        command().replace("%player%", player.getName())
                );
            } else {
                Bukkit.dispatchCommand(
                        player,
                        command().replace("%player%", player.getName())
                );
            }
        }
    }
}
