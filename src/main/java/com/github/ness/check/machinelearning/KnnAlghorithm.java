package com.github.ness.check.machinelearning;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.NonNull;

public class KnnAlghorithm {

	private int xCoord;
	private int yCoord;
	private int zCoord;

	public KnnAlghorithm(int x, int y, int z) {
		xCoord = x;
		yCoord = y;
		zCoord = z;
	}

	public Float getDistanceBetween(@NonNull float[] dataElement1, @NonNull float[] dataElement2) {
		int x1 = Math.round(dataElement1[xCoord]);
		int y1 = Math.round(dataElement1[yCoord]);
		int z1 = Math.round(dataElement1[zCoord]);
		int x2 = Math.round(dataElement2[xCoord]);
		int y2 = Math.round(dataElement2[yCoord]);
		int z2 = Math.round(dataElement2[zCoord]);
		int term1 = (x2 - x1) * (x2 - x1);
		int term2 = (y2 - y1) * (y2 - y1);
		int term3 = (z2 - z1) * (z2 - z1);
		int sum = term1 + term2 + term3;
		String convertedSum = Integer.toString(sum);
		double convertedToDoubleSum = Double.parseDouble(convertedSum);
		double distance = Math.abs(Math.sqrt(convertedToDoubleSum));
		String convertedDistance = Double.toString(distance);
		return Float.parseFloat(convertedDistance);
	}

	public int determineK() {
		int size = data.size();
		String sizeString = Integer.toString(size);
		double sizeDouble = Double.parseDouble(sizeString);
		double root = Math.sqrt(sizeDouble);
		double rawK = root / 2;
		int num = Math.round((float) rawK);
		if (num % 2 != 0) {
			return num;
		} else {
			return num - 1;
		}
	}
	
	public int getClass() {
		for (int index = 0; index < trainingData.size(); index++) {
		    float distance = getDistanceBetween(dataPoint, trainingData.get(index));
		    distances.add(distance);
		    distancesClone.add(distance);
		}

		Collections.sort(distances, new Comparator<Float>() {
		    @Override
		    public int compare(Float o1, Float o2) {
		        return o1.compareTo(o2);
		    }
		});

		int K = determineK() ;
		List<Float >shortestDistances= distances.subList( 0 , K  ) ;
		for ( float element : shortestDistances ) {
		    Integer indexOnClone = distancesClone.indexOf(element);
		    float[] nearestNeighbour = trainingData.get(indexOnClone);
		    if (nearestNeighbour [ 3] == 1) {
		        CLASS_1.add( nearestNeighbour ) ;
		    }
		    else if (nearestNeighbour [ 3] == 2) {
		        CLASS_2.add( nearestNeighbour ) ;
		    }
		}
		if ( CLASS_1.size() > CLASS_2 .size() ){
		    return CLASS_1
		}
		else {
		    return CLASS_2
		}
	}

}
