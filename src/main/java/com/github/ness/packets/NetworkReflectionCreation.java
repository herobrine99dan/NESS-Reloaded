package com.github.ness.packets;

import java.lang.invoke.MethodHandle;

import com.github.ness.reflect.locator.ClassLocator;
import com.github.ness.reflect.MemberDescriptions;
import com.github.ness.reflect.Reflection;

import io.netty.channel.Channel;

public class NetworkReflectionCreation {

	private final Reflection reflection;
	private final ClassLocator classLocator;

	public NetworkReflectionCreation(Reflection reflection, ClassLocator classLocator) {
		this.reflection = reflection;
		this.classLocator = classLocator;
	}

	public NetworkReflection create() {
		Class<?> craftPlayerClass = classLocator.getObcClass("entity.CraftPlayer");
		/*
		 * Note: getHandleMethod's formal return type may be either EntityHuman or EntityPlayer.
		 * Class.getDeclaredMethods has a bug where it includes shadowed methods. As a result, the
		 * reflection API sometimes returns the shadowed method as a MethodInvoker. The shadowed
		 * 'getHandle' returns EntityHuman whereas the normal 'getHandle' returns EntityPlayer.
		 * See https://bugs.openjdk.java.net/browse/JDK-8144122
		 */
		MethodHandle getHandleMethod = reflection
				.getMethod(craftPlayerClass, MemberDescriptions.forMethod("getHandle")).unreflect();

		// Use entityPlayerClass rather than getHandleMethod.type().returnType()
		Class<?> entityPlayerClass = classLocator.getNmsClass("EntityPlayer");
		MethodHandle playerConnectionField = reflection
				.getField(entityPlayerClass, MemberDescriptions.forField("playerConnection"))
				.unreflectGetter();
		MethodHandle networkManagerField = reflection
				.getField(playerConnectionField.type().returnType(), MemberDescriptions.forField("networkManager"))
				.unreflectGetter();

		MethodHandle channelField = reflection
				.getField(networkManagerField.type().returnType(), MemberDescriptions.forField(Channel.class, "channel"))
				.unreflectGetter();
		return new NetworkReflection(getHandleMethod, playerConnectionField, networkManagerField, channelField);
	}

}
