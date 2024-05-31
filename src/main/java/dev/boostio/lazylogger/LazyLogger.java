package dev.boostio.lazylogger;

import dev.boostio.lazylogger.managers.LogManager;
import dev.boostio.lazylogger.managers.StartupManager;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LazyLogger extends JavaPlugin {
    private LogManager logManager;

    @Override
    public void onEnable() {
       logManager = new LogManager();

        new StartupManager(this);

       enableBStats();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Enable the bStats plugin statistics system.
     * This method catches and logs any exceptions that might be thrown during the enabling process.
     */
    private void enableBStats() {
        try {
            new Metrics(this, 22088);
        } catch (Exception e) {
            getLogger().warning("Something went wrong while enabling bStats.\n" + e.getMessage());
        }
    }
}
