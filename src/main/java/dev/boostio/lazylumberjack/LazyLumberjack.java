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

package dev.boostio.lazylumberjack;

import com.github.retrooper.packetevents.PacketEvents;
import dev.boostio.lazylumberjack.managers.ConfigManager;
import dev.boostio.lazylumberjack.managers.LumberManager;
import dev.boostio.lazylumberjack.managers.StartupManager;
import dev.boostio.lazylumberjack.schedulers.IScheduler;
import dev.boostio.lazylumberjack.schedulers.Scheduler;
import io.github.retrooper.packetevents.bstats.Metrics;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LazyLumberjack extends JavaPlugin {
    private LumberManager logManager;
    private ConfigManager configManager;
    private IScheduler scheduler;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        scheduler = new Scheduler(this).getScheduler();
        logManager = new LumberManager(this);

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
