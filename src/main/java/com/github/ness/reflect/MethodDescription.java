package com.github.ness.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

public class MethodDescription<R> implements MemberDescription<Method> {

	private final String name;
	private final Class<R> returnType;
	private final Class<?>[] parameterTypes;
	private final boolean isStatic;

	private MethodDescription(String name, Class<R> returnType, Class<?>[] parameterTypes, boolean isStatic) {
		this.name = Objects.requireNonNull(name, "name");
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.isStatic = isStatic;
	}

	public static <R> MethodDescription<R> forMethod(String name, Class<R> returnType, Class<?>...parameterTypes) {
		return new MethodDescription<>(name, returnType, parameterTypes, false);
	}

	public static <R> MethodDescription<R> forStaticMethod(String name, Class<R> returnType, Class<?>...parameterTypes) {
		return new MethodDescription<>(name, returnType, parameterTypes, true);
	}

	@Override
	public boolean matches(Method method) {
		return method.getName().equals(name)
				&& method.getReturnType().equals(returnType)
				&& Arrays.equals(method.getParameterTypes(), parameterTypes)
				&& isStatic == Modifier.isStatic(method.getModifiers());
	}

	@Override
	public String toString() {
		return "MethodDescription [name=" + name + ", returnType=" + returnType + ", parameterTypes="
				+ Arrays.toString(parameterTypes) + ", isStatic=" + isStatic + "]";
	}

}
