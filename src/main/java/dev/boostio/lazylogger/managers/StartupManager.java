package dev.boostio.lazylogger.managers;

import dev.boostio.lazylogger.LazyLogger;
import dev.boostio.lazylogger.events.AsyncBreakBlock;

/**
 * Manages the start-up processes of the plugin, including the registration of commands and events.
 */
public class StartupManager {

    private final LazyLogger plugin;

    /**
     * Creates a new StartUpManager instance.
     *
     * @param plugin the instance of the plugin class.
     */
    public StartupManager(LazyLogger plugin) {
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
        plugin.getServer().getPluginManager().registerEvents(new AsyncBreakBlock(this.plugin), plugin);
    }
}