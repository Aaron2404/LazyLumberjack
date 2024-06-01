package dev.boostio.lazylogger;

import com.github.retrooper.packetevents.PacketEvents;
import dev.boostio.lazylogger.managers.LogManager;
import dev.boostio.lazylogger.managers.StartupManager;
import dev.boostio.lazylogger.schedulers.Scheduler;
import dev.boostio.lazylogger.schedulers.impl.BukkitScheduler;
import dev.boostio.lazylogger.schedulers.impl.FoliaScheduler;
import io.github.retrooper.packetevents.bstats.Metrics;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LazyLogger extends JavaPlugin {
    private LogManager logManager;
    private Scheduler scheduler;

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onEnable() {
        logManager = new LogManager();
        scheduler = isFolia() ? new FoliaScheduler(this) : new BukkitScheduler(this);

        PacketEvents.getAPI().init();

        new StartupManager(this);

        enableBStats();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        getLogger().info("Plugin has been uninitialized!");
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
