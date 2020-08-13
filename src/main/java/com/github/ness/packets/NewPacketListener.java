package com.github.ness.packets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ness.NESSAnticheat;
import com.github.ness.packets.events.ReceivedPacketEvent;
import com.github.ness.packets.wrappers.PacketPlayInPositionLook;
import com.github.ness.packets.wrappers.PacketPlayInUseEntity;
import com.github.ness.packets.wrappers.SimplePacket;
import com.github.ness.utility.ReflectionUtility;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

public class NewPacketListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		try {
			injectPlayer(event.getPlayer());
		} catch (Exception e) {

		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		try {
			removePlayer(event.getPlayer());
		} catch (Exception e) {

		}
	}

	/**
	 * remove the Channel Handler from a player
	 * 
	 * @param player
	 * @throws Exception
	 */

	public void removePlayer(Player player) throws Exception {
		Channel channel = getChannel(player);
		// Channel channel = ((CraftPlayer)
		// player).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> {
			channel.pipeline().remove(player.getName()+ "NESSListener");
		});
	}

	/**
	 * inject the Channel Handler inside a player
	 * 
	 * @param player
	 * @throws Exception
	 */

	public void injectPlayer(Player player) throws Exception {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
			@Override
			public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
				// We can drop the packet disabling this super method!
				ReceivedPacketEvent event = new ReceivedPacketEvent(
						NESSAnticheat.getInstance().getCheckManager().getPlayer(player),
						NewPacketListener.this.getPacketObject(packet));
				Bukkit.getPluginManager().callEvent(event);
				if(!event.isCancelled()) {
					super.channelRead(channelHandlerContext, packet);
				}
			}
		};

		// ChannelPipeline pipeline = ((CraftPlayer)
		// player).getHandle().playerConnection.networkManager.channel
		ChannelPipeline pipeline = getChannel(player).pipeline();
		pipeline.addBefore("packet_handler", player.getName()+ "NESSListener", channelDuplexHandler);
	}

	public SimplePacket getPacketObject(Object p) {
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
	/**
	 * Get the Channel of a Player
	 * 
	 * @param player
	 * @return
	 */

	public Channel getChannel(Player player) {
		try {
			Class<?> craftplayerclass = (Class<?>) Class
					.forName("org.bukkit.craftbukkit." + ReflectionUtility.ver() + ".entity.CraftPlayer");
			// Object craftedplayer = craftplayerclass.cast(player);
			Object handle = craftplayerclass.getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getDeclaredField("playerConnection").get(handle);
			Object networkManager = playerConnection.getClass().getDeclaredField("networkManager")
					.get(playerConnection);
			Channel channel = (Channel) networkManager.getClass().getDeclaredField("channel").get(networkManager);
			return channel;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
