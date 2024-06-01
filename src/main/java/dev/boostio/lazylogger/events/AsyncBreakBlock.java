package dev.boostio.lazylogger.events;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation;
import dev.boostio.lazylogger.LazyLogger;
import dev.boostio.lazylogger.managers.LogManager;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import org.bukkit.Bukkit;
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
    private final LazyLogger plugin;

    public AsyncBreakBlock(LazyLogger plugin) {
        this.logManager = plugin.getLogManager();
        this.plugin = plugin;
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

            int counter = 0;
            for (Block block : oakLogs) {
                if(!logManager.isLog(block.getType())){
                    continue;
                }

                counter++;

                FoliaScheduler.getRegionScheduler().runDelayed(this.plugin, block.getLocation(), (o) -> {
                    for (byte i = 0; i < 9; i++) {
                        final byte stage = i;

                        FoliaScheduler.getRegionScheduler().runDelayed(this.plugin, block.getLocation(), (o1) -> {
                            player.sendMessage("Breaking block at stage " + stage);

                            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerBlockBreakAnimation(player.getEntityId(), new Vector3i(block.getX(), block.getY(), block.getZ()), stage));
                        }, 1L * i);

                        FoliaScheduler.getRegionScheduler().runDelayed(this.plugin, block.getLocation(), (o2) -> {
                            block.breakNaturally();
                        }, 1L * i);
                    }
                }, 1L * 8 * counter);
            }

            logManager.findLowestY(oakLogs).stream()
                    .filter(log -> logManager.isDirtOrPodzol(log.getRelative(BlockFace.DOWN).getType()))
                    .forEach(log -> logManager.plantSapling(log, logMaterial));
        }
    }
}