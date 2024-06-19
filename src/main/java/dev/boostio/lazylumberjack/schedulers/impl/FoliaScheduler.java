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

public final class FoliaScheduler implements IScheduler {

    private final LazyLumberjack plugin;

    public FoliaScheduler(LazyLumberjack plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runAsyncTask(@NotNull Consumer<Object> task) {
        Bukkit.getAsyncScheduler().runNow(plugin, (o) -> task.accept(null));
    }

    @Override
    public void runAsyncTaskDelayed(@NotNull Consumer<Object> task, long delay, @NotNull TimeUnit timeUnit) {
        Bukkit.getAsyncScheduler().runDelayed(plugin, (o) -> task.accept(null), delay, timeUnit);
    }

    @Override
    public void runTask(@NotNull Consumer<Object> task) {
        Bukkit.getScheduler().runTask(plugin, (o) -> task.accept(null));
    }

    @Override
    public void runRegionTaskDelayed(@NotNull Location location, @NotNull Consumer<Object> task, long delay, @NotNull TimeUnit timeUnit) {
        Bukkit.getRegionScheduler().runDelayed(plugin, location, (o) -> task.accept(null), delay);
    }

    @Override
    public void runTaskDelayed(@NotNull Consumer<Object> task, long delay, @NotNull TimeUnit timeUnit) {
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
