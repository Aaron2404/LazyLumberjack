/*
 * This file is part of LazyLumberjack - https://github.com/Aaron2404/LazyLumberjack
 * Copyright (C) 2024 Aaron and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.boostio.lazylumberjack.schedulers.impl;

import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.schedulers.IScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class BukkitScheduler implements IScheduler {

    private final LazyLumberjack plugin;

    public BukkitScheduler(LazyLumberjack plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsyncTask(@NotNull Consumer<Object> task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> task.accept(null));
    }

    @Override
    public void runAsyncTaskDelayed(@NotNull Consumer<Object> task, long delay, @NotNull TimeUnit timeUnit) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> task.accept(null), convertTimeToTicks(delay, timeUnit));
    }

    @Override
    public void runTask(Location location, @NotNull Consumer<Object> task) {
        Bukkit.getScheduler().runTask(plugin, () -> task.accept(null));
    }

    @Override
    public void runRegionTask(@NotNull Location location, @NotNull Consumer<Object> task) {
        Bukkit.getScheduler().runTask(plugin, () -> task.accept(null));
    }

    @Override
    public void runRegionTaskDelayed(@NotNull Location location, @NotNull Consumer<Object> task, long delay, @NotNull TimeUnit timeUnit) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> task.accept(null), convertTimeToTicks(delay, timeUnit));
    }

    @Override
    public void runTaskDelayed(@NotNull Location location, @NotNull Consumer<Object> task, long delay, @NotNull TimeUnit timeUnit) {
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
