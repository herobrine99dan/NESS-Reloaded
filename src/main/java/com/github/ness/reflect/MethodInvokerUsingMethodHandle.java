package com.github.ness.reflect;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

public class MethodInvokerUsingMethodHandle<R> implements MethodInvoker<R> {

	private final MethodHandle methodHandle;

	public MethodInvokerUsingMethodHandle(MethodHandle methodHandle) {
		this.methodHandle = Objects.requireNonNull(methodHandle);
	}

	public MethodInvokerUsingMethodHandle(MethodInvoker<R> delegate) {
		this(delegate.unreflect());
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
	public String toString() {
		return "MethodInvokerUsingMethodHandle [methodHandle=" + methodHandle + "]";
	}

}
