package com.github.ness.reflect;

import java.lang.reflect.Method;

/**
 * Description of a method
 * 
 * @author A248
 *
 * @param <R> the return type
 */
public interface MethodDescription<R> extends MemberDescription<Method> {

	@Override
	default MethodDescription<R> withOffset(int memberOffset) {
		if (memberOffset == memberOffset()) {
			return this;
		}
		return new MethodDescriptionWithOffset<>(this, memberOffset);
	}

}
