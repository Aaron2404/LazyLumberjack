package dev.boostio.lazylumberjack.events;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import dev.boostio.lazylumberjack.LazyLumberjack;
import dev.boostio.lazylumberjack.managers.LumberManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class BreakBlock implements Listener {
    private final LumberManager logManager;

    public BreakBlock(LazyLumberjack plugin) {
        this.logManager = plugin.getLogManager();
    }

    @EventHandler
    public void onAsyncBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        if (!player.isSneaking() || !player.hasPermission("LazyLumberjack.Use") || !logManager.isAxe(logManager.getItemInMainHand(player)) || !logManager.isLog(event.getBlock().getType())) {
            return;
        }

        List<Block> relatedLogs = new ArrayList<>();
        logManager.findRelatedLogs(event.getBlock(), relatedLogs, new HashSet<>(), 0, 0);

        if (relatedLogs.isEmpty()) {
            return;
        }

        Material logMaterial = relatedLogs.get(0).getType();

        long delay = relatedLogs.size() > 50 ? 35 : 40;
        logManager.processLogs(user, relatedLogs, delay);

        if(user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) {
            logManager.plantSaplingsAfterDelay(relatedLogs, logMaterial, delay);
        }
    }
}