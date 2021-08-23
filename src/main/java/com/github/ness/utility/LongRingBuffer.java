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
 * @author A248, herobrine99dan
 *
 */
//TODO Convert this to FloatRingBuffer (Float max value is: 3.40282346638528860e+38 and it is enough)
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
     * This object will be in the same state as when it was created when this
     * method returns
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
        return calculateAverage(longToDoubleArray(this.values));
    }
    
    private double[] longToDoubleArray(long[] array) {
        double[] newOne = new double[array.length];
        for(int i = 0; i < array.length; i++){
            newOne[i] = array[i];
        }
        return newOne;
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
     * Get the median beetween values of the array Median is a Robust Statistic
     * and works even if ~45% of data is messed up.
     *
     * @return the median
     */
    public double median() {
        return calculateMedian(longToDoubleArray(this.values));
    }

    /**
     * Get the median beetween values in long array
     *
     * @param the long array
     * @return the median
     */
    private double calculateMedian(double[] values) {
        Arrays.sort(values);
        double median;
        final int index = values.length / 2;
        if (values.length % 2 == 0) {
            median = (values[index - 1] + values[index]) / 2.0;
            //System.out.println("median: " + median);
            //System.out.println("1: " + values[index-1]);
            //System.out.println("2: " + values[index]);
        } else {
            median = values[index];
        }
        
        return median;
    }
    
     /**
     * Get the mean beetween values in long array
     *
     * @param the long array
     * @return the mean
     */
    private double calculateAverage(double[] values) {
        int size = size();
        if (size == 0) {
            return 0L;
        }
        double sum = 0;
        for(double l : values){
            sum+=l;
        }
        return sum / (double) values.length;
    }

    /**
     * Get the range of variation beetween values
     * This value is heavily affected by Outliers.
     * @return the range of variation
     */
    public long variationRange() {
        return this.biggestValue() - this.smallestValue();
    }
    
    /**
     * Get the mean range of variation beetween values
     * This value is heavily affected by Outliers.
     * @return the range of variation
     */
    public double meanRange() {
        double max = biggestValue();
        double min = smallestValue();
        return (max+min)/2;
    }

    /**
     * Get the Mean Absolute Deviation
     *
     * @return the simple average waste
     */
    public double meanAbsoluteDeviation() {
        double number = 0;

        double mean = average();

        for (long num : values) {
            number += Math.abs(num - mean);
        }
        return number / this.values.length;
    }

    /**
     * Calculate the sum of wastes with median.
     * The sum of wastes with mean is always zero.
     *
     * @return the wastes
     */
    public double calculateWastes() {
        double number = 0;

        double median = median();

        for (long num : values) {
            number += num - median;
        }
        return number / this.values.length;
    }

    /**
     * Get the Skewness of this sample with the Pearson formula.
     * Formula: 3*(x̄-M)/s
     * @return Skewness
     */
    public double getSkewness() {
        double mean = average();
        double median = median();
        return 3 * (mean - median) / standardDeviation();
    }

    public double tuckeyMean() {
        double q2 = median();
        //We have already sorted the values
        double q1 = values.length / 4; //Median of the first part
        double q3 = (values.length / 2 + values.length / 4);
        return (q1 + 2 * q2 + q3) / 4; //T = (Q1 + 2Q2+ Q3)/4
    }
    
     /**
     * Calculate the Mean Absolute Deviation From Median.
     *
     * @return double value
     */
    public double meanAbsoluteDeviationFromMedian() {
        double median = this.median();
        double[] wastes = new double[this.values.length];
        for (int i = 0; i < wastes.length; i++) {
            wastes[i] = Math.abs(values[i] - median);
        }
        return calculateAverage(wastes);
    }

    /**
     * Calculate the MAD (Median Absolute Deviation). 
     * MAD is a robust measure of statistical dispersion.
     *
     * @return
     */
    public double medianAbsoluteDeviation() {
        double median = this.median();
        double[] wastes = new double[this.values.length];
        for (int i = 0; i < wastes.length; i++) {
            wastes[i] = Math.abs(values[i] - median);
        }
        return calculateMedian(wastes);
    }
    
    //TODO Not good
    public long[] detectOutliers() {
        double median = median();
        double mad = medianAbsoluteDeviation();
        System.out.println("mad: " + mad);
        
        for(long l : values) {
            double result = Math.abs(l - median);
            System.out.println("result: " + result);
            if(result <= mad) {
                System.out.println("Non-Outlier val: " + l);
            } else {
                System.out.println("Outlier val: " + l);
            }
        }
        return null;
    }

    /**
     * Get the k costant beetween MAD and Standard Deviation 
     * MAD And Standard Deviation are linked by a relationship: 
     * deviationStandard = k*MAD 
     * For normally distributed data the k is 1.4826 (MAD/deviationStandard=0.6745)
     */
    public double getCostantBeetweenMADandStandardDeviation() {
        return standardDeviation() / medianAbsoluteDeviation();
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
        return variance / (this.values.length - 1); //We will always have samples!
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
        return Math.sqrt(variance());
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
