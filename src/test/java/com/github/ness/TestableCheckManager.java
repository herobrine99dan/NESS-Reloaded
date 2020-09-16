package com.github.ness;

import com.github.ness.check.CheckManager;

/**
 * Can be useful to instantiate checks during test
 * 
 * @author A248
 *
 */
public class TestableCheckManager extends CheckManager {

	private static volatile TestableCheckManager inst;
	
	private TestableCheckManager() {
		super(null);
	}
	
	public static TestableCheckManager get() {
		if (inst == null) {
			synchronized (TestableCheckManager.class) {
				if (inst == null) {
					inst = new TestableCheckManager();
				}
			}
		}
		return inst;
	}

}
