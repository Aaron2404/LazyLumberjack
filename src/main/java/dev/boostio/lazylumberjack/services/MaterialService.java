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

package dev.boostio.lazylumberjack.services;

import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.data.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class MaterialService {
    private final Settings settings;

    public MaterialService(LazyLumberjack plugin) {
        this.settings = plugin.getConfigManager().getSettings();
    }


    /**
     * Checks if the given material is a log.
     *
     * @param material the material to check.
     * @return true if the material is a log, false otherwise.
     */
    public boolean isLog(Material material) {
        return material.name().contains("LOG");
    }

    /**
     * Checks if the given material is a leaf.
     *
     * @param material the material to check.
     * @return true if the material is a leaf, false otherwise.
     */
    public boolean isLeaf(Material material) {
        return material.name().contains("LEAVES");
    }

    /**
     * Checks if the given material is an axe.
     *
     * @param material the material to check.
     * @return true if the material is an axe, false otherwise.
     */
    public boolean isAxe(Material material) {
        return material.name().contains("AXE");
    }

    /**
     * Checks if the given material is dirt or podzol.
     *
     * @param material the material to check.
     * @return true if the material is dirt or podzol, false otherwise.
     */
    public boolean isDirtOrPodzol(Material material) {
        return material == Material.DIRT || material == Material.PODZOL;
    }

    /**
     * Gets the corresponding sapling type for the given log type.
     *
     * @param logType the log type.
     * @return the corresponding sapling type.
     */
    public Material getSaplingFromLog(Material logType) {
        switch (logType) {
            case OAK_LOG:
                return Material.OAK_SAPLING;
            case SPRUCE_LOG:
                return Material.SPRUCE_SAPLING;
            case BIRCH_LOG:
                return Material.BIRCH_SAPLING;
            case JUNGLE_LOG:
                return Material.JUNGLE_SAPLING;
            case ACACIA_LOG:
                return Material.ACACIA_SAPLING;
            case DARK_OAK_LOG:
                return Material.DARK_OAK_SAPLING;
            default:
                return Material.AIR;
        }
    }


    public double getAxeSpeedFactor(Material axeMaterial) {
        Settings.Animations.SlowBreak.Delay.RealisticSpeeds realisticSpeeds = settings.getAnimations().getSlowBreak().getDelay().getRealisticSpeeds();
        switch (axeMaterial) {
            case WOODEN_AXE:
                return realisticSpeeds.getWoodenAxeFactor();
            case STONE_AXE:
                return realisticSpeeds.getStoneAxeFactor();
            case IRON_AXE:
                return realisticSpeeds.getIronAxeFactor();
            case DIAMOND_AXE:
                return realisticSpeeds.getDiamondAxeFactor();
            case NETHERITE_AXE:
                return realisticSpeeds.getNetheriteAxeFactor();
            case GOLDEN_AXE:
                return realisticSpeeds.getGoldenAxeFactor();
            default:
                return 3.0; // default speed factor for non-axe tools
        }
    }

    public double getEffectFactor(Player player) {
        double effectFactor = 1.0;
        Settings.Animations.SlowBreak.Delay.RealisticSpeeds realisticSpeeds = settings.getAnimations().getSlowBreak().getDelay().getRealisticSpeeds();
        if (player.hasPotionEffect(PotionEffectType.getByName("FAST_DIGGING"))) {
            effectFactor *= 1 - realisticSpeeds.getHasteFactor() * player.getPotionEffect(PotionEffectType.getByName("FAST_DIGGING")).getAmplifier();
        }
        if (player.hasPotionEffect(PotionEffectType.getByName("SLOW_DIGGING"))) {
            effectFactor *= 1 + realisticSpeeds.getMiningFatigueFactor() * player.getPotionEffect(PotionEffectType.getByName("SLOW_DIGGING")).getAmplifier();
        }
        return effectFactor;
    }

    public double getEnchantmentFactor(int efficiencyLevel) {
        if(efficiencyLevel == 0) return 1.0;
        return 0.80 - settings.getAnimations().getSlowBreak().getDelay().getRealisticSpeeds().getEfficiencyFactor() * efficiencyLevel;
    }
}
