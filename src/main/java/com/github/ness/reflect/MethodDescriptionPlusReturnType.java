package com.github.ness.reflect;

import java.lang.reflect.Method;
import java.util.Objects;

final class MethodDescriptionPlusReturnType<R> implements MethodDescription<R> {

	private final MethodDescriptionSimple<?> delegate;
	private final Class<R> returnType;

	MethodDescriptionPlusReturnType(MethodDescriptionSimple<?> delegate, Class<R> returnType) {
		this.delegate = Objects.requireNonNull(delegate);
		this.returnType = Objects.requireNonNull(returnType);
	}

	@Override
	public boolean matches(Method method) {
		return method.getReturnType().equals(returnType) && delegate.matches(method);
	}

	@Override
	public int memberOffset() {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delegate.hashCode();
		result = prime * result + returnType.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof MethodDescriptionPlusReturnType)) {
			return false;
		}
		MethodDescriptionPlusReturnType<?> other = (MethodDescriptionPlusReturnType<?>) object;
		return returnType.equals(other.returnType) && delegate.equals(other.delegate);
	}

	@Override
	public String toString() {
		return "MethodDescriptionPlusReturnType [delegate=" + delegate + ", returnType=" + returnType + "]";
	}

}
