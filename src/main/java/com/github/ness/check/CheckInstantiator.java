package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.github.ness.NessPlayer;
import com.github.ness.utility.UncheckedReflectiveOperationException;

/**
 * Instantiator of new checks
 * 
 * @author A248
 *
 * @param <C> the check type
 */
@FunctionalInterface
public interface CheckInstantiator<C extends Check> {

	/**
	 * Creates a new check with the associated factory and ness player. Most implementations
	 * will simply pass these arguments to a constructor.
	 * 
	 * @param factory the factory
	 * @param nessPlayer the ness player
	 * @return the check
	 */
	C newCheck(CheckFactory<C> factory, NessPlayer nessPlayer);
	
}
