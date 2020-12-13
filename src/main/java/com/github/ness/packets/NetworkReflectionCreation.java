package com.github.ness.packets;

import java.lang.invoke.MethodHandle;

import com.github.ness.reflect.ClassLocator;
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
		MethodHandle getHandleMethod = reflection
				.getMethod(craftPlayerClass, MemberDescriptions.forMethod("getHandle")).unreflect();

		MethodHandle playerConnectionField = reflection
				.getField(getHandleMethod.type().returnType(), MemberDescriptions.forField("playerConnection"))
				.unreflectGetter();
		MethodHandle networkManagerField = reflection
				.getField(playerConnectionField.type().returnType(), MemberDescriptions.forField("networkManager"))
				.unreflectGetter();

		MethodHandle channelField = reflection
				.getField(networkManagerField.type().returnType(), MemberDescriptions.forField("channel", Channel.class))
				.unreflectGetter();
		return new NetworkReflection(getHandleMethod, playerConnectionField, networkManagerField, channelField);
	}

}
