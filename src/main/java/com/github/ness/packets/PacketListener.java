package com.github.ness.packets;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.packets.wrappers.PacketPlayInPositionLook;
import com.github.ness.packets.wrappers.PacketPlayInUseEntity;
import com.github.ness.packets.wrappers.SimplePacket;
import com.github.ness.utility.UncheckedReflectiveOperationException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

public class PacketListener implements Listener {

    /**
     * Gets the netty Channel of a Player
     *
     * @param player the bukkit player
     * @return the channel, never {@code null}
     * @throws UncheckedReflectiveOperationException if reflection failed
     */
    private Channel getChannel(Player player) {
    	return NetworkReflection.getChannel(NetworkReflection.getNetworkManager(player));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        injectPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    /**
     * Remove our channel handler from the pipeline
     *
     * @param player the player
     */
    private void removePlayer(Player player) {
        Channel channel = getChannel(player);
        // Channel channel = ((CraftPlayer)
        // player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName() + "NESSListener");
        });
    }

    /**
     * Inject our own channel handler into the pipeline
     *
     * @param player the player
     */
    private void injectPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                // We can drop the packet disabling this super method!
            	NessPlayer nessPlayer = NESSAnticheat.getInstance().getCheckManager().getExistingPlayer(player);
            	if (nessPlayer == null) {
            		return;
            	}
                ReceivedPacketEvent event = new ReceivedPacketEvent(
                        nessPlayer,
                        PacketListener.this.getPacketObject(packet));
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    super.channelRead(channelHandlerContext, packet);
                }
            }
        };
        
        // ChannelPipeline pipeline = ((CraftPlayer)
        // player).getHandle().playerConnection.networkManager.channel
        ChannelPipeline pipeline = getChannel(player).pipeline();
        pipeline.addBefore("packet_handler", player.getName() + "NESSListener", channelDuplexHandler);
    }

    private SimplePacket getPacketObject(Object p) {
        String packetname = p.getClass().getSimpleName().toLowerCase();
        SimplePacket packet;
        if (packetname.contains("position")) {
            packet = new PacketPlayInPositionLook(p);
        } else if (packetname.contains("useentity")) {
            packet = new PacketPlayInUseEntity(p);
        } else {
            packet = new SimplePacket(p);
        }
        return packet;
    }

}
