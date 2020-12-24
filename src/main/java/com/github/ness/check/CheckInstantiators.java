package com.github.ness.check;

import com.github.ness.utility.UncheckedReflectiveOperationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

final class CheckInstantiators {

	private CheckInstantiators() {}

	static <C extends Check> CheckInstantiator<C> fromConstructor(Constructor<C> constructor) {
		return (factory, nessPlayer) -> {
			try {
				return constructor.newInstance(factory, nessPlayer);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
				throw new UncheckedReflectiveOperationException(
						"Unable to instantiate check " + constructor.getDeclaringClass().getName(), ex);
			}
		};
	}

}
