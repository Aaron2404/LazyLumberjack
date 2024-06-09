package dev.boostio.lazylumberjack.services;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class MaterialService {
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

    /**
     * Plants a sapling at the given log's location.
     *
     * @param log the log block.
     * @param logMaterial the material of the log.
     */
    public void plantSapling(Block log, Material logMaterial) {
        log.setType(getSaplingFromLog(logMaterial));
        log.getWorld().spawnFallingBlock(log.getLocation(), log.getBlockData());
    }
}
