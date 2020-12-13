package com.github.ness.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

final class MethodDescriptionSimple<R> implements MethodDescription<R> {

	private final String name;
	private final Class<?>[] parameterTypes;
	private final boolean isStatic;

	MethodDescriptionSimple(String name, Class<?>[] parameterTypes, boolean isStatic) {
		this.name = Objects.requireNonNull(name);
		this.parameterTypes = Objects.requireNonNull(parameterTypes);
		this.isStatic = isStatic;
	}

	@Override
	public boolean matches(Method method) {
		return method.getName().equals(name)
				&& Arrays.equals(method.getParameterTypes(), parameterTypes)
				&& isStatic == Modifier.isStatic(method.getModifiers());
	}

	@Override
	public int memberOffset() {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		result = prime * result + Arrays.hashCode(parameterTypes);
		result = prime * result + (isStatic ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof MethodDescriptionSimple)) {
			return false;
		}
		MethodDescriptionSimple<?> other = (MethodDescriptionSimple<?>) object;
		return name.equals(other.name)
				&& Arrays.equals(parameterTypes, other.parameterTypes)
				&& isStatic == other.isStatic;
	}

	@Override
	public String toString() {
		return "MethodDescriptionSimple [name=" + name + ", parameterTypes=" + Arrays.toString(parameterTypes)
				+ ", isStatic=" + isStatic + "]";
	}

}
