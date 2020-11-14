package com.github.ness.check.combat.autoclick;

import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

abstract class IntPairSerialiser<T> implements ValueSerialiser<T> {

	@Override
	public T deserialise(FlexibleType flexibleType) throws BadValueException {
		String[] info = flexibleType.getString().split(":", 2);
		try {
			return fromInts(Integer.parseInt(info[0]), Integer.parseInt(info[1]));
		} catch (NumberFormatException ex) {
			throw flexibleType.badValueExceptionBuilder().cause(ex).build();
		}
	}

	@Override
	public Object serialise(T value, Decomposer decomposer) {
		int[] toInts = toInts(value);
		return toInts[0] + ":" + toInts[1];
	}
	
	abstract T fromInts(int value1, int value2);
	
	abstract int[] toInts(T value);
	
}
