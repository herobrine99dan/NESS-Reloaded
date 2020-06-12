package com.github.ness.check;

import java.util.Arrays;

import com.github.ness.check.killaura.heuristics.AngleHeuristics;

public class AngleHeuristicsTest {

	public static void main(String[] args) {
		AngleHeuristics alghorithm = new AngleHeuristics(Arrays.asList(0.75f, 0.75f, 0.59f));
		/*
		 * float legit[]={0.72f,0.92f,0.87f,0.45f,0.87f,0.98f}; float
		 * b[]={0.99f,0.91f,0.89f,0.94f,0.87f,0.98f}; float
		 * hack[]={0.30f,0.40f,0.25f,0.45f,0.65f,0.98f}; alghorithm.getResult(b);
		 * alghorithm.getResult(legit); alghorithm.getResult(hack);
		 */
		float legit[] = { 0.72f, 0.92f, 0.45f };
		float b[] = { 0.94f, 0.87f, 0.98f };
		float hack[] = { 0.30f, 0.25f, 0.98f };
		alghorithm.getMineResult(b);
		alghorithm.getMineResult(legit);
		alghorithm.getMineResult(hack);
	}

}
