package dev.boostio.lazylumberjack.schedulers.impl;

import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.schedulers.Scheduler;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class BukkitScheduler implements Scheduler {

    private final LazyLumberjack plugin;

    public BukkitScheduler(LazyLumberjack plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsyncTask(Consumer<Object> task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> task.accept(null));
    }

    @Override
    public void runAsyncTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> task.accept(null), convertTimeToTicks(delay, timeUnit));
    }

    @Override
    public void runTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> task.accept(null), convertTimeToTicks(delay, timeUnit));
    }

    @Override
    public void runAsyncTaskAtFixedRate(@NotNull Consumer<Object> task, long delay, long period, @NotNull TimeUnit timeUnit) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> task.accept(null), convertTimeToTicks(delay, timeUnit), convertTimeToTicks(period, timeUnit));
    }

    private long convertTimeToTicks(long time, TimeUnit timeUnit) {
        return timeUnit.toMillis(time) / 50;
    }
}
