package com.github.ness.protocol;

import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ness.check.PingSpoof;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

public class Packet1_15Helper implements Listener{
	
	  @EventHandler
	  public void onJoin(PlayerJoinEvent event) {
		  try {
			  injectPlayer(event.getPlayer());
		  }catch(Exception e) {
			  
		}
	  }
	  
	  @EventHandler
	  public void onQuit(PlayerQuitEvent event) {
		  try {
			  removePlayer(event.getPlayer());
		  }catch(Exception e) {
			  
		  }
	  }

	public void removePlayer(Player player) {
		Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> {
			channel.pipeline().remove(player.getName());
			return null;
		});
	}

	public void injectPlayer(Player player) {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

			@Override
			public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
				String packetname = packet.toString().substring(0, packet.toString().indexOf("@"))
						.replace("net.minecraft.server.", "");
				if (packetname.toLowerCase().contains("position")) {
					PacketListener.BadPacketsCheck(player, packet);
				} else if (packetname.toLowerCase().contains("flying")) {
					PingSpoof.Check(player, packet);
				}
				PacketListener.MorePacketsCheck(player, packet);
				super.channelRead(channelHandlerContext, packet);
			}
		};

		ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel
				.pipeline();
		pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
	}
}
