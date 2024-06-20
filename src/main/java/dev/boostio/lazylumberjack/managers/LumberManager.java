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
import dev.boostio.lazylumberjack.services.BlockService;
import dev.boostio.lazylumberjack.services.MaterialService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LumberManager {
    private final BlockService blockService;
    private final MaterialService materialService;
    private final Settings settings;

    /**
     * Constructor for LumberManager.
     *
     * @param plugin the plugin instance.
     */
    public LumberManager(LazyLumberjack plugin) {
        this.materialService = new MaterialService(plugin);
        this.settings = plugin.getConfigManager().getSettings();
        this.blockService = new BlockService(plugin, materialService);
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
     * @param logs  the list of logs.
     * @param delay the delay between animations.
     */
    public void processLogs(List<Block> logs, long delay) {
        blockService.processLogs(logs, delay);
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
     * Calculates the delay for breaking logs based on various factors.
     * The delay is influenced by the size of the tree, the type of axe used, the efficiency level of the axe, and any active player effects.
     * The delay is calculated using the formula: originalDelay * axeFactor * enchantmentFactor * effectFactor
     * The result is then compared with 1, and the maximum value is returned.
     * This ensures that the delay is at least 1 millisecond.
     *
     * @param size the size of the tree (i.e., the number of logs).
     * @param axe the ItemStack representing the axe used by the player.
     * @param player the Player who is breaking the logs.
     * @return the calculated delay in milliseconds.
     */
    public long calculateRealisticDelay(int size, ItemStack axe, Player player) {
        double axeFactor = materialService.getAxeSpeedFactor(axe.getType());
        double enchantmentFactor = materialService.getEnchantmentFactor(axe.getEnchantmentLevel(Enchantment.EFFICIENCY));
        double effectFactor = materialService.getEffectFactor(player);
        long originalDelay = calculateDelay(size);
        long calculatedDelay = (long) Math.max(originalDelay * axeFactor * enchantmentFactor * effectFactor, 1);

        if(settings.getAnimations().getSlowBreak().getDelay().getRealisticSpeeds().isDebug()) {
            Bukkit.broadcast(debugRealisticDelayComponent(axeFactor, enchantmentFactor, effectFactor, originalDelay, calculatedDelay), "LazyLumberjack.Debug");
        }

        return calculatedDelay;
    }

    private Component debugRealisticDelayComponent(double axeFactor, double enchantmentFactor, double effectFactor, long originalDelay, long calculatedDelay) {

        return Component.text()
                .append(Component.text("[DEBUG] delay Stats", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .appendNewline()
                .append(Component.text("\n\u25cf Axe factor: ", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(axeFactor, NamedTextColor.AQUA))
                .append(Component.text("\n\u25cf Enchanted factor: ", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(Math.round(enchantmentFactor * 100.0) / 100.0, NamedTextColor.AQUA))
                .append(Component.text("\n\u25cf Effect factor: ", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(effectFactor, NamedTextColor.AQUA))
                .append(Component.text("\n\u25cf Original delay: ", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(originalDelay, NamedTextColor.AQUA))
                .append(Component.text("\n\u25cf Calculated delay: ", NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text(calculatedDelay, NamedTextColor.AQUA))
                .build();
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
    public ItemStack getItemInMainHand(Player player) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if (user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
            return player.getInventory().getItemInMainHand();
        } else {
            return player.getInventory().getItemInHand();
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
