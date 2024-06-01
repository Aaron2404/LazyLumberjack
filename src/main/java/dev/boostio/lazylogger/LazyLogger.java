package dev.boostio.lazylogger;

import com.github.retrooper.packetevents.PacketEvents;
import dev.boostio.lazylogger.managers.LogManager;
import dev.boostio.lazylogger.managers.StartupManager;
import lombok.Getter;
import io.github.retrooper.packetevents.bstats.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LazyLogger extends JavaPlugin {
    private LogManager logManager;

    @Override
    public void onEnable() {
       logManager = new LogManager();

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
