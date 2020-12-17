package com.github.ness.check;

import com.github.ness.utility.UncheckedReflectiveOperationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

final class CheckInstantiators {

	private CheckInstantiators() {}

	static <C extends Check> CheckInstantiator<C> fromConstructor(Constructor<C> constructor) {
		if (constructor.getParameterCount() == 3) {
			return fromReflectHelperConstructor(constructor);
		}
		return fromStandardConstructor(constructor);
	}

	private static <C extends Check> CheckInstantiator<C> fromStandardConstructor(Constructor<C> constructor) {
		return (factory, nessPlayer) -> {
			try {
				return constructor.newInstance(factory, nessPlayer);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
				throw new UncheckedReflectiveOperationException(
						"Unable to instantiate check " + constructor.getDeclaringClass().getName(), ex);
			}
		};
	}

	private static <C extends Check> CheckInstantiator<C> fromReflectHelperConstructor(Constructor<C> constructor) {
		return (factory, nessPlayer) -> {
			try {
				return constructor.newInstance(factory, nessPlayer,
						factory.getCheckManager().getNess().getReflectHelper());
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
				throw new UncheckedReflectiveOperationException(
						"Unable to instantiate check " + constructor.getDeclaringClass().getName()
								+ " which requires ReflectHelper", ex);
			}
		};
	}

}
