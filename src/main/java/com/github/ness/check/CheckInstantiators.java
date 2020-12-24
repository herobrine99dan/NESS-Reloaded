package com.github.ness.check;

import com.github.ness.reflect.ReflectionException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

final class CheckInstantiators {

	private CheckInstantiators() {}

	static <C extends Check> CheckInstantiator<C> fromConstructor(Constructor<C> constructor) {
		MethodHandle methodHandle;
		try {
			methodHandle = MethodHandles.lookup().unreflectConstructor(constructor);
		} catch (IllegalAccessException ex) {
			throw new ReflectionException(ex);
		}
		return (factory, nessPlayer) -> {
			try {
				@SuppressWarnings("unchecked")
				C check = (C) methodHandle.invoke(factory, nessPlayer);
				return check;
			} catch (RuntimeException | Error ex) {
				throw ex;
			} catch (Throwable ex) {
				throw new ReflectionException("Unable to instantiate check", ex);
			}
		};
	}

}
