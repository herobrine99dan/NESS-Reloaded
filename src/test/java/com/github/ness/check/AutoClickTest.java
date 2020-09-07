package com.github.ness.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ness.check.combat.AutoClick;

public class AutoClickTest {

	private static final Logger logger = LoggerFactory.getLogger(AutoClickTest.class);
	
	@RepeatedTest(5)
	public void testStandardDeviationOfSameValues() {
		long longToTest = ThreadLocalRandom.current().nextLong(Integer.MIN_VALUE, Integer.MAX_VALUE);
		logger.debug("Testing standard deviation using same value of {}", longToTest);
		List<Long> listOfSameValues = new ArrayList<>();
		int max = 1 + ThreadLocalRandom.current().nextInt(31);
		for (int n = 0; n < max; n++) {
			listOfSameValues.add(longToTest);
		}
		Assertions.assertEquals(0, AutoClick.getStdDevPercent(listOfSameValues));
	}
	
	@Test
	public void testCalculationsPreCalculated() {
		logger.info("Testing precalculated average and standard deviation");
		List<Long> values = Arrays.asList(12L, 100L, 79L, 22L);
		long average = AutoClick.calculateAverage(values);
		assertToDegreeOfAccuracy(53, (int) average, 1);
		long standardDeviationPercent = AutoClick.getStdDevPercent(values);
		assertToDegreeOfAccuracy(70, (int) standardDeviationPercent, 3);
	}
	
	private static void assertToDegreeOfAccuracy(int expected, int actual, int deviation) {
		Assertions.assertTrue(Math.abs(expected - actual) < deviation);
	}
	
}
