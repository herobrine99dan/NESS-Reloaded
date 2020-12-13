package com.github.ness.packets;

import java.util.NoSuchElementException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelPipeline;

public class PacketListener implements Listener {

	private final NetworkReflection networkReflection;
	private final PacketInterceptor interceptor;

	public PacketListener(NetworkReflection networkReflection, PacketInterceptor interceptor) {
		this.networkReflection = networkReflection;
		this.interceptor = interceptor;
	}

    /**
     * Gets the netty Channel of a Player
     *
     * @param player the bukkit player
     * @return the channel
     * @throws ReflectionException if reflection failed
     */
    private Channel getChannel(Player player) {
    	return networkReflection.getChannel(networkReflection.getNetworkManager(player));
    }

    private String getChannelIdentifier(Player player) {
    	return "Ness_PacketListener_" + player.getUniqueId().toString();
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
    void removePlayer(Player player) {
    	String identifier = getChannelIdentifier(player);
        Channel channel = getChannel(player);

        channel.eventLoop().submit(() -> {
        	ChannelPipeline pipeline = channel.pipeline();
        	try {
        		 pipeline.remove(identifier);
        	} catch (NoSuchElementException ignored) {
        		// Not present either way
        	}
        });
    }

    /**
     * Inject our own channel handler into the pipeline
     *
     * @param player the player
     */
    void injectPlayer(Player player) {
        ChannelDuplexHandler handler = new PacketInterceptorHandler(player.getUniqueId(), interceptor);
        String identifier = getChannelIdentifier(player);

        getChannel(player).pipeline().addBefore("packet_handler", identifier, handler);
    }

}
