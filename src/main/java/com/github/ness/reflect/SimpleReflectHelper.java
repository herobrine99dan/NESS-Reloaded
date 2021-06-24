package com.github.ness.reflect;

import com.github.ness.reflect.locator.ClassLocator;

public final class SimpleReflectHelper implements ReflectHelper {

	private final ClassLocator locator;
	private final Reflection reflection;

	public SimpleReflectHelper(ClassLocator locator, Reflection reflection) {
		this.locator = locator;
		this.reflection = reflection;
	}

	@Override
	public Class<?> getNmsClass(String className) {
		return locator.getNmsClass(className);
	}

	@Override
	public Class<?> getObcClass(String className) {
		return locator.getObcClass(className);
	}

	@Override
	public <T> FieldInvoker<T> getField(Class<?> clazz, FieldDescription<T> description) {
		return reflection.getField(clazz, description);
	}

	@Override
	public <R> MethodInvoker<R> getMethod(Class<?> clazz, MethodDescription<R> description) {
		return reflection.getMethod(clazz, description);
	}

}
