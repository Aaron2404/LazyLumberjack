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

package dev.boostio.lazylumberjack.events;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.data.Settings;
import dev.boostio.lazylumberjack.managers.LumberManager;
import dev.boostio.lazylumberjack.schedulers.IScheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BreakBlock implements Listener {
    private final LumberManager logManager;
    private final Settings settings;
    private final IScheduler scheduler;

    public BreakBlock(LazyLumberjack plugin) {
        this.logManager = plugin.getLogManager();
        this.settings = plugin.getConfigManager().getSettings();
        this.scheduler = plugin.getScheduler();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAsyncBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!player.isSneaking()) return;

        ItemStack handItem = logManager.getItemInMainHand(player);
        if (!logManager.isAxe(handItem.getType())) return;

        Block block = event.getBlock();
        if (!logManager.isLog(block.getType())) return;

        scheduler.runRegionTask(block.getLocation(), (o) -> {
            if (!player.hasPermission("LazyLumberjack.Use")) return;

            List<Block> relatedLogs = logManager.findRelatedLogs(block, 0, 0);

            if (relatedLogs.isEmpty()) {
                return;
            }

            Material logMaterial = relatedLogs.get(0).getType();

            long delay = settings.getAnimations().getSlowBreak().getDelay().getRealisticSpeeds().isEnabled() ?
                    logManager.calculateRealisticDelay(relatedLogs.size(), handItem, player) :
                    logManager.calculateDelay(relatedLogs.size());

            logManager.processLogs(relatedLogs, delay);
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && settings.getHelpers().isPlaceSapling()) {
                logManager.plantSaplingsAfterDelay(relatedLogs, logMaterial, delay);
            }
        });
    }
}