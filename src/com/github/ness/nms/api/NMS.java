package com.github.ness.nms.api;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMS {

	private static final String VERSION;

	static {
		String packageName = Bukkit.getServer().getClass().getPackage().getName(); // e.g.
																					// "org.bukkit.craftbukkit.v1_8_R3"
		VERSION = packageName.substring("org.bukkit.craftbukkit.".length());
	}

	// Prevent instantiation
	private NMS() {
	}

	/**
	 * Gets the global NMS version, such as "v1_8_R3" or "v1_14_R1".
	 * 
	 * @return the runtime nms version
	 */
	public static String getVersion() {
		return VERSION;
	}

	/**
	 * Gets a player's ping, that is, the latency between the client and the host.
	 * 
	 * @param player the player
	 * @return the player's ping
	 * @throws InvocationTargetException reflection exception
	 * @throws IllegalAccessException    reflection exception
	 * @throws IllegalArgumentException  reflection exception
	 */
	public static int getPing(final Player player) {
		int ping = 900;
		try {
			final Object entityPlayer = player.getClass().getMethod("getHandle", (Class<?>[]) new Class[0])
					.invoke(player, new Object[0]);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (Exception ex) {
		}
		return ping;
	}

	public static void setPing(Player player, int ping) {
		try {
			Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit."
					+ Bukkit.getServer().getClass().getPackage().getName().substring(23) + ".entity.CraftPlayer");
			Object handle = craftPlayerClass.getMethod("getHandle", new Class[0]).invoke(craftPlayerClass.cast(player),
					new Object[0]);
			handle.getClass().getDeclaredField("ping").set(handle, Integer.valueOf(50));
		} catch (Exception exception) {

		}
	}

	/**
	 * Gets the recent TPS (ticks per second) of the server. The returned array has
	 * a length of <code>3</code>, corresponding to the TPS over the past 1 minute,
	 * 5 minutes, and 15 minutes respectively, using /tps.
	 * 
	 * @return the tps array
	 * @throws IllegalArgumentException  reflection exception
	 * @throws IllegalAccessException    reflection exception
	 * @throws InvocationTargetException reflection exception
	 */
	public static double[] getTPS() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return GetMinecraftServer.invoke(GetCraftServer.invoke(Bukkit.getServer()));
	}

	static Class<?> getOBCClass(String clazzname) throws ClassNotFoundException {
		return Class.forName("org.bukkit.craftbukkit." + VERSION + "." + clazzname);
	}

	static Class<?> getNMSClass(String clazzname) throws ClassNotFoundException {
		return Class.forName("net.minecraft.server." + VERSION + "." + clazzname);
	}

}