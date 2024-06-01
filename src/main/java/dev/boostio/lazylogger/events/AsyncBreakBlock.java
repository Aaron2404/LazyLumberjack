package dev.boostio.lazylogger.events;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockBreakAnimation;
import dev.boostio.lazylogger.LazyLogger;
import dev.boostio.lazylogger.managers.LogManager;
import dev.boostio.lazylogger.schedulers.Scheduler;
import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
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
    private final LazyLogger plugin;
    private final Scheduler scheduler;
    private final LogManager logManager;

    public AsyncBreakBlock(LazyLogger plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getScheduler();
        this.logManager = plugin.getLogManager();
    }

    @EventHandler
    public void onAsyncBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        Material mainHandItem = player.getInventory().getItemInMainHand().getType();
        Material blockType = event.getBlock().getType();

        if (!player.isSneaking() || !player.hasPermission("LazyLogger.Use") || !logManager.isAxe(mainHandItem) || !logManager.isLog(blockType)) {
            return;
        }

        List<Block> oakLogs = new ArrayList<>();
        logManager.findRelatedLogs(event.getBlock(), oakLogs, new HashSet<>(), 0, 0);
        Material logMaterial = oakLogs.get(0).getType();

        int counter = 0;
        for (Block block : oakLogs) {
            if (!logManager.isLog(block.getType())) {
                continue;
            }

            counter++;

            FoliaScheduler.getRegionScheduler().runDelayed(this.plugin, block.getLocation(), (o) -> {
                for (byte i = 0; i < 9; i++) {
                    final byte stage = i;

                    FoliaScheduler.getRegionScheduler().runDelayed(this.plugin, block.getLocation(), (o1) -> {
                        player.sendMessage("Breaking block at stage " + stage);

                        user.sendPacket(new WrapperPlayServerBlockBreakAnimation(player.getEntityId(), new Vector3i(block.getX(), block.getY(), block.getZ()), stage));
                    }, i);
                    FoliaScheduler.getRegionScheduler().runDelayed(this.plugin, block.getLocation(), (o2) -> {
                        block.breakNaturally();
                    }, i);
                }
            }, (long) 8 * counter);
        }

        logManager.findLowestY(oakLogs).stream()
                .filter(log -> logManager.isDirtOrPodzol(log.getRelative(BlockFace.DOWN).getType()))
                .forEach(log -> logManager.plantSapling(log, logMaterial));
    }
}