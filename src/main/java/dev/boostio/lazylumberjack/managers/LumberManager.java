package dev.boostio.lazylumberjack.managers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.enums.ConfigOption;
import dev.boostio.lazylumberjack.schedulers.IScheduler;
import dev.boostio.lazylumberjack.services.BlockService;
import dev.boostio.lazylumberjack.services.MaterialService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class LumberManager {
    private final IScheduler scheduler;
    private final BlockService blockService;
    private final MaterialService materialService;
    private final ConfigManager configManager;

    private final int baseDelay; // Base delay in milliseconds
    private final double speedFactor;
    ; // Speed factor for gradual scaling

    /**
     * Constructor for LumberManager.
     *
     * @param plugin the plugin instance.
     */
    public LumberManager(LazyLumberjack plugin) {
        this.scheduler = plugin.getScheduler();
        this.materialService = new MaterialService();
        this.configManager = plugin.getConfigManager();
        this.blockService = new BlockService(configManager, scheduler, materialService);

        baseDelay = configManager.getConfigurationOption(ConfigOption.SLOW_BREAK_BASE_DELAY);
        speedFactor = configManager.getConfigurationOption(ConfigOption.SLOW_BREAK_SPEED_FACTOR);
    }

    /**
     * Recursively find logs related to the given log by checking each side and recursively moving down the blocks.
     *
     * @param block the block to start from.
     * @param relatedLogs the list to store the related logs in.
     * @param visitedBlocks the set to store the visited blocks in.
     * @param consecutiveLeaves the number of consecutive leaves found.
     * @param airBlocks the number of air blocks found.
     */
    public void findRelatedLogs(Block block, List<Block> relatedLogs, Set<Block> visitedBlocks, int consecutiveLeaves, int airBlocks) {
        blockService.findRelatedLogs(block, relatedLogs, visitedBlocks, consecutiveLeaves, airBlocks);
    }

    /**
     * Processes logs by breaking them with animation.
     *
     * @param user the user.
     * @param logs the list of logs.
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
        return (long) Math.max(baseDelay / Math.pow(1 + size, speedFactor), 1);
    }

    /**
     * Plants saplings after a delay.
     *
     * @param logs the list of logs.
     * @param logMaterial the material of the logs.
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
        if(user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)){
            return player.getInventory().getItemInMainHand().getType();
        }
        else{
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
