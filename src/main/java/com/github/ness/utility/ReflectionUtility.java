package com.github.ness.utility;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.ness.data.ImmutableLoc;

public class ReflectionUtility {

	public static String cbVer() {
		return "org.bukkit.craftbukkit." + ver() + ".";
	}

	public static String nmsVer() {
		return "net.minecraft.server." + ver() + ".";
	}

	public static String ver() {
		String pkg = Bukkit.getServer().getClass().getPackage().getName();
		return pkg.substring(pkg.lastIndexOf(".") + 1);
	}

	//TODO To Optimize this
	public static String getBlockName(Player p, ImmutableLoc loc) {
		Object entityPlayer = ReflectionUtility.getHandle(p);
		Object world = ReflectionUtility.callMethod(entityPlayer, "getWorld");
		Object position = ReflectionUtility.callConstructor(ReflectionUtility.getNMSClass("BlockPosition"), loc.getX(), loc.getY(), loc.getZ());
		Object type = ReflectionUtility.callMethod(world, "getType", position);
		String info = type.toString().replace("{", "").replaceFirst("Block", "").replace("minecraft", "");
		return info.substring(1, info.indexOf("}"));
	}

	public static Color getColorByName(String name) {
		try {
			return (Color) Color.class.getField(name.toUpperCase()).get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> wrapperToPrimitive(Class<?> clazz) {
		if (clazz == Boolean.class)
			return boolean.class;
		if (clazz == Integer.class)
			return int.class;
		if (clazz == Double.class)
			return double.class;
		if (clazz == Float.class)
			return float.class;
		if (clazz == Long.class)
			return long.class;
		if (clazz == Short.class)
			return short.class;
		if (clazz == Byte.class)
			return byte.class;
		if (clazz == Void.class)
			return void.class;
		if (clazz == Character.class)
			return char.class;
		return clazz;
	}

	public static Class<?>[] toParamTypes(Object... params) {
		Class<?>[] classes = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++)
			classes[i] = wrapperToPrimitive(params[i].getClass());
		return classes;
	}

	public static Object getHandle(Entity e) {
		return callMethod(e, "getHandle");
	}

	public static Object getHandle(World w) {
		return callMethod(w, "getHandle");
	}

	public static Object playerConnection(Player p) {
		return playerConnection(getHandle(p));
	}

	public static Object playerConnection(Object handle) {
		return getDeclaredField(handle, "playerConnection");
	}

	public static void sendPacket(Player p, Object packet) {
		Object pc = playerConnection(p);
		try {
			pc.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(pc, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object getPacket(String name, Object... params) {
		return callDeclaredConstructor(getNMSClass(name), params);
	}

	public static void sendJson(Player p, String json) {
		Object comp = callDeclaredMethod(getNMSClass("ChatSerializer"), "a", json);
		sendPacket(p, getPacket("PacketPlayOutChat", comp, true));
	}

	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name);
		} catch (Exception e) {
			return null;
		}
	}

	public static Class<?> getNMSClass(String name) {
		return getClass(nmsVer() + name);
	}

	public static Class<?> getCBClass(String name) {
		return getClass(cbVer() + name);
	}

	public static Object callDeclaredMethod(Object object, String method, Object... params) {
		try {
			Method m = object.getClass().getDeclaredMethod(method, toParamTypes(params));
			m.setAccessible(true);
			return m.invoke(object, params);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object callMethod(Object object, String method, Object... params) {
		try {
			Method m = object.getClass().getMethod(method, toParamTypes(params));
			m.setAccessible(true);
			return m.invoke(object, params);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object callDeclaredConstructor(Class<?> clazz, Object... params) {
		try {
			Constructor<?> con = clazz.getDeclaredConstructor(toParamTypes(params));
			con.setAccessible(true);
			return con.newInstance(params);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object callConstructor(Class<?> clazz, Object... params) {
		try {
			Constructor<?> con = clazz.getConstructor(toParamTypes(params));
			con.setAccessible(true);
			return con.newInstance(params);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getDeclaredField(Object object, String field) {
		try {
			Field f = object.getClass().getDeclaredField(field);
			f.setAccessible(true);
			return f.get(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getField(Object object, String field) {
		try {
			Field f = object.getClass().getField(field);
			f.setAccessible(true);
			return f.get(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setDeclaredField(Object object, String field, Object value) {
		try {
			Field f = object.getClass().getDeclaredField(field);
			f.setAccessible(true);
			f.set(object, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setField(Object object, String field, Object value) {
		try {
			Field f = object.getClass().getField(field);
			f.setAccessible(true);
			f.set(object, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
