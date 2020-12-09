package com.github.ness.reflect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ReflectionMethodsTest extends ReflectionTest {

	private MethodInvoker<?> getMethod(Class<?> clazz, String methodName, Class<?>...parameterTypes)
			throws NoSuchMethodException, SecurityException {
		return new MethodInvokerImpl<>(null, clazz.getDeclaredMethod(methodName, parameterTypes));
	}

	@Test
	public void publicMethods() throws NoSuchMethodException, SecurityException {
		assertEquals(
				getMethod(Superclass.class, "newObject"),
				reflection.getMethod(Superclass.class, MethodDescription.forMethod("newObject", Object.class), 0));
		assertThrows(ReflectionException.class, () -> {
			reflection.getMethod(Superclass.class, MethodDescription.forMethod("newObject", Object.class), randomPositiveIndex());
		});
	}

	@Test
	public void privateMethods() throws NoSuchMethodException, SecurityException {
		assertEquals(
				getMethod(Superclass.class, "lengthOf", String.class),
				reflection.getMethod(Superclass.class, MethodDescription.forMethod("lengthOf", int.class, String.class), 0));
	}

	@Test
	public void inheritedMethods() throws NoSuchMethodException, SecurityException {
		assertEquals(
				getMethod(Superclass.class, "newObject"),
				reflection.getMethod(Subclass.class, MethodDescription.forMethod("newObject", Object.class), 0));
	}

}
