package com.github.ness.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;

/**
 * Reflective setter/getter for a field
 * 
 * @author A248
 *
 * @param <T> the type of the field
 */
public interface FieldInvoker<T> {

	void set(Object object, T value);

	T get(Object object);

	/**
	 * Obtains a method handle which may be used to more efficiently get the value
	 * of this field. <br>
	 * <br>
	 * The method handle is created as if by {@link Lookup#unreflectGetter(Field)}
	 * 
	 * @return a method handle for getting this field
	 */
	MethodHandle unreflectGetter();

}
