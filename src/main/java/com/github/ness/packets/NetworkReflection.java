package com.github.ness.packets;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.ness.utility.UncheckedReflectiveOperationException;

import io.netty.channel.Channel;

public final class NetworkReflection {

	// player.getHandle().playerConnection.networkManager
	private static final Method getHandleMethod;
	private static final Field playerConnectionField;
	private static final Field networkManagerField;

	// Fields in NetworkManager
	private static final Field channelField;
	private static final Field packetQueueField;

	// Collection.clear()
	private static final Method clearMethod;

	static {
		Class<?> craftPlayerClass;
		try {
			craftPlayerClass = Class
					.forName("org.bukkit.craftbukkit." + ver() + ".entity.CraftPlayer");
			getHandleMethod = craftPlayerClass.getMethod("getHandle");
			playerConnectionField = getHandleMethod.getReturnType().getDeclaredField("playerConnection");
			networkManagerField = playerConnectionField.getType().getDeclaredField("networkManager");

			Class<?> networkManagerClass = networkManagerField.getType();
			channelField = networkManagerClass.getDeclaredField("channel");
			// Unfortunally packetQueue field is obfuscated in some versions
			// packetQueueField = networkManagerClass.getDeclaredField("packetQueue");
			packetQueueField = null;
			clearMethod = Collection.class.getMethod("clear");
		} catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	
    public static String ver() {
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        return pkg.substring(pkg.lastIndexOf(".") + 1);
    }

	private NetworkReflection() {
	}

	/**
	 * Gets the network Manager of a Player
	 *
	 * @param player the bukkit player
	 * @return the networkManager, never {@code null}
	 * @throws UncheckedReflectiveOperationException if reflection failed
	 */
	public static Object getNetworkManager(Player player) {
		try {
			Object handle = getHandleMethod.invoke(player);
			Object playerConnection = playerConnectionField.get(handle);
			Object networkManager = networkManagerField.get(playerConnection);
			return networkManager;
		} catch (IllegalAccessException | InvocationTargetException ex) {
			throw new UncheckedReflectiveOperationException(ex);
		}
	}

	/**
	 * Gets the channel of a network manager
	 * 
	 * @param networkManager the network manager
	 * @return the channel
	 * @throws UncheckedReflectiveOperationException if reflection failed
	 */
	public static Channel getChannel(Object networkManager) {
		try {
			return (Channel) channelField.get(networkManager);
		} catch (IllegalAccessException ex) {
			throw new UncheckedReflectiveOperationException(ex);
		}
	}

	/**
	 * Clears the packet queue of a network manager
	 * 
	 * @param networkManager the network manager
	 * @throws UncheckedReflectiveOperationException if reflection failed
	 */
	public static void clearPacketQueu(Object networkManager) {
		try {
			Object packetQueue = packetQueueField.get(networkManager);
			clearMethod.invoke(packetQueue);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			throw new UncheckedReflectiveOperationException(ex);
		}
	}

}
