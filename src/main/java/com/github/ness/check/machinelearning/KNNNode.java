package com.github.ness.check.machinelearning;

public class KNNNode {
/**
 * @author mybreeze77
 * from https://github.com/mybreeze77/Simple-KNN-with-Java8
 */
	private double[] characters;
	private int label;
	
	public KNNNode() {
		super();
	}

	public KNNNode(double[] characters, int label) {
		super();
		this.characters = characters;
		this.label = label;
	}

	public double[] getCharacters() {
		return characters;
	}

	public void setCharacters(double[] characters) {
		this.characters = characters;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}
	
}
