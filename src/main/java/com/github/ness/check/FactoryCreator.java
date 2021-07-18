package com.github.ness.check;

import com.github.ness.NessPlayer;
import com.github.ness.utility.UncheckedReflectiveOperationException;
import org.bukkit.event.Event;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

class FactoryCreator<C extends BaseCheck> {

	private final CheckManager manager;
	private final String fullClassName;
	private final String factoryClassName;

	FactoryCreator(CheckManager manager, String packagePrefix, String checkName) {
		this.manager = manager;
		fullClassName = "com.github.ness.check." + packagePrefix + '.' + checkName;
		factoryClassName = fullClassName + "Factory";
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> classForNameOrNull(String className) {
		try {
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException ignored) {
			return null;
		}
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
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException ex) {
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
		} catch (NoSuchMethodException ignored) {
		}

		List<Class<?>> checkFactoryClasses = Arrays.asList(CheckFactory.class, ListeningCheckFactory.class,
				PacketCheckFactory.class, MultipleListeningCheckFactory.class);
		for (Class<?> checkFactoryClass : checkFactoryClasses) {
			try {
				return checkClass.getDeclaredConstructor(checkFactoryClass, NessPlayer.class);
			} catch (NoSuchMethodException ignored) {
			}
		}
		throw new IllegalStateException("Unable to find check constructor");
	}

	private CheckInfo getCheckInfo(Class<C> checkClass) {
		Field checkInfoField;
		try {
			checkInfoField = checkClass.getDeclaredField("checkInfo");
		} catch (NoSuchFieldException ex) {
			throw new UncheckedReflectiveOperationException(
					"Check " + checkClass + " must declare checkInfo or use its own factory", ex);
		}
		try {
			return (CheckInfo) checkInfoField.get(null);
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
		CheckInfo checkInfo = getCheckInfo(checkClass);
		Constructor<C> constructor = getCheckConstructor(checkClass);

		if (ListeningCheck.class.isAssignableFrom(checkClass)) {
			if (!(checkInfo instanceof ListeningCheckInfo)) {
				throw new IllegalStateException("Check " + checkClass.getName() + " has mismatched check info");
			}
			ListeningCheckInfo<?> listeningCheckInfo = (ListeningCheckInfo<?>) checkInfo;
			return createListening(constructor, listeningCheckInfo);
		}
		if (checkInfo instanceof ListeningCheckInfo) {
			throw new IllegalStateException("Check " + checkClass.getName() + " has mismatched check info");
		}
		if (PacketCheck.class.isAssignableFrom(checkClass)) {
			return createPacket(constructor, checkInfo);
		}
		if (MultipleListeningCheck.class.isAssignableFrom(checkClass)) {
			if (!(checkInfo instanceof MultipleListeningCheckInfo))
				throw new IllegalStateException("Check " + checkClass.getName() + " has mismatched check info");

			return createMultipleEventListener(constructor, (MultipleListeningCheckInfo) checkInfo);
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
		return (BaseCheckFactory<C>) createListeningChecked((Constructor<L>) constructor,
				(ListeningCheckInfo<E>) listeningCheckInfo);
	}

	private <L extends ListeningCheck<E>, E extends Event> ListeningCheckFactory<L, E> createListeningChecked(
			Constructor<L> constructor, ListeningCheckInfo<E> listeningCheckInfo) {
		return new ListeningCheckFactory<>(CheckInstantiators.fromConstructor(constructor),
				constructor.getDeclaringClass().getSimpleName(), manager, listeningCheckInfo);
	}

	// PacketCheckFactory

	@SuppressWarnings("unchecked")
	private <L extends PacketCheck> BaseCheckFactory<C> createPacket(Constructor<C> constructor, CheckInfo checkInfo) {
		return (BaseCheckFactory<C>) createPacketChecked((Constructor<L>) constructor, checkInfo);
	}

	private <L extends PacketCheck> PacketCheckFactory<L> createPacketChecked(Constructor<L> constructor,
			CheckInfo checkInfo) {
		return new PacketCheckFactory<>(CheckInstantiators.fromConstructor(constructor),
				constructor.getDeclaringClass().getSimpleName(), manager, checkInfo);
	}

	@SuppressWarnings("unchecked")
	private <L extends MultipleListeningCheck> BaseCheckFactory<C> createMultipleEventListener(
			Constructor<C> constructor, MultipleListeningCheckInfo checkInfo) {
		return (BaseCheckFactory<C>) createMultipleEventListenerChecked((Constructor<L>) constructor, checkInfo);
	}

	private <L extends MultipleListeningCheck> MultipleListeningCheckFactory<L> createMultipleEventListenerChecked(
			Constructor<L> constructor, MultipleListeningCheckInfo checkInfo) {
		return new MultipleListeningCheckFactory<>(CheckInstantiators.fromConstructor(constructor),
				constructor.getDeclaringClass().getSimpleName(), manager, checkInfo);
	}

	// CheckFactory

	@SuppressWarnings("unchecked")
	private <L extends Check> BaseCheckFactory<C> create(Constructor<C> constructor, CheckInfo checkInfo) {
		return (BaseCheckFactory<C>) createChecked((Constructor<L>) constructor, checkInfo);
	}

	private <L extends Check> CheckFactory<L> createChecked(Constructor<L> constructor, CheckInfo checkInfo) {
		return new CheckFactory<>(CheckInstantiators.fromConstructor(constructor),
				constructor.getDeclaringClass().getSimpleName(), manager, checkInfo);
	}

}
