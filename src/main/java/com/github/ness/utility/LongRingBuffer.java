package com.github.ness.utility;

import java.util.Arrays;

/**
 * A ring buffer of {@code long} values which can grow to its maximum size. When the buffer
 * is full, additions remove the oldest entry. <br>
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
	 * This object will be in the same state as when it was created when this method returns
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
	public long average() {
		int size = size();
		if (size == 0) {
			return 0L;
		}
		return sum() / size;
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
	
}
