package dev.boostio.lazylumberjack.packetlisteners;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;


public class BlockBreakAnimation extends PacketListenerAbstract {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (PacketType.Play.Server.BLOCK_BREAK_ANIMATION == event.getPacketType()) {
            User user = event.getUser();

            //user.sendMessage("Block Break Animation Packet Sent");
        }
    }
}