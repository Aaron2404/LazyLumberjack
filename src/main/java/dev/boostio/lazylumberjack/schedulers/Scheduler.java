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
