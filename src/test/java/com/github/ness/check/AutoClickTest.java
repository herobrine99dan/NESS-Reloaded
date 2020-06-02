package com.github.ness.check;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoClickTest {

	private static final Logger logger = LoggerFactory.getLogger(AutoClickTest.class);
	
	@RepeatedTest(5)
	public void testStandardDeviationOfSameValues() {
		long longToTest = ThreadLocalRandom.current().nextLong(10000);
		logger.info("Testing standard deviation using same value of {}", longToTest);
		List<Long> listOfSameValues = new ArrayList<>();
		int max = 1 + ThreadLocalRandom.current().nextInt(14);
		for (int n = 0; n < max; n++) {
			listOfSameValues.add(longToTest);
		}
		Assertions.assertEquals(0, AutoClick.getStdDevPercent(listOfSameValues));
	}
	
}
