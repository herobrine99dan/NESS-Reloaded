package com.github.ness.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.netty.util.internal.ThreadLocalRandom;

public class LongRingBufferTest {

	private LongRingBuffer buffer;
	
	@BeforeEach
	public void setup() {
		buffer = new LongRingBuffer(10);
	}
	
	@Test
	public void testValuesWithinCapacity() {
		int count = 1 + ThreadLocalRandom.current().nextInt(buffer.getCapacity());
		long sum = new SumComputer(count).generateAndComputeSum();

		assertTrue(count <= buffer.getCapacity());
		assertEquals(sum, buffer.sum());
		assertEquals(sum / count, buffer.average());
	}
	
	@Test
	public void testOverwriteOldValues() {
		Random random = ThreadLocalRandom.current();

		int count = buffer.getCapacity() + 1 + random.nextInt(10);
		double sum = new SumComputer(count).generateAndComputeSum();
		
		assertTrue(count > buffer.getCapacity());
		assertEquals(sum, buffer.sum());
		assertEquals(sum / buffer.getCapacity(), buffer.average());
	}
	
	@AfterEach
	public void tearDown() {
		buffer.clear();
	}
	
	private class SumComputer {
		
		private final int count;
		
		SumComputer(int count) {
			this.count = count;
		}
		
		long generateAndComputeSum() {
			Random random = ThreadLocalRandom.current();
			long sum = 0L;
			int underWhichWillBeOverwritten = count - buffer.getCapacity();
			for (int n = 0; n < count; n++) {
				long value = random.nextLong();
				if (n >= underWhichWillBeOverwritten) {
					sum += value;
				}
				buffer.add(value);
			}
			return sum;
		}
		
	}
	
}
