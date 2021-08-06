package com.github.ness.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A ring buffer of {@code long} values which can grow to its maximum size. When
 * the buffer is full, additions remove the oldest entry. <br>
 * <br>
 * Not synchronized and not thread safe.
 * 
 * @author A248
 *
 */
public class LongRingBuffer {

	private final long[] values;
	/**
	 * The index after the last element, at which new elements will be written
	 */
	private int writerIndex;
	private boolean full;

	/**
	 * Creates from a fixed buffer size
	 * 
	 * @param capacity the maximum amount of values to hold
	 * @throws IllegalArgumentException if the capacity is less than 1
	 */
	public LongRingBuffer(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Capacity must be at least 1");
		}
		values = new long[capacity];
	}

	/**
	 * Gets the capacity this buffer was created with
	 * 
	 * @return the capacity
	 */
	public int getCapacity() {
		return values.length;
	}

	private int nextWriterIndex() {
		int writerIndex = this.writerIndex;
		writerIndex++;
		if (writerIndex == values.length) {
			full = true;
			return 0;
		}
		return writerIndex;
	}

	/**
	 * Adds a value. If full, removes/overwrites the oldest value
	 * 
	 * @param value the long value
	 */
	public void add(long value) {
		values[writerIndex] = value;
		writerIndex = nextWriterIndex();
	}

	/**
	 * Clears and resets. <br>
	 * <br>
	 * This object will be in the same state as when it was created when this method
	 * returns
	 * 
	 */
	public void clear() {
		Arrays.fill(values, 0);
		writerIndex = 0;
		full = false;
	}

	/**
	 * Determines whether the buffer is empty
	 * 
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty() {
		return !full && writerIndex == 0;
	}

	/**
	 * Gets the amount of values in the buffer
	 * 
	 * @return the size of the current values
	 */
	public int size() {
		if (full) {
			return values.length;
		}
		return writerIndex;
	}

	/**
	 * Computes the average value
	 * 
	 * @return the average
	 */
	public double average() {
		int size = size();
		if (size == 0) {
			return 0L;
		}
		return (double) sum() / (double) size;
	}

	/**
	 * Get the highest value
	 * 
	 * @return the highest value
	 */
	public long biggestValue() {
		long max = values[0];
		for (long value : values) {
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	/**
	 * Get the smallest value
	 * 
	 * @return the smallest value
	 */
	public long smallestValue() {
		long min = values[0];
		for (long value : values) {
			if (value < min) {
				min = value;
			}
		}
		return min;
	}

	/**
	 * Get the median beetween values
	 * 
	 * @return the median
	 */
	public long median() {
		return calculateMedian(this.values);
	}
	
	/**
	 * Get the median beetween values
	 * 
	 * @param the long array
	 * @return the median
	 */
	public long calculateMedian(long[] values) {
		Arrays.sort(values);
		long median;
		if (values.length % 2 == 0) {
			median = (values[values.length / 2] + values[values.length / 2 + 1]) / 2;
		} else {
			median = values[values.length / 2];
		}
		return median;
	}

	/**
	 * Get the range of variation beetween values
	 * 
	 * @return the range of variation
	 */
	public long variationRange() {
		return this.biggestValue() - this.smallestValue();
	}

	/**
	 * Get the simple average waste
	 * 
	 * @return the simple average waste
	 */
	public double simpleAverageWaste() {
		double number = 0;

		double mean = average();

		for (long num : values) {
			number += Math.abs(num - mean);
		}
		return number / this.values.length;
	}

	/**
	 * Calculate the wastes from median/average
	 * 
	 * @param boolean median, should we use median or average?
	 * 
	 * @return the wastes
	 */
	public double calculateWastes(boolean median) {
		double number = 0;

		double mean = median ? median() : average();

		for (long num : values) {
			number += num - mean;
		}
		return number / (double) this.values.length;
	}

	/**
	 * Calculate the MAD. 
	 * MAD is a more robust than Standard Deviation measure of statistical dispersion.
	 * MAD And Standard Deviation are linked by a relationship: deviationStandard = k*MAD
	 * For normally distributed data the k is 1.4826 (MAD/deviationStandard=0.6745)
	 * @return
	 */
	public long calculateMAD() {
		long median = this.median();
		long[] wastes = new long[this.values.length];
		for (int i = 0; i < wastes.length; i++) {
			wastes[i] = Math.abs(values[i] - median);
		}
		return calculateMedian(wastes);
	}

	/**
	 * Get variance beetween values
	 * 
	 * @return the variance
	 */
	public double variance() {
		double variance = 0;

		double mean = average();

		for (long num : values) {
			variance += Math.pow(num - mean, 2);
		}
		return (double) variance / (double) (this.values.length - 1); //We will always have samples!
	}

	/**
	 * Get Standard Score of an x number
	 * 
	 * @param x
	 * @return how many standard deviations an element is from the mean.
	 */
	public double standardScore(long x) {
		return (x - average()) / standardDeviation();
	}

	public double coefficientOfVariation() {
		return this.standardDeviation() / this.average();
	}

	/**
	 * Get standard deviation beetween values
	 * 
	 * @return the standard Deviation
	 */
	public double standardDeviation() {
		return (double) Math.sqrt(variance());
	}

	public List<Long> calculateDelta() {
		if (values.length < 1) {
			throw new IllegalArgumentException("List must contains at least two values!");
		}
		List<Long> out = new ArrayList<Long>();
		for (int i = 1; i <= values.length - 1; i++) {
			out.add(values[i] - values[i - 1]);
		}
		return out;
	}

	/**
	 * Computes the sum of all the values
	 * 
	 * @return the sum
	 */
	public long sum() {
		long sum = 0L;
		for (long value : values) {
			sum += value;
		}
		return sum;
	}

	public long[] getValues() {
		return values;
	}

}
