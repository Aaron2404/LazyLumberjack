package dev.boostio.lazylumberjack.managers;

import com.github.retrooper.packetevents.PacketEvents;
import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.events.BreakBlock;
import dev.boostio.lazylumberjack.packetlisteners.BlockBreakAnimation;

/**
 * Manages the start-up processes of the plugin, including the registration of commands and events.
 */
public class StartupManager {

    private final LazyLumberjack plugin;

    /**
     * Creates a new StartUpManager instance.
     *
     * @param plugin the instance of the plugin class.
     */
    public StartupManager(LazyLumberjack plugin) {
        this.plugin = plugin;

        load();
    }

    /**
     * Calls methods to register commands and events.
     */
    private void load() {
        registerCommands();
        registerEvents();
    }

    /**
     * Registers commands related to the plugin.
     */
    private void registerCommands() {
    }

    /**
     * Registers events related to the plugin.
     */
    private void registerEvents() {
        PacketEvents.getAPI().getEventManager().registerListener(new BlockBreakAnimation());
        plugin.getServer().getPluginManager().registerEvents(new BreakBlock(this.plugin), plugin);
    }
}