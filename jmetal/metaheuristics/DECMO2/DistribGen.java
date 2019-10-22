/**
 * DistribGen.java
 *
 * @author Ciprian Zavoianu
 * @version 1.0
 */
package jmetal.metaheuristics.DECMO2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DistribGen {

	public static void main(String[] args) {

		System.out.println("Distribution computation started.");

		DistribGen dg = new DistribGen();

		int n = 4;
		dg.createDistribution(n, n * 10, "input\\TestWeights.dat");

		System.out.println("Distribution computation ended.");
	}

	private double createDistrib(int dimensions, int numberOfSamples, int steps, boolean mockRun, String fileName) {

		List<List<Integer>> distrib = new ArrayList<List<Integer>>();
		List<List<Double>> finalDistrib = new ArrayList<List<Double>>();

		for (int i = 0; i <= steps; i++) {
			List<Integer> sample = new ArrayList<Integer>();
			sample.add(i);
			distrib.add(sample);
		}
		for (List<Integer> sample : distrib) {
			for (int i = 1; i < dimensions; i++) {
				sample.add(0);
			}
		}

		boolean addedNewSamples = true;
		int modifColumn = 1;

		while (addedNewSamples == true) {
			addedNewSamples = false;
			List<List<Integer>> newDistrib = new ArrayList<List<Integer>>();
			for (List<Integer> sample : distrib) {
				int sum = 0;
				for (int i = 0; i < sample.size(); i++) {
					sum += sample.get(i);
				}
				for (int i = 0; i <= steps; i++) {
					if ((modifColumn + 1) < dimensions) {
						if ((sum + i) <= steps) {
							List<Integer> newSample = new ArrayList<Integer>();
							newSample.addAll(sample);
							newSample.set(modifColumn, i);
							newDistrib.add(newSample);
							addedNewSamples = true;
						}
					} else {
						if ((sum + i) == steps) {
							List<Integer> newSample = new ArrayList<Integer>();
							newSample.addAll(sample);
							newSample.set(modifColumn, i);
							newDistrib.add(newSample);
							addedNewSamples = true;
						}
					}
				}
			}

			distrib = newDistrib;

			modifColumn++;
			if ((modifColumn + 1) > dimensions) {
				addedNewSamples = false;
			}
		}

		int originalSetSize = distrib.size();

		Random rnd = new Random(System.currentTimeMillis());
		while (distrib.size() > numberOfSamples) {
			int ind = rnd.nextInt(distrib.size());
			distrib.remove(ind);
		}

		if (!mockRun) {
			try {
				File outputFile = new File(fileName);
				File dir = new File(outputFile.getParent());
				if (!dir.exists() && !dir.mkdirs()) {
					System.out.println("Could not create directory path: ");
				}
				if (!outputFile.exists()) {
					outputFile.createNewFile();
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, false));
				for (List<Integer> sample : distrib) {
					String s = "";
					List<Double> finalSample = new ArrayList<Double>();
					for (int i = 0; i < sample.size(); i++) {
						finalSample.add((sample.get(i) * 1.0) / steps);
						s += String.format("%.6f", finalSample.get(i)) + " ";
					}
					finalDistrib.add(finalSample);
					// System.out.println(s);
					bw.write(s + "\n");
				}
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		double percentRem = (1.0 - (distrib.size() * 1.0 / originalSetSize)) * 100.0;

		return percentRem;
	}

	public void createDistribution(int dimensions, int numberOfSamples, String fileName) {
		int steps = 2;
		double remaining = 0.0;
		boolean stop = false;
		while (!stop) {
			remaining = createDistrib(dimensions, numberOfSamples, steps, true, fileName);
			if (remaining > 0.0) {
				stop = true;
			} else {
				steps++;
			}
		}
		System.out.println(
				"Required steps: " + steps + "\n" + "Rmoved samples: " + String.format("%.2f", remaining) + "%");

		createDistrib(dimensions, numberOfSamples, steps, false, fileName);
	}
}
