package com.github.ness.utility;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongRingBufferMathTest {
	
	private LongRingBuffer buffer;
	private static final Logger logger = LoggerFactory.getLogger(LongRingBufferMathTest.class.getSimpleName());
	
	public static void main(String[] args) {
		LongRingBufferMathTest obj = new LongRingBufferMathTest();
		obj.setup();
		obj.test();
	}
	
	@BeforeEach
	public void setup() {
		buffer = new LongRingBuffer(4);
                Arrays.asList(225,250,25,235).forEach(l -> {
                    buffer.add(l);
            });
	}
	
	@Test
	public void test() {
		logger.info("Smallest: " + buffer.smallestValue());
                logger.info("Biggest: " + buffer.biggestValue());
		logger.info("Range: " + buffer.variationRange());
                logger.info("Mean Range: " + buffer.meanRange());
                logger.info("Mean Absolute Deviation: " + buffer.meanAbsoluteDeviation());
                logger.info("Wastes sum from Median: " + buffer.calculateWastes());
                logger.info("Skewness: " + buffer.getSkewness());
                logger.info("Tuckey Mean: " + buffer.tuckeyMean());
                logger.info("Mean Absolute Deviation From Median: " + buffer.meanAbsoluteDeviationFromMedian());
                logger.info("Median Absolute Deviation: " + buffer.medianAbsoluteDeviation());
                logger.info("Costant beetween MAD and Standard Deviation: " + buffer.getCostantBeetweenMADandStandardDeviation());
		logger.info("Variance: " + buffer.variance());
                logger.info("Coefficient Of Variation: " + buffer.coefficientOfVariation());
                logger.info("Standard Deviation: " + buffer.standardDeviation());
                logger.info("Average: " + buffer.average());
                logger.info("Median: " + buffer.median());
                buffer.detectOutliers();
	}

}
