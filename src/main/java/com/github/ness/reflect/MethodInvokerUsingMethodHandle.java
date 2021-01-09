package com.github.ness.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Objects;

public class MethodInvokerUsingMethodHandle<R> implements MethodInvoker<R> {

	private final Method method;
	private final MethodHandle methodHandle;

	public MethodInvokerUsingMethodHandle(Method method, MethodHandle methodHandle) {
		this.method = Objects.requireNonNull(method);
		this.methodHandle = Objects.requireNonNull(methodHandle);
	}

	public MethodInvokerUsingMethodHandle(MethodInvoker<R> delegate) {
		this(delegate.reflect(), delegate.unreflect());
	}

	@SuppressWarnings("unchecked")
	@Override
	public R invoke(Object object, Object... arguments) {
		try {
			return (R) methodHandle.bindTo(object).invokeWithArguments(arguments);
		} catch (RuntimeException | Error ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new ReflectionException(ex);
		}
	}

	@Override
	public MethodHandle unreflect() {
		return methodHandle;
	}

	@Override
	public Method reflect() {
		return method;
	}

	@Override
	public int hashCode() {
		return 31 + reflect().hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return this == object || object instanceof MethodInvoker
				&& reflect().equals(((MethodInvoker<?>) object).reflect());
	}

	@Override
	public String toString() {
		return "MethodInvokerUsingMethodHandle{" +
				"method=" + method +
				", methodHandle=" + methodHandle +
				'}';
	}
}
