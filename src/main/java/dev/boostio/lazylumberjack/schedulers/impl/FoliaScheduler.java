package dev.boostio.lazylumberjack.schedulers.impl;

import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.schedulers.IScheduler;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class FoliaScheduler implements IScheduler {

    private final LazyLumberjack plugin;

    public FoliaScheduler(LazyLumberjack plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsyncTask(Consumer<Object> task) {
        Bukkit.getAsyncScheduler().runNow(plugin, (o) -> task.accept(null));
    }

    @Override
    public void runAsyncTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit) {
        Bukkit.getAsyncScheduler().runDelayed(plugin, (o) -> task.accept(null), delay, timeUnit);
    }

    @Override
    public void runTask(Consumer<Object> task) {
        Bukkit.getScheduler().runTask(plugin, (o) -> task.accept(null));
    }

    @Override
    public void runTaskDelayed(Consumer<Object> task, long delay, TimeUnit timeUnit) {
        Bukkit.getScheduler().runTaskLater(plugin, (o) -> task.accept(null), convertTimeToTicks(delay, timeUnit));
    }

    @Override
    public void runAsyncTaskAtFixedRate(@NotNull Consumer<Object> task, long delay, long period, @NotNull TimeUnit timeUnit) {
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (o) -> task.accept(null), delay, period, timeUnit);
    }

    private long convertTimeToTicks(long time, TimeUnit timeUnit) {
        return timeUnit.toMillis(time) / 50;
    }
}
