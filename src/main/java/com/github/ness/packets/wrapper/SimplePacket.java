package com.github.ness.packets.wrapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class SimplePacket {
	private final Object packet;

	public SimplePacket(Object packet) {
		this.packet = packet;
	}

	public String getName() {
		return this.packet.getClass().getSimpleName();
	}

	public Object getPacket() {
		return this.packet;
	}

	Object getField(String field) {
		throw new UnsupportedOperationException("Must migrate to PacketCheck/PacketCheckFactory");
	}

	<T> Field getField(Class<?> target, Class<T> fieldType, int index) {
		List<Field> fields = Arrays.asList(target.getDeclaredFields());
		for(Field f : fields) {
			if (fieldType.isAssignableFrom(f.getType()) && index-- <= 0) {
				f.setAccessible(true);
				return f;
			}
		}
		// We Search in parent classes
		if (target.getSuperclass() != null)
			return getField(target.getSuperclass(),fieldType, index);

		throw new IllegalArgumentException("Cannot find field with type " + fieldType);
	}
	
	public void process() throws IllegalArgumentException, IllegalAccessException {
		return;
	}

	Object getDeclaredField(String field) {
		throw new UnsupportedOperationException("Must migrate to PacketCheck/PacketCheckFactory");
	}

}
