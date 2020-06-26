package com.github.ness.packets;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ness.NESSAnticheat;
import com.github.ness.check.PingSpoof;
import com.github.ness.packets.checks.BadPackets;
import com.github.ness.packets.checks.KillauraFalseFlyingPacket;
import com.github.ness.packets.checks.MorePackets;
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
			channel.pipeline().remove(player.getName());
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
				if (!executeActions(player, packet)) {
					super.channelRead(channelHandlerContext, packet);
				}
			}
		};

		// ChannelPipeline pipeline = ((CraftPlayer)
		// player).getHandle().playerConnection.networkManager.channel
		ChannelPipeline pipeline = getChannel(player).pipeline();
		pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
	}

	/**
	 * Execute all the packet checks
	 * 
	 * @param p
	 * @param packet
	 * @return
	 */

	public boolean executeActions(Player p, Object packet) {
		if (p == null || packet == null || NESSAnticheat.main == null) {
			return true;
		}
		String packetname = packet.toString().substring(0, packet.toString().indexOf("@"))
				.replace("net.minecraft.server.", "");
		if (packetname.toLowerCase().contains("position")) {
			return BadPackets.Check(p, ReflectionUtility.getPacketName(packet));
		} else if (packetname.toLowerCase().contains("flying")) {
			return PingSpoof.Check(p, packet);
		}
		if (KillauraFalseFlyingPacket.Check(packet, p)) {
			return true;
		}
		return MorePackets.Check(p, packet);
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
