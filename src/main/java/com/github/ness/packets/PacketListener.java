package com.github.ness.packets;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.packets.wrappers.SimplePacket;
import com.github.ness.utility.UncheckedReflectiveOperationException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

public class PacketListener implements Listener {

	private final NessAnticheat ness;

	public PacketListener(NessAnticheat ness) {
		this.ness = ness;
	}

	/**
	 * Gets the netty Channel of a Player
	 *
	 * @param player the bukkit player
	 * @return the channel, never {@code null}
	 * @throws UncheckedReflectiveOperationException if reflection failed
	 */
	private Channel getChannel(Player player) {
		if (!player.isOnline()) {
			throw new UncheckedReflectiveOperationException("Player " + player.getName() + " isn't online!",
					new ReflectiveOperationException());
		}
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
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandlerImpl(player);

		ChannelPipeline pipeline = getChannel(player).pipeline();
		pipeline.addBefore("packet_handler", player.getName() + "NESSListener", channelDuplexHandler);
	}

	private class ChannelDuplexHandlerImpl extends ChannelDuplexHandler {

		private final UUID uuid;

		ChannelDuplexHandlerImpl(Player player) {
			uuid = player.getUniqueId();
		}

		@Override
		public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
			if (shouldContinue(packet)) {
				super.channelRead(channelHandlerContext, packet);
			}
		}

		private boolean shouldContinue(Object packet) throws IllegalArgumentException, IllegalAccessException {
			NessPlayer nessPlayer = ness.getCheckManager().getExistingPlayer(uuid);
			if (nessPlayer == null) {
				return true;
			}
			ReceivedPacketEvent event = new ReceivedPacketEvent(nessPlayer, getPacketObject(packet));
			ness.getPlugin().getServer().getPluginManager().callEvent(event);
			return !event.isCancelled();
		}
	}

	private SimplePacket getPacketObject(Object p) throws IllegalArgumentException, IllegalAccessException {
		String packetname = p.getClass().getSimpleName().toLowerCase();
		SimplePacket packet = new SimplePacket(p);
		packet.process();
		return packet;
	}

}
