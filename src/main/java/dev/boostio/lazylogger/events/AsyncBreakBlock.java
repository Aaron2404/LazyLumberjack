package dev.boostio.lazylogger.events;

import dev.boostio.lazylogger.LazyLogger;
import dev.boostio.lazylogger.managers.LogManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
        Material mainHandItem = player.getInventory().getItemInMainHand().getType();
        Material blockType = event.getBlock().getType();

        if (player.isSneaking() && player.hasPermission("LazyLogger.Use") && logManager.isAxe(mainHandItem) && logManager.isLog(blockType)) {
            List<Block> oakLogs = new ArrayList<>();
            logManager.findRelatedLogs(event.getBlock(), oakLogs, new HashSet<>(), 0, 0);
            Material logMaterial = oakLogs.get(0).getType();


            oakLogs.forEach(Block::breakNaturally);

            logManager.findLowestY(oakLogs).stream()
                    .filter(log -> logManager.isDirtOrPodzol(log.getRelative(BlockFace.DOWN).getType()))
                    .forEach(log -> logManager.plantSapling(log, logMaterial));
        }
    }
}