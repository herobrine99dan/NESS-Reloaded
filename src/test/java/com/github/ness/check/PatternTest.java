package com.github.ness.check;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PatternTest {

	public static void main(String[] args) {
		new PatternTest().executeTest();
	}

	@Test
	public void executeTest() {
		List<Double> numbers = Arrays.asList(10d,100d,1000d,10000d);
		System.out.println("Average: " + getAverage(numbers) + " Pattern: " + this.findPatternNumber(numbers));
	}

	public double getAverage(List<Double> list) {
		double sum = 0;
		for (double d : list) {
			sum += d;
		}
		sum /= list.size();
		return sum;
	}

	public double findPatternNumber(List<Double> list) {
		double lastNumber = list.get(list.size() - 1);
		double subtraction = 0;
		for (int i = list.size(); i > 0; i--) {
			double d = list.get(i - 1);
			double currentSubtraction = Math.abs(d - lastNumber);
			if(subtraction != currentSubtraction) {
				subtraction = currentSubtraction;
			}
			lastNumber = d;
		}
		return subtraction;
	}

}
