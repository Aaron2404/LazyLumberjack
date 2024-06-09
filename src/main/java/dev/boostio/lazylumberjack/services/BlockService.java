package dev.boostio.lazylumberjack.services;

import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleBlockStateData;
import com.github.retrooper.packetevents.protocol.particle.data.ParticleItemStackData;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import dev.boostio.lazylumberjack.schedulers.Scheduler;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BlockService {
    private final Scheduler scheduler;
    private final MaterialService materialService;

    /**
     * Constructor for BlockService.
     *
     * @param scheduler the scheduler to use for delayed tasks.
     * @param materialService the material service to use for material-related checks.
     */
    public BlockService(Scheduler scheduler, MaterialService materialService) {
        this.scheduler = scheduler;
        this.materialService = materialService;
    }

    public WrapperPlayServerParticle breakParticle(Location location, BlockData blockData) {
        ParticleBlockStateData particleBlockStateData = new ParticleBlockStateData(SpigotConversionUtil.fromBukkitBlockData(blockData));
        return new WrapperPlayServerParticle(
                new Particle(ParticleTypes.BLOCK, particleBlockStateData),
                false,
                new Vector3d(location.getX(), location.getY(), location.getZ()),
                new Vector3f(),
                0f, 10
        );
    }

    /**
     * Recursively finds logs related to the given log by checking each side and recursively moving down the blocks.
     *
     * @param block the block to start from.
     * @param relatedLogs the list to store the related logs in.
     * @param visitedBlocks the set to store the visited blocks in.
     * @param consecutiveLeaves the number of consecutive leaves found.
     * @param airBlocks the number of air blocks found.
     */
    public void findRelatedLogs(Block block, List<Block> relatedLogs, Set<Block> visitedBlocks, int consecutiveLeaves, int airBlocks) {
        BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

        for (BlockFace face : faces) {
            Block relativeBlock = block.getRelative(face);

            if (visitedBlocks.add(relativeBlock)) {
                if (materialService.isLog(relativeBlock.getType())) {
                    relatedLogs.add(relativeBlock);
                    findRelatedLogs(relativeBlock, relatedLogs, visitedBlocks, 0, 0);
                } else if (materialService.isLeaf(relativeBlock.getType()) && consecutiveLeaves < 2) {
                    findRelatedLogs(relativeBlock, relatedLogs, visitedBlocks, consecutiveLeaves + 1, airBlocks);
                } else if (relativeBlock.getType() == Material.AIR && airBlocks <= 1) {
                    findRelatedLogs(relativeBlock, relatedLogs, visitedBlocks, consecutiveLeaves, airBlocks + 1);
                }
            }
        }
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
     * Breaks a block with animation.
     *
     * @param user the user.
     * @param block the block to break.
     * @param counter the counter for animation timing.
     * @param blockBreakAnimationDelay the delay between animations.
     */
    public void breakBlockWithAnimation(User user, Block block, int counter, long blockBreakAnimationDelay) {
        scheduler.runTaskDelayed(o -> {
            for (byte i = 0; i < 9; i++) {
                byte finalI = i;
                scheduler.runTaskDelayed(o1 -> {
                    scheduler.runAsyncTask(o3 -> {
                        user.sendPacket(new WrapperPlayServerBlockBreakAnimation(finalI, new Vector3i(block.getX(), block.getY(), block.getZ()), finalI));
                        user.sendPacket(breakParticle(block.getLocation(), block.getBlockData()));
                    });
                }, blockBreakAnimationDelay * i, TimeUnit.MILLISECONDS);
            }
            scheduler.runTaskDelayed(o2 -> {
                if (materialService.isLog(block.getType())) {
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
                    .filter(log -> materialService.isDirtOrPodzol(log.getRelative(BlockFace.DOWN).getType()))
                    .forEach(log -> materialService.plantSapling(log, logMaterial));
        }, blockBreakAnimationDelay * 8 * logs.size() + 20L, TimeUnit.MILLISECONDS);
    }
}
