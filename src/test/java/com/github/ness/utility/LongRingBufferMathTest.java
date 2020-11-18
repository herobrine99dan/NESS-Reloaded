package com.github.ness.utility;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ness.check.combat.autoclick.AutoClickTest;

public class LongRingBufferMathTest {
	
	private LongRingBuffer buffer;
	private static final Logger logger = LoggerFactory.getLogger(LongRingBufferMathTest.class);
	
	public static void main(String[] args) {
		LongRingBufferMathTest obj = new LongRingBufferMathTest();
		obj.setup();
		obj.test();
	}
	
	@BeforeEach
	public void setup() {
		buffer = new LongRingBuffer(6);
		for(long l : Arrays.asList(1,2,3,4,5,6)) {
			buffer.add(l);
		}
	}
	
	@Test
	public void test() {
		logger.info("Smallest: " + buffer.smallestValue());
		logger.info("Range: " + buffer.variationRange());
		logger.info("Average: " + buffer.median());
	}

}
