package com.github.ness.nms.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Server;

public class GetCraftServer {

	private static final Method METHOD;
	
	static {
		try {
			Class<?> serverClazz = NMS.getOBCClass("CraftServer");

			METHOD = serverClazz.getMethod("getServer");
			METHOD.setAccessible(true);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	private GetCraftServer() {}
	
	static Object invoke(Server server) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return METHOD.invoke(server);
	}
	
}
