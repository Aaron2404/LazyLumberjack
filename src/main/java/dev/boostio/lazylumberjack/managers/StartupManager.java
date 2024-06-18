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

package dev.boostio.lazylumberjack.managers;

import com.github.retrooper.packetevents.PacketEvents;
import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.events.BreakBlock;
import dev.boostio.lazylumberjack.packetlisteners.BlockBreakAnimation;

/**
 * Manages the start-up processes of the plugin, including the registration of commands and events.
 */
public class StartupManager {

    private final LazyLumberjack plugin;

    /**
     * Creates a new StartUpManager instance.
     *
     * @param plugin the instance of the plugin class.
     */
    public StartupManager(LazyLumberjack plugin) {
        this.plugin = plugin;

        load();
    }

    /**
     * Calls methods to register commands and events.
     */
    private void load() {
        registerCommands();
        registerEvents();
    }

    /**
     * Registers commands related to the plugin.
     */
    private void registerCommands() {
    }

    /**
     * Registers events related to the plugin.
     */
    private void registerEvents() {
        PacketEvents.getAPI().getEventManager().registerListener(new BlockBreakAnimation());
        plugin.getServer().getPluginManager().registerEvents(new BreakBlock(this.plugin), plugin);
    }
}