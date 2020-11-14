package com.github.ness.check.combat.autoclick;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoClickTest {

	private static final Logger logger = LoggerFactory.getLogger(AutoClickTest.class);
	
	@Test
	public void testStandardDeviationOfSameValues() {
		final long sameValue = ThreadLocalRandom.current().nextLong(Integer.MIN_VALUE, Integer.MAX_VALUE);
		logger.debug("Testing standard deviation using same value of {}", sameValue);

		int count = 1 + ThreadLocalRandom.current().nextInt(31);
		List<Long> sameValuesList = new ArrayList<>(count);
		for (int n = 0; n < count; n++) {
			sameValuesList.add(sameValue);
		}
		assertEquals(sameValue, AutoClick.calculateAverage(sameValuesList));
		assertEquals(0, AutoClick.getStdDevPercent(sameValuesList));
	}
	
	@Test
	public void testCalculationsPreCalculated() {
		List<Long> values = Arrays.asList(12L, 100L, 79L, 22L);

		long average = AutoClick.calculateAverage(values);
		long standardDeviationPercent = AutoClick.getStdDevPercent(values);

		assertEqualToDegreeOfAccuracy(53, (int) average, 1);
		assertEqualToDegreeOfAccuracy(70, (int) standardDeviationPercent, 3);
	}
	
	private static void assertEqualToDegreeOfAccuracy(int expected, int actual, int deviation) {
		assertTrue(Math.abs(expected - actual) < deviation);
	}
	
}
