package com.github.ness.check;

import com.github.ness.NessLogger;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class FactoryLoader {

	private final CheckManager manager;
	private final Set<String> enabledCheckNames;
	
	private static final Logger logger = NessLogger.getLogger(FactoryLoader.class);
	
	FactoryLoader(CheckManager manager, Collection<String> enabledCheckNames) {
		this.manager = manager;
		this.enabledCheckNames = new LinkedHashSet<>(enabledCheckNames);
	}

	/**
	 * Creates all check factories
	 * 
	 * @return an ordered set of all check factories
	 */
	Set<BaseCheckFactory<?>> createAllFactories() {
		Set<BaseCheckFactory<?>> factories = new LinkedHashSet<>();

		factoryLoadLoop:
		for (String checkName : enabledCheckNames) {
			for (ChecksPackage checksPackage : ChecksPackage.values()) {

				BaseCheckFactory<?> factory = loadFactory(checksPackage.prefix(), checkName);
				if (factory != null) {
					factories.add(factory);
					continue factoryLoadLoop;
				}
			}
			logger.log(Level.WARNING, "No check factory found for {0}", checkName);
		}
		for (String requiredCheck : ChecksPackage.REQUIRED_CHECKS) {
			BaseCheckFactory<?> factory = loadFactory("required", requiredCheck);
			if (factory == null) {
				logger.log(Level.WARNING, "No check factory found for required {0}", requiredCheck);
				continue;
			}
			factories.add(factory);
		}
		return factories;
	}

	private <C extends BaseCheck> BaseCheckFactory<C> loadFactory(String packagePrefix,
			String checkName) {
		return new FactoryCreator<C>(manager, packagePrefix, checkName).create();
	}
	
}
