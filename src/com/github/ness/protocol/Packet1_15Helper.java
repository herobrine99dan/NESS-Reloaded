package com.github.ness.protocol;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.mswsplex.MSWS.NESS.NESS;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

public class Packet1_15Helper {

	public static void removePlayer(Player player) {
		Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> {
			channel.pipeline().remove(player.getName());
			return null;
		});
	}

	public static void injectPlayer(Player player) {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

			@Override
			public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
				Bukkit.getScheduler().scheduleSyncDelayedTask(NESS.main, () -> {
					// System.out.println("Packet: " + packet.toString());
					DefaultPacketListener.Executor(player, packet);
				}, 0);
				super.channelRead(channelHandlerContext, packet);
			}
		};

		ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel
				.pipeline();
		pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
	}
}
