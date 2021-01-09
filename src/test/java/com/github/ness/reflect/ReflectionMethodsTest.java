package com.github.ness.reflect;

import static com.github.ness.reflect.RandomIndex.randomPositiveIndex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.invoke.MethodHandles;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class ReflectionMethodsTest {

	private MethodInvoker<?> getMethod(Class<?> clazz, String methodName, Class<?>...parameterTypes)
			throws NoSuchMethodException, SecurityException {
		return new MethodInvokerUsingCoreReflection<>(MethodHandles.lookup(), clazz.getDeclaredMethod(methodName, parameterTypes));
	}

	@ParameterizedTest
	@ArgumentsSource(ReflectionArgumentsProvider.class)
	public void publicMethods(Reflection reflection) throws NoSuchMethodException, SecurityException {
		assertEquals(
				getMethod(Superclass.class, "newObject"),
				reflection.getMethod(Superclass.class, MemberDescriptions.forMethod(Object.class, "newObject")));
		assertThrows(MemberNotFoundException.class, () -> {
			reflection.getMethod(Superclass.class, MemberDescriptions
					.forMethod(Object.class, "newObject").withOffset(randomPositiveIndex()));
		});
	}

	@ParameterizedTest
	@ArgumentsSource(ReflectionArgumentsProvider.class)
	public void privateMethods(Reflection reflection) throws NoSuchMethodException, SecurityException {
		assertEquals(
				getMethod(Superclass.class, "lengthOf", String.class),
				reflection.getMethod(Superclass.class, MemberDescriptions.forMethod(int.class, "lengthOf", String.class)));
	}

	@ParameterizedTest
	@ArgumentsSource(ReflectionArgumentsProvider.class)
	public void inheritedMethods(Reflection reflection) throws NoSuchMethodException, SecurityException {
		assertEquals(
				getMethod(Superclass.class, "newObject"),
				reflection.getMethod(Subclass.class, MemberDescriptions.forMethod(Object.class, "newObject")));
	}

	@ParameterizedTest
	@ArgumentsSource(ReflectionArgumentsProvider.class)
	public void methodInSubclass(Reflection reflection) throws NoSuchMethodException, SecurityException {
		assertEquals(
				getMethod(Subclass.class, "methodInSubclass"),
				reflection.getMethod(Subclass.class, MemberDescriptions.forMethod(Object.class, "methodInSubclass")));
	}

}
