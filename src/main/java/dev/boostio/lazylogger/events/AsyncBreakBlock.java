package dev.boostio.lazylogger.events;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.boostio.lazylogger.LazyLogger;
import dev.boostio.lazylogger.managers.LogManager;
import dev.boostio.lazylogger.schedulers.Scheduler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;
import java.util.stream.Collectors;

public class AsyncBreakBlock implements Listener {
    private final LogManager logManager;
    private final Scheduler scheduler;

    public AsyncBreakBlock(LazyLogger plugin) {
        this.logManager = plugin.getLogManager();
        this.scheduler = plugin.getScheduler();
    }

    @EventHandler
    public void onAsyncBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        if (!player.isSneaking() || !player.hasPermission("LazyLogger.Use") || !logManager.isAxe(player.getInventory().getItemInMainHand().getType()) || !logManager.isLog(event.getBlock().getType())) {
            return;
        }

        List<Block> relatedLogs = new ArrayList<>();
        logManager.findRelatedLogs(event.getBlock(), relatedLogs, new HashSet<>(), 0, 0);

        if (relatedLogs.isEmpty()) {
            return;
        }

        Material logMaterial = relatedLogs.get(0).getType();
        relatedLogs.sort(Comparator.comparingInt(block -> block.getLocation().getBlockY()));

        long delay = relatedLogs.size() > 50 ? 35 : 40;
        logManager.processLogs(user, relatedLogs, delay);

        logManager.plantSaplingsAfterDelay(relatedLogs, logMaterial, delay);
    }
}