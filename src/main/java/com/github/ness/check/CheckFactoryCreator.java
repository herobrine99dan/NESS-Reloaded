package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.ness.NessPlayer;
import com.github.ness.utility.UncheckedReflectiveOperationException;

import org.bukkit.event.Event;

class CheckFactoryCreator {

	private final CheckManager manager;
	private final Collection<String> enabledCheckNames;
	
	private static final Logger logger = Logger.getLogger(CheckFactoryCreator.class.getName());
	
	CheckFactoryCreator(CheckManager manager, Collection<String> enabledCheckNames) {
		this.manager = manager;
		this.enabledCheckNames = enabledCheckNames;
	}

	Set<CheckFactory<?>> createAllFactories() {
		Set<CheckFactory<?>> factories = new HashSet<>();

		factoryLoadLoop:
		for (String checkName : enabledCheckNames) {
			for (ChecksPackage checksPackage : ChecksPackage.values()) {

				CheckFactory<?> factory = loadFactory(checksPackage.prefix(), checkName);
				if (factory != null) {
					factories.add(factory);
					continue factoryLoadLoop;
				}
			}
			logger.log(Level.WARNING, "No check class found for check " + checkName);
		}
		for (String requiredCheck : ChecksPackage.REQUIRED_CHECKS) {
			CheckFactory<?> factory = loadFactory("required", requiredCheck);
			if (factory == null) {
				logger.log(Level.WARNING, "No check class found for required check " + requiredCheck);
				continue;
			}
			factories.add(factory);
		}
		return factories;
	}

	@SuppressWarnings("unchecked")
	private <E extends Event, C extends AbstractCheck<E>> CheckFactory<C> loadFactory(String packagePrefix,
			String checkName) {
		return (CheckFactory<C>) new IndividualFactoryCreator<>(packagePrefix, checkName).create();
	}
	
	private class IndividualFactoryCreator<E extends Event, C extends AbstractCheck<E>> {
		
		private final String fullClassName;
		
		IndividualFactoryCreator(String packagePrefix, String checkName) {
			fullClassName = "com.github.ness.check." + packagePrefix + '.' + checkName;
		}
		
		@SuppressWarnings("unchecked")
		private Class<C> getCheckClass() {
	    	try {
				return (Class<C>) Class.forName(fullClassName);
			} catch (ClassNotFoundException ignored) {
				return null;
			}
		}
		
		private Constructor<C> getConstructor(Class<C> clazz) {
			try {
				return clazz.getDeclaredConstructor(CheckFactory.class, NessPlayer.class);
			} catch (NoSuchMethodException ex) {
				throw new UncheckedReflectiveOperationException(ex);
			}
		}
		
		@SuppressWarnings("unchecked")
		private CheckInfo<E> getCheckInfo(Class<C> clazz) {
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
			Class<C> clazz = getCheckClass();
			if (clazz == null) {
				return null;
			}
			Constructor<C> constructor = getConstructor(clazz);
			CheckInfo<E> checkInfo = getCheckInfo(clazz);
			if (checkInfo.event != null) {
				return new ListeningCheckFactory<>(constructor, manager, checkInfo);
			}
			return new CheckFactory<>(constructor, manager, checkInfo);
		}
		
	}
	
}
