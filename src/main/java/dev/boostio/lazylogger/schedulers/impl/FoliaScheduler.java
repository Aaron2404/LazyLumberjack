package dev.boostio.lazylogger.schedulers.impl;

import dev.boostio.lazylogger.LazyLogger;
import dev.boostio.lazylogger.schedulers.Scheduler;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class FoliaScheduler implements Scheduler {

    private final LazyLogger plugin;

    public FoliaScheduler(LazyLogger plugin) {
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
    public void runAsyncTaskAtFixedRate(@NotNull Consumer<Object> task, long delay, long period, @NotNull TimeUnit timeUnit) {
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (o) -> task.accept(null), delay, period, timeUnit);
    }
}
