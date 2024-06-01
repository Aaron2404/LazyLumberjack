package dev.boostio.lazylogger.events;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.boostio.lazylogger.LazyLogger;
import dev.boostio.lazylogger.managers.LogManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AsyncBreakBlock implements Listener {
    private final LogManager logManager;

    public AsyncBreakBlock(LazyLogger plugin) {
        this.logManager = plugin.getLogManager();
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

        for (int counter = 0; counter < oakLogs.size(); counter++) {
            Block block = oakLogs.get(counter);
            this.logManager.breakBlockWithAnimation(user, block, counter);
        }

       this.logManager.plantSaplingsAfterDelay(oakLogs, logMaterial);
    }
}