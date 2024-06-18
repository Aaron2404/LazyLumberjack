package dev.boostio.lazylumberjack.events;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.enums.ConfigOption;
import dev.boostio.lazylumberjack.managers.ConfigManager;
import dev.boostio.lazylumberjack.managers.LumberManager;
import dev.boostio.lazylumberjack.schedulers.IScheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BreakBlock implements Listener {
    private final LumberManager logManager;
    private final ConfigManager configManager;
    private final boolean plantSaplings;
    private final IScheduler scheduler;

    public BreakBlock(LazyLumberjack plugin) {
        this.logManager = plugin.getLogManager();
        this.configManager = plugin.getConfigManager();
        this.scheduler = plugin.getScheduler();
        this.plantSaplings = configManager.getBoolean(ConfigOption.SAPLING_PLANTING_ENABLED);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAsyncBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        if (!player.isSneaking() || !player.hasPermission("LazyLumberjack.Use") || !logManager.isAxe(logManager.getItemInMainHand(player)) || !logManager.isLog(event.getBlock().getType())) {
            return;
        }

        scheduler.runAsyncTask(o1 -> {
            List<Block> relatedLogs = new ArrayList<>();
            logManager.findRelatedLogs(event.getBlock(), relatedLogs, new HashSet<>(), 0, 0);

            if (relatedLogs.isEmpty()) {
                return;
            }

            Material logMaterial = relatedLogs.get(0).getType();

            long delay = logManager.calculateDelay(relatedLogs.size());
            logManager.processLogs(user, relatedLogs, delay);

            if (user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13) && plantSaplings) {
                logManager.plantSaplingsAfterDelay(relatedLogs, logMaterial, delay);
            }
        });
    }
}