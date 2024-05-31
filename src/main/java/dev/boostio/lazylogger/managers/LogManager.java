package dev.boostio.lazylogger.managers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Set;

public class LogManager {
    /**
     * Recursively find logs related the given log by checking each side and recursively moving down the blocks.
     *
     * @param block the block to start from.
     * @param oakLogs the list to store the oak logs in.
     * @param visitedBlocks the set to store the visited blocks in.
     */
    public void findRelatedLogs(Block block, List<Block> oakLogs, Set<Block> visitedBlocks) {
        BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

        for (BlockFace face : faces) {
            Block relativeBlock = block.getRelative(face);

            if (!visitedBlocks.contains(relativeBlock)) {
                visitedBlocks.add(relativeBlock);

                if (isLog(relativeBlock.getType())) {
                    oakLogs.add(relativeBlock);
                    findRelatedLogs(relativeBlock, oakLogs, visitedBlocks);
                } else if (isLeaf(relativeBlock.getType())) {
                    findRelatedLogs(relativeBlock, oakLogs, visitedBlocks);
                }
            }
        }
    }

    /**
     * Check if the material is an axe.
     *
     * @param material the material to check.
     * @return true if the material is an axe, false otherwise.
     */
    public boolean isAxe(Material material) {
        return material == Material.WOODEN_AXE
                || material == Material.STONE_AXE
                || material == Material.IRON_AXE
                || material == Material.GOLDEN_AXE
                || material == Material.DIAMOND_AXE;
    }

    /**
     * Check if the material is a log block.
     *
     * @param material the material to check.
     * @return true if the material is a log block, false otherwise.
     */
    public boolean isLog(Material material) {
        return material == Material.OAK_LOG
                || material == Material.SPRUCE_LOG
                || material == Material.BIRCH_LOG
                || material == Material.JUNGLE_LOG
                || material == Material.ACACIA_LOG
                || material == Material.DARK_OAK_LOG;
    }

    /**
     * Check if the material is a leaf block.
     *
     * @param material the material to check.
     * @return true if the material is a leaf block, false otherwise.
     */
    public boolean isLeaf(Material material) {
        return material == Material.OAK_LEAVES
                || material == Material.SPRUCE_LEAVES
                || material == Material.BIRCH_LEAVES
                || material == Material.JUNGLE_LEAVES
                || material == Material.ACACIA_LEAVES
                || material == Material.DARK_OAK_LEAVES;
    }
}
