package com.github.ness;

import org.junit.jupiter.api.Test;

public class SetBackTest {

	long lastSetBack;

	@Test
	public void executeTest() throws InterruptedException {
		for (int i = 0; i < 1000; i++) {
			final long current = System.nanoTime() / 1000_000L;
			System.out.println("SetBack!" + (current - lastSetBack));
			if ((current - lastSetBack) > 20) {
				System.out.println("SetBack!");
			}
			lastSetBack = current;
		}
	}

}
