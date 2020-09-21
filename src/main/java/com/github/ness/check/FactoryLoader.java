package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.utility.UncheckedReflectiveOperationException;

import org.bukkit.event.Event;

class FactoryLoader {

	private final CheckManager manager;
	private final Collection<String> enabledCheckNames;
	
	private static final Logger logger = NessLogger.getLogger(FactoryLoader.class);
	
	FactoryLoader(CheckManager manager, Collection<String> enabledCheckNames) {
		this.manager = manager;
		this.enabledCheckNames = enabledCheckNames;
	}

	Set<BaseCheckFactory<?>> createAllFactories() {
		Set<BaseCheckFactory<?>> factories = new HashSet<>();

		factoryLoadLoop:
		for (String checkName : enabledCheckNames) {
			for (ChecksPackage checksPackage : ChecksPackage.values()) {

				BaseCheckFactory<?> factory = loadFactory(checksPackage.prefix(), checkName);
				if (factory != null) {
					factories.add(factory);
					continue factoryLoadLoop;
				}
			}
			logger.log(Level.WARNING, "No check factory found for " + checkName);
		}
		for (String requiredCheck : ChecksPackage.REQUIRED_CHECKS) {
			BaseCheckFactory<?> factory = loadFactory("required", requiredCheck);
			if (factory == null) {
				logger.log(Level.WARNING, "No check factory found for required " + requiredCheck);
				continue;
			}
			factories.add(factory);
		}
		return factories;
	}

	private <C extends BaseCheck> BaseCheckFactory<C> loadFactory(String packagePrefix,
			String checkName) {
		return new IndividualFactoryCreator<C>(packagePrefix, checkName).create();
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Class<T> classForNameOrNull(String className) {
		try {
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException ignored) {
			return null;
		}
	}
	
	private class IndividualFactoryCreator<C extends BaseCheck> {
		
		private final String fullClassName;
		private final String factoryClassName;
		
		IndividualFactoryCreator(String packagePrefix, String checkName) {
			fullClassName = "com.github.ness.check." + packagePrefix + '.' + checkName;
			factoryClassName = fullClassName + "Factory";
		}
		
		/*
		 * Subclassed factories
		 */
		
		private Class<? extends BaseCheckFactory<C>> getFactoryClass() {
	    	return classForNameOrNull(factoryClassName);
		}
		
		private BaseCheckFactory<C> getFactoryUsingItsConstructor(Class<? extends BaseCheckFactory<C>> factoryClass) {
			try {
				return factoryClass.getDeclaredConstructor(CheckManager.class).newInstance(manager);
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
				throw new UncheckedReflectiveOperationException(
						"Unable to instantiate factory " + factoryClass.getSimpleName(), ex);
			}
		}
		
		/*
		 * Subclassed checks using standard factories
		 */
		
		private Class<C> getCheckClass() {
	    	return classForNameOrNull(fullClassName);
		}
		
		private Constructor<C> getCheckConstructor(Class<C> checkClass) {
			try {
				return checkClass.getDeclaredConstructor(BaseCheckFactory.class);
			} catch (NoSuchMethodException ignored) {}

			Class<?>[] checkFactoryClasses = new Class<?>[] {CheckFactory.class, ListeningCheckFactory.class};
			for (Class<?> checkFactoryClass : checkFactoryClasses) {
				try {
					return checkClass.getDeclaredConstructor(checkFactoryClass, NessPlayer.class);
				} catch (NoSuchMethodException ignored) {}
			}
			throw new IllegalStateException("Unable to find check constructor");
		}
		
		private CheckInfo<?> getCheckInfo(Class<C> checkClass) {
			Field checkInfoField;
			try {
				checkInfoField = checkClass.getDeclaredField("checkInfo");
			} catch (NoSuchFieldException ex) {
				throw new UncheckedReflectiveOperationException(
						"Check " + checkClass + " must declare checkInfo or use its own factory", ex);
			}
			try {
				return (CheckInfo<?>) checkInfoField.get(null);
			} catch (IllegalAccessException ex) {
				throw new UncheckedReflectiveOperationException("CheckInfo not accessible", ex);
			}
		}
		
		/**
		 * Creates the check factory
		 * 
		 * @return the check factory or {@code null} if none in the package exists
		 */
		BaseCheckFactory<C> create() {
			Class<? extends BaseCheckFactory<C>> factoryClass = getFactoryClass();
			if (factoryClass != null) {
				/*
				 * Subclassed factory
				 */
				return getFactoryUsingItsConstructor(factoryClass);
			}

			Class<C> checkClass = getCheckClass();
			if (checkClass == null) {
				// Check does not exist
				return null;
			}
			/*
			 * Subclassed check, standard factory
			 */
			CheckInfo<?> checkInfo = getCheckInfo(checkClass);
			Constructor<C> constructor = getCheckConstructor(checkClass);

			if (checkInfo instanceof ListeningCheckInfo) {
				ListeningCheckInfo<?> listeningCheckInfo = (ListeningCheckInfo<?>) checkInfo;
				return createListening(constructor, listeningCheckInfo);
			}
			return create(constructor, checkInfo);
		}
		
		/*
		 * Solutions to troublesome generics
		 * 
		 * L is used to in place of C
		 */
		
		// ListeningCheckFactory
		
		@SuppressWarnings("unchecked")
		private <L extends ListeningCheck<E>, E extends Event> BaseCheckFactory<C> createListening(
				Constructor<C> constructor, ListeningCheckInfo<?> listeningCheckInfo) {
			return (BaseCheckFactory<C>) createListening0((Constructor<L>) constructor, (ListeningCheckInfo<E>) listeningCheckInfo);
		}
		
		private <L extends ListeningCheck<E>, E extends Event> ListeningCheckFactory<L, E> createListening0(
				Constructor<L> constructor, ListeningCheckInfo<E> listeningCheckInfo) {
			return new ListeningCheckFactory<L, E>(constructor, manager, listeningCheckInfo);
		}
		
		// CheckFactory
		
		@SuppressWarnings("unchecked")
		private <L extends Check> BaseCheckFactory<C> create(Constructor<C> constructor, CheckInfo<?> checkInfo) {
			return (BaseCheckFactory<C>) create0((Constructor<L>) constructor, checkInfo);
		}
		
		private <L extends Check> CheckFactory<L> create0(Constructor<L> constructor, CheckInfo<?> checkInfo) {
			return new CheckFactory<>(constructor, manager, checkInfo);
		}
		
	}
	
}
