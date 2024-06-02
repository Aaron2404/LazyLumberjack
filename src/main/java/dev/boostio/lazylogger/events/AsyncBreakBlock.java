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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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

        List<Block> oakLogs = new ArrayList<>();
        logManager.findRelatedLogs(event.getBlock(), oakLogs, new HashSet<>(), 0, 0);
        Material logMaterial = oakLogs.get(0).getType();
        oakLogs.sort(Comparator.comparingInt(block -> block.getLocation().getBlockY()));

        long delay;
        if(oakLogs.size() > 50) {
            delay = 12;
        } else {
            delay = 25;
        }

        player.sendMessage(oakLogs.size() + "");

        for (int counter = 0; counter < oakLogs.size(); counter++) {
            Block block = oakLogs.get(counter);

            List<Block> blocksWithTheSameY = oakLogs.stream()
                    .filter(filteredBlock -> filteredBlock.getLocation().getBlockY() == block.getY())
                    .collect(Collectors.toList());

            for (Block blockWithTheSameY : blocksWithTheSameY) {
                int finalCounter = counter;
                scheduler.runAsyncTask((o) -> logManager.breakBlockWithAnimation(user, blockWithTheSameY, finalCounter, delay));
            }
        }

       this.logManager.plantSaplingsAfterDelay(oakLogs, logMaterial, delay);
    }
}