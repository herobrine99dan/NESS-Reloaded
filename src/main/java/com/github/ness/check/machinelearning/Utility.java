package com.github.ness.check.machinelearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utility {
	/**
	 * Covert inputed CSV File to KNNNode
	 * 
	 * @param file         files name of data set
	 * @param characterQty the quantity of characters in data set
	 * @return List of converted KNNNode
	 * @throws IOException
	 */
	public static ArrayList<KNNNode> parseCSVFile(String file, int doubleSize) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(System.getProperty("user.dir"), file));
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<KNNNode> nodes = new ArrayList<>();
		int label = 0;
		while (sc.hasNextLine()) {
			String s = sc.nextLine();
			String[] values = s.split(",");
			double[] doublevalues = new double[doubleSize + 1];
			for (int i = 0; i < doubleSize; i++) {
				doublevalues[i] = Double.valueOf(values[i]);
				//System.out.println("Double: " + values[i]);
			}
			label = Integer.valueOf(values[values.length - 1]);
			//System.out.println("Label " + label);
			nodes.add(new KNNNode(doublevalues, label));
			//System.out.println(s);
		}
		sc.close();
		return nodes;
	}

}
