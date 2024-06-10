package dev.boostio.lazylumberjack.schedulers;

import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.schedulers.impl.BukkitScheduler;
import dev.boostio.lazylumberjack.schedulers.impl.FoliaScheduler;

public class Scheduler {

    private final LazyLumberjack plugin;

    public Scheduler(LazyLumberjack plugin) {
        this.plugin = plugin;
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public IScheduler getScheduler() {
        return isFolia() ? new FoliaScheduler(plugin) : new BukkitScheduler(plugin);
    }
}
