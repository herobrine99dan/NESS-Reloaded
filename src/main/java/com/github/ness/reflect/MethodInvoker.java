package com.github.ness.reflect;

import java.lang.invoke.MethodHandle;

public interface MethodInvoker<R> {

	R invoke(Object object, Object...arguments);

	/**
	 * Obtains a method handle which may be used to more efficiently invoke this method
	 * 
	 * @return a method handle for the method
	 */
	MethodHandle unreflect();

}
