package com.github.ness.packets;

import java.lang.invoke.MethodHandle;

import com.github.ness.reflect.ReflectionException;
import com.github.ness.utility.UncheckedReflectiveOperationException;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;

public class NetworkReflection {

	// player.getHandle().playerConnection.networkManager
	private final MethodHandle getHandleMethod;
	private final MethodHandle playerConnectionField;
	private final MethodHandle networkManagerField;

	// Field in NetworkManager
	private final MethodHandle channelField;

	NetworkReflection(MethodHandle getHandleMethod, MethodHandle playerConnectionField,
			MethodHandle networkManagerField, MethodHandle channelField) {
		this.getHandleMethod = getHandleMethod;
		this.playerConnectionField = playerConnectionField;
		this.networkManagerField = networkManagerField;
		this.channelField = channelField;
	}

	/**
	 * Gets the network Manager of a Player
	 *
	 * @param player the bukkit player
	 * @return the networkManager, never {@code null}
	 * @throws UncheckedReflectiveOperationException if reflection failed
	 */
	Object getNetworkManager(Player player) {
		try {
			Object handle = getHandleMethod.invoke(player);
			Object playerConnection = playerConnectionField.invokeWithArguments(handle);
			Object networkManager = networkManagerField.invokeWithArguments(playerConnection);
			return networkManager;
		} catch (RuntimeException | Error ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new ReflectionException(ex);
		}
	}

	/**
	 * Gets the channel of a network manager
	 * 
	 * @param networkManager the network manager
	 * @return the channel
	 * @throws UncheckedReflectiveOperationException if reflection failed
	 */
	Channel getChannel(Object networkManager) {
		try {
			return (Channel) channelField.invokeWithArguments(networkManager);
		} catch (RuntimeException | Error ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new ReflectionException(ex);
		}
	}

}
