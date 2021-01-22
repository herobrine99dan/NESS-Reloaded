
package com.github.ness.utility;

import java.util.List;

public class GraphUtil {
	
	/**
	 * From Frequency
	 * https://github.com/ElevatedDev/Frequency/blob/master/src/main/java/xyz/elevated/frequency/util/GraphUtil.java
	 * @author Elevated
	 *
	 */

	public static class GraphResult {
		private final int positives, negatives;

		public GraphResult(int positives, int negatives) {
			this.positives = positives;
			this.negatives = negatives;
		}

		public int getPositives() {
			return positives;
		}

		public int getNegatives() {
			return negatives;
		}
	}

	public static GraphResult getGraph(List<Double> values) {
		double largest = 0;
		for (double value : values) {
			if (value > largest)
				largest = value;
		}
		final int GRAPH_HEIGHT = 2;
		int positives = 0, negatives = 0;
		for (int i = GRAPH_HEIGHT - 1; i > 0; i -= 1) {
			for (double index : values) {
				double value = GRAPH_HEIGHT * index / largest;

				if (value > i && value < i + 1) {
					++positives;
				} else {
					++negatives;
				}
			}
		}
		return new GraphResult(positives, negatives);
	}
}