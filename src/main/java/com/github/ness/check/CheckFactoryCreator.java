package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.github.ness.NessPlayer;
import com.github.ness.utility.UncheckedReflectiveOperationException;

import org.bukkit.event.Event;

class CheckFactoryCreator<E extends Event, C extends AbstractCheck<E>> {

	private final CheckManager manager;
	private final String fullClassName;
	
	private Class<C> clazz;
	
	CheckFactoryCreator(CheckManager manager, String packagePrefix, String checkName) {
		this.manager = manager;
		this.fullClassName = "com.github.ness.check." + packagePrefix + '.' + checkName;
	}
	
	@SuppressWarnings("unchecked")
	private Class<C> getCheckClass() {
    	try {
			return (Class<C>) Class.forName(fullClassName);
		} catch (ClassNotFoundException ignored) {
			return null;
		}
	}
	
	private Constructor<C> getConstructor() {
		try {
			return clazz.getDeclaredConstructor(CheckFactory.class, NessPlayer.class);
		} catch (NoSuchMethodException ex) {
			throw new UncheckedReflectiveOperationException(ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	private CheckInfo<E> getCheckInfo() {
		Field checkInfoField;
		try {
			checkInfoField = clazz.getDeclaredField("checkInfo");
		} catch (NoSuchFieldException ex) {
			throw new IllegalArgumentException("Class " + clazz + " does not declare checkInfo", ex);
		}
		try {
			return (CheckInfo<E>) checkInfoField.get(null);
		} catch (IllegalAccessException ex) {
			throw new UncheckedReflectiveOperationException(ex);
		}
	}
	
	/**
	 * Creates the check factory
	 * 
	 * @return the check factory or {@code null} if none in the package exists
	 */
	CheckFactory<C> create() {
		Class<C> checkClass = getCheckClass();
		if (checkClass == null) {
			return null;
		}
		Constructor<C> constructor = getConstructor();
		CheckInfo<E> checkInfo = getCheckInfo();
		if (checkInfo.event != null) {
			return new ListeningCheckFactory<>(constructor, manager, checkInfo);
		}
		return new CheckFactory<>(constructor, manager, checkInfo);
	}
	
}
