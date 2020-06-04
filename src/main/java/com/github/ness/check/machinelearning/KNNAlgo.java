package com.github.ness.check.machinelearning;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public class KNNAlgo {
	/**
	 * @author mybreeze77
	 * from https://github.com/mybreeze77/Simple-KNN-with-Java8
	 */
	
	/**
	 * KNN(k-nearest neighbors) classification algorithm
	 * @param samples provided for training
	 * @param characters provided for testing
	 * @param f the concrete distance algorithm
	 * @return the predicted label
	 */
	public static int classify(List<KNNNode> samples, double[] characters,
			BiFunction<double[], double[], Double> f) {
		final Comparator<KNNNode> comp = (n1, n2) -> Double.compare(
				f.apply(n1.getCharacters(), characters),
				f.apply(n2.getCharacters(), characters));
		return samples.stream().min(comp).get().getLabel();
	}
	
	/**
	 * Euclidean distance function
	 */
	public static BiFunction<double[], double[], Double> euclideanDistanceFunc = DistanceUtil::euclidean;
		
	/**
	 * Manhattan distance function
	 */
	public static BiFunction<double[], double[], Double> manhattanDistanceFunc = DistanceUtil::manhattan;

}
