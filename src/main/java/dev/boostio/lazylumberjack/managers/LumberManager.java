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
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.data.Settings;
import dev.boostio.lazylumberjack.schedulers.IScheduler;
import dev.boostio.lazylumberjack.services.BlockService;
import dev.boostio.lazylumberjack.services.MaterialService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LumberManager {
    private final IScheduler scheduler;
    private final BlockService blockService;
    private final MaterialService materialService;
    private final Settings settings;

    /**
     * Constructor for LumberManager.
     *
     * @param plugin the plugin instance.
     */
    public LumberManager(LazyLumberjack plugin) {
        this.scheduler = plugin.getScheduler();
        this.materialService = new MaterialService();
        this.settings = plugin.getConfigManager().getSettings();
        this.blockService = new BlockService(plugin.getConfigManager(), scheduler, materialService);
    }

    /**
     * Recursively find logs related to the given log by checking each side and recursively moving down the blocks.
     *
     * @param block             the block to start from.
     * @param consecutiveLeaves the number of consecutive leaves found.
     * @param airBlocks         the number of air blocks found.
     */
    public List<Block> findRelatedLogs(Block block, int consecutiveLeaves, int airBlocks) {
        return blockService.findRelatedLogs(block, new ArrayList<>(), new HashSet<>(), consecutiveLeaves, airBlocks);
    }

    /**
     * Processes logs by breaking them with animation.
     *
     * @param user  the user.
     * @param logs  the list of logs.
     * @param delay the delay between animations.
     */
    public void processLogs(User user, List<Block> logs, long delay) {
        blockService.processLogs(user, logs, delay);
    }

    /**
     * Calculates the delay for breaking logs based on the size of the tree.
     * The delay is calculated using the formula: baseDelay / (1 + size) ^ speedFactor
     * The result is then compared with 1, and the maximum value is returned.
     * This ensures that the delay is at least 1 millisecond.
     *
     * @param size the size of the tree (i.e., the number of logs).
     * @return the calculated delay in milliseconds.
     */
    public long calculateDelay(int size) {
        return (long) Math.max(settings.getAnimations().getSlowBreak().getDelay().getBaseDelay() / Math.pow(1 + size, settings.getAnimations().getSlowBreak().getDelay().getSpeedFactor()), 1);
    }

    /**
     * Plants saplings after a delay.
     *
     * @param logs                     the list of logs.
     * @param logMaterial              the material of the logs.
     * @param blockBreakAnimationDelay the delay between animations.
     */
    public void plantSaplingsAfterDelay(List<Block> logs, Material logMaterial, long blockBreakAnimationDelay) {
        blockService.plantSaplingsAfterDelay(logs, logMaterial, blockBreakAnimationDelay);
    }

    /**
     * Gets the item in the player's main hand.
     *
     * @param player the player.
     * @return the material of the item in the player's main hand.
     */
    public Material getItemInMainHand(Player player) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if (user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
            return player.getInventory().getItemInMainHand().getType();
        } else {
            return player.getInventory().getItemInHand().getType();
        }
    }

    /**
     * Checks if the given material is an axe.
     *
     * @param material the material to check.
     * @return true if the material is an axe, false otherwise.
     */
    public boolean isAxe(Material material) {
        return materialService.isAxe(material);
    }

    /**
     * Checks if the given material is a log.
     *
     * @param material the material to check.
     * @return true if the material is a log, false otherwise.
     */
    public boolean isLog(Material material) {
        return materialService.isLog(material);
    }
}
