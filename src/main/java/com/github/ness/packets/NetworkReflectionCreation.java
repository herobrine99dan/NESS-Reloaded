package com.github.ness.packets;

import java.lang.invoke.MethodHandle;

import com.github.ness.reflect.ClassLocator;
import com.github.ness.reflect.MemberDescriptions;
import com.github.ness.reflect.MethodInvoker;
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
		MethodInvoker<?> getHandleInvoker = reflection
				.getMethod(craftPlayerClass, MemberDescriptions.forMethod("getHandle"));

		Class<?> entityPlayerClass = classLocator.getNmsClass("EntityPlayer");
		System.out.println("Found MethodInvoker " + getHandleInvoker);
		System.out.println("Found Method " + getHandleInvoker.reflect());
		MethodHandle getHandleMethod = getHandleInvoker.unreflect();
		System.out.println("Found MethodHandle " + getHandleMethod);
		if (getHandleInvoker.unreflect().type().returnType() != entityPlayerClass) {
			throw new AssertionError("Expected return type to be " + entityPlayerClass);
		}
		
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
