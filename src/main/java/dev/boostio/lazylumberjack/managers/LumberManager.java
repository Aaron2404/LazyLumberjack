package dev.boostio.lazylumberjack.managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation;
import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.schedulers.Scheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LumberManager {
    private final Scheduler scheduler;

    public LumberManager(LazyLumberjack plugin) {
        this.scheduler = plugin.getScheduler();
    }

    /**
     * Recursively find logs related to the given log by checking each side and recursively moving down the blocks.
     *
     * @param block the block to start from.
     * @param relatedLogs the list to store the related logs in.
     * @param visitedBlocks the set to store the visited blocks in.
     */
    public void findRelatedLogs(Block block, List<Block> relatedLogs, Set<Block> visitedBlocks, int consecutiveLeaves, int airBlocks) {
        BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

        for (BlockFace face : faces) {
            Block relativeBlock = block.getRelative(face);

            if (visitedBlocks.add(relativeBlock)) {
                if (isLog(relativeBlock.getType())) {
                    relatedLogs.add(relativeBlock);
                    findRelatedLogs(relativeBlock, relatedLogs, visitedBlocks, 0, 0);
                } else if (isLeaf(relativeBlock.getType()) && consecutiveLeaves < 2) {
                    findRelatedLogs(relativeBlock, relatedLogs, visitedBlocks, consecutiveLeaves + 1, airBlocks);
                } else if (relativeBlock.getType() == Material.AIR && airBlocks <= 1) {
                    findRelatedLogs(relativeBlock, relatedLogs, visitedBlocks, consecutiveLeaves, airBlocks + 1);
                }
            }
        }
    }

    /**
     * Finds blocks at the lowest Y level.
     *
     * @param logs the list of logs.
     * @return a list of blocks at the lowest Y level.
     */
    private List<Block> findLowestY(List<Block> logs) {
        int minY = logs.stream().mapToInt(block -> block.getLocation().getBlockY()).min().orElse(Integer.MAX_VALUE);
        return logs.stream().filter(block -> block.getLocation().getBlockY() == minY).collect(Collectors.toList());
    }

    /**
     * Check if the material is dirt or podzol.
     *
     * @param material the material to check.
     * @return true if the material is dirt or podzol, false otherwise.
     */
    private boolean isDirtOrPodzol(Material material) {
        return material == Material.DIRT || material == Material.PODZOL;
    }

    /**
     * Plants a sapling at the given log's location.
     *
     * @param log the log block.
     * @param logMaterial the material of the log.
     */
    private void plantSapling(Block log, Material logMaterial) {
        log.setType(getSaplingFromLog(logMaterial));
        log.getWorld().spawnFallingBlock(log.getLocation(), log.getBlockData());
    }

    /**
     * Breaks a block with animation.
     *
     * @param user the user.
     * @param block the block to break.
     * @param counter the counter for animation timing.
     * @param blockBreakAnimationDelay the delay between animations.
     */
    private void breakBlockWithAnimation(User user, Block block, int counter, long blockBreakAnimationDelay) {
        scheduler.runTaskDelayed(o -> {
            for (byte i = 0; i < 9; i++) {
                byte finalI = i;
                scheduler.runTaskDelayed(o1 -> {
                    scheduler.runAsyncTask(o3 -> {
                        user.sendPacket(new WrapperPlayServerBlockBreakAnimation(finalI, new Vector3i(block.getX(), block.getY(), block.getZ()), finalI));
                    });
                }, blockBreakAnimationDelay * i, TimeUnit.MILLISECONDS);
            }
            scheduler.runTaskDelayed(o2 -> {
                if (isLog(block.getType())) {
                    block.breakNaturally();
                }
            }, blockBreakAnimationDelay * 8, TimeUnit.MILLISECONDS);
        }, blockBreakAnimationDelay * 8 * counter, TimeUnit.MILLISECONDS);
    }

    /**
     * Plants saplings after a delay.
     *
     * @param logs the list of logs.
     * @param logMaterial the material of the logs.
     * @param blockBreakAnimationDelay the delay between animations.
     */
    public void plantSaplingsAfterDelay(List<Block> logs, Material logMaterial, long blockBreakAnimationDelay) {
        scheduler.runTaskDelayed(o -> {
            findLowestY(logs).stream()
                    .filter(log -> isDirtOrPodzol(log.getRelative(BlockFace.DOWN).getType()))
                    .forEach(log -> plantSapling(log, logMaterial));
        }, blockBreakAnimationDelay * 8 * logs.size() + 20L, TimeUnit.MILLISECONDS);
    }

    /**
     * Processes logs by breaking them with animation.
     *
     * @param user the user.
     * @param logs the list of logs.
     * @param delay the delay between animations.
     */
    public void processLogs(User user, List<Block> logs, long delay) {
        Map<Integer, List<Block>> logsByYLevel = logs.stream()
                .collect(Collectors.groupingBy(block -> block.getLocation().getBlockY()));

        List<Integer> sortedYLevels = logsByYLevel.keySet().stream().sorted().collect(Collectors.toList());

        int counter = 0;
        for (Integer yLevel : sortedYLevels) {
            List<Block> sameYLevelBlocks = logsByYLevel.get(yLevel);
            for (Block block : sameYLevelBlocks) {
                int finalCounter = counter;
                scheduler.runAsyncTask(o -> breakBlockWithAnimation(user, block, finalCounter, delay));
            }
            counter++;
        }
    }

    public Material getItemInMainHand(Player player) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if(user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)){
            return player.getInventory().getItemInMainHand().getType();
        }
        else{
            return player.getInventory().getItemInHand().getType();
        }
    }

    /**
     * Get the corresponding sapling for the given log type.
     *
     * @param logType the log type.
     * @return the corresponding sapling type.
     */
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
        return material.name().contains("AXE");
        // TODO: Find a better way to check if the material is an axe, which works on every version.
    }

    /**
     * Check if the material is a log block.
     *
     * @param material the material to check.
     * @return true if the material is a log block, false otherwise.
     */
    public boolean isLog(Material material) {
        return material.name().contains("LOG");
        // TODO: Find a better way to check if the material is an axe, which works on every version.
    }

    /**
     * Check if the material is a leaf block.
     *
     * @param material the material to check.
     * @return true if the material is a leaf block, false otherwise.
     */
    private boolean isLeaf(Material material) {
        return material.name().contains("LEAVES");
        // TODO: Find a better way to check if the material is an axe, which works on every version.
    }
}
