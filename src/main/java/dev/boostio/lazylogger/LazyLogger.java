package dev.boostio.lazylogger;

import org.bukkit.plugin.java.JavaPlugin;

public final class LazyLogger extends JavaPlugin {

    @Override
    public void onEnable() {
       getLogger().warning("This is a warning message");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
