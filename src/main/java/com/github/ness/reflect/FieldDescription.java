package com.github.ness.reflect;

import java.lang.reflect.Field;

/**
 * Description of a field
 * 
 * @author A248
 *
 * @param <T> the type of the field
 */
public interface FieldDescription<T> extends MemberDescription<Field> {

	@Override
	default FieldDescription<T> withOffset(int memberOffset) {
		if (memberOffset == memberOffset()) {
			return this;
		}
		return new FieldDescriptionWithOffset<>(this, memberOffset);
	}

}
