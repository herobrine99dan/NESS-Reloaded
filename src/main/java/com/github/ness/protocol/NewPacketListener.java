package com.github.ness.protocol;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.github.ness.check.PingSpoof;
import com.github.ness.nms.ReflectionUtility;

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

	public void removePlayer(Player player)
			throws Exception {
		Channel channel = getChannel(player);
		// Channel channel = ((CraftPlayer)
		// player).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> {
			channel.pipeline().remove(player.getName());
			return;
		});
	}

	public void injectPlayer(Player player)
			throws Exception {
		player.sendMessage("Injecting...");
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
			@Override
			public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
				String packetname = packet.toString().substring(0, packet.toString().indexOf("@"))
						.replace("net.minecraft.server.", "");
				player.sendMessage(packet.toString());
				super.channelRead(channelHandlerContext, packet);
			}
		};

		// ChannelPipeline pipeline = ((CraftPlayer)
		// player).getHandle().playerConnection.networkManager.channel
		ChannelPipeline pipeline = getChannel(player).pipeline();
		pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
		player.sendMessage("Added!");
	}

	public Channel getChannel(Player player) {
		try {
		player.sendMessage("GetClass...");
		Class<?> craftplayerclass = (Class<?>) Class
				.forName("org.bukkit.craftbukkit." + ReflectionUtility.ver() + ".entity.CraftPlayer");
		// Object craftedplayer = craftplayerclass.cast(player);
		player.sendMessage("Getting Handle...");
		Object handle = craftplayerclass.getMethod("getHandle").invoke(player);
		player.sendMessage("PlayerConnection...");
		Object playerConnection = handle.getClass().getDeclaredField("playerConnection").get(handle);
		player.sendMessage("NetWorkManager...");
		Object networkManager = playerConnection.getClass().getDeclaredField("networkManager").get(handle);
		player.sendMessage("Channel...");
		Channel channel = (Channel) networkManager.getClass().getDeclaredField("channel").get(handle);
		return channel;
		}catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
