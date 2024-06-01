package dev.boostio.lazylogger.managers;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation;
import dev.boostio.lazylogger.LazyLogger;
import dev.boostio.lazylogger.schedulers.Scheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LogManager {
    private final Scheduler scheduler;

    public LogManager(LazyLogger plugin) {
        this.scheduler = plugin.getScheduler();
    }

    /**
     * Recursively find logs related the given log by checking each side and recursively moving down the blocks.
     *
     * @param block the block to start from.
     * @param oakLogs the list to store the oak logs in.
     * @param visitedBlocks the set to store the visited blocks in.
     */
    public void findRelatedLogs(Block block, List<Block> oakLogs, Set<Block> visitedBlocks, int consecutiveLeaves, int airBlocks) {
        BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

        for (BlockFace face : faces) {
            Block relativeBlock = block.getRelative(face);

            if (!visitedBlocks.contains(relativeBlock)) {
                visitedBlocks.add(relativeBlock);

                if (isLog(relativeBlock.getType())) {
                        oakLogs.add(relativeBlock);
                        findRelatedLogs(relativeBlock, oakLogs, visitedBlocks, 0, 0); // Reset the consecutive leaves and air blocks counters
                } else if (isLeaf(relativeBlock.getType())) {
                    if (consecutiveLeaves < 2) { // Only continue if there are less than 2 consecutive leaves
                        findRelatedLogs(relativeBlock, oakLogs, visitedBlocks, consecutiveLeaves + 1, airBlocks);
                    }
                } else if (relativeBlock.getType() == Material.AIR) {
                    if(airBlocks <= 1){
                        findRelatedLogs(relativeBlock, oakLogs, visitedBlocks, consecutiveLeaves, airBlocks + 1); // Increment the air blocks counter
                    }
                }
            }
        }
    }

    public List<Block> findLowestY(List<Block> oakLogs) {
        int minY = Integer.MAX_VALUE;
        List<Block> lowestBlocks = new ArrayList<>();

        for (Block block : oakLogs) {
            int y = block.getLocation().getBlockY();
            if (y < minY) {
                minY = y;
                lowestBlocks.clear(); // Clear the list as we found a new minimum
                lowestBlocks.add(block);
            } else if (y == minY) {
                lowestBlocks.add(block); // Add the block to the list as it shares the minimum Y value
            }
        }

        return lowestBlocks;
    }

    private boolean isDirtOrPodzol(Material material) {
        return material == Material.DIRT || material == Material.PODZOL;
    }

    private void plantSapling(Block log, Material logMaterial) {
        log.setType(this.getSaplingFromLog(logMaterial));
        log.getWorld().spawnFallingBlock(log.getLocation(), log.getBlockData());
    }

    public void breakBlockWithAnimation(User user, Block block, int counter) {
        scheduler.runTaskDelayed((o) -> {
            for (byte i = 0; i < 9; i++) {
                byte finalI = i;
                scheduler.runTaskDelayed((o1) -> {
                    scheduler.runAsyncTask((o3) -> {
                        user.sendPacket(new WrapperPlayServerBlockBreakAnimation(user.getEntityId(), new Vector3i(block.getX(), block.getY(), block.getZ()), finalI));
                    });
                }, 1L * i);
            }
            scheduler.runTaskDelayed((o2) -> {
                if(this.isLog(block.getType())){
                    block.breakNaturally();
                }
            }, 1L * 8);
        }, 1L * 8 * counter);
    }

    public void plantSaplingsAfterDelay(List<Block> oakLogs, Material logMaterial) {
        scheduler.runTaskDelayed((o) -> {
            this.findLowestY(oakLogs).stream()
                    .filter(log -> this.isDirtOrPodzol(log.getRelative(BlockFace.DOWN).getType()))
                    .forEach(log -> this.plantSapling(log, logMaterial));
        }, 1L * 8 * oakLogs.size() + 20L);
    }

    private Material getSaplingFromLog(Material logType) {
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
                return Material.AIR; // Return air if the log type doesn't have a corresponding sapling
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
