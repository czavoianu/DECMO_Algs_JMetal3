/**
 * ComparativeRuntimeEvaluator.java
 *
 * Class for performing comparative runtime analyses using pre-saved data points (only implemented metric is the hypervolume)
 *
 * @author Ciprian Zavoianu
 * @version 1.0
 */
package jmetal.metaheuristics.baseline.performanceComparator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jmetal.base.Problem;
import jmetal.problems.Kursawe;
import jmetal.problems.DTLZ.DTLZ1;
import jmetal.problems.DTLZ.DTLZ2;
import jmetal.problems.DTLZ.DTLZ3;
import jmetal.problems.DTLZ.DTLZ4;
import jmetal.problems.DTLZ.DTLZ5;
import jmetal.problems.DTLZ.DTLZ6;
import jmetal.problems.DTLZ.DTLZ7;
import jmetal.problems.LZ09.LZ09_F1;
import jmetal.problems.LZ09.LZ09_F2;
import jmetal.problems.LZ09.LZ09_F3;
import jmetal.problems.LZ09.LZ09_F4;
import jmetal.problems.LZ09.LZ09_F5;
import jmetal.problems.LZ09.LZ09_F6;
import jmetal.problems.LZ09.LZ09_F7;
import jmetal.problems.LZ09.LZ09_F8;
import jmetal.problems.LZ09.LZ09_F9;
import jmetal.problems.WFG.WFG1;
import jmetal.problems.WFG.WFG2;
import jmetal.problems.WFG.WFG3;
import jmetal.problems.WFG.WFG4;
import jmetal.problems.WFG.WFG5;
import jmetal.problems.WFG.WFG6;
import jmetal.problems.WFG.WFG7;
import jmetal.problems.WFG.WFG8;
import jmetal.problems.WFG.WFG9;
import jmetal.problems.ZDT.ZDT1;
import jmetal.problems.ZDT.ZDT2;
import jmetal.problems.ZDT.ZDT3;
import jmetal.problems.ZDT.ZDT4;
import jmetal.problems.ZDT.ZDT6;
import jmetal.qualityIndicator.QualityIndicator;
import jsc.independentsamples.MannWhitneyTest;

public class ComparativeRuntimeEvaluator {

	private static List<Double> computeAverageConvergenceCurve(String ctc, Problem problem, String problemName,
			String metric) throws IOException {
		List<Double> result = new ArrayList<Double>();
		String fileName = ctc + problemName + "//" + metric + ".csv";
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		int testCount = 0;
		QualityIndicator indicator = new QualityIndicator(problem,
				"data\\input\\trueParetoFronts\\" + problemName + ".pareto");

		while ((line = br.readLine()) != null) {
			String[] lineTokens = line.split("\\,");
			if (lineTokens.length > result.size()) {
				int elmToAdd = lineTokens.length - result.size();
				for (int i = 0; i < elmToAdd; i++) {
					result.add(0.0);
				}
			}
			if (lineTokens.length > 10) {
				testCount++;
				int j = 0;
				for (int i = 0; i < lineTokens.length; i++) {
					double val = Double.parseDouble(lineTokens[i]);
					if (!Double.isNaN(val)) {
						result.set(j, result.get(j) + (val / indicator.getTrueParetoFrontHypervolume()));
						j++;
					}
				}
			}
		}
		br.close();

		for (int i = 0; i < result.size(); i++) {
			double value = (result.get(i) / testCount) * 100;
			if (value > 100.0) {
				value = 100.0;
			}
			result.set(i, value);
		}

		return result;
	}

	private static Map<String, List<Double>> computeAverageRankPerformanceBasic(List<String> classesToCompare,
			Map<String, List<Double>> averageHvValues, int rankGranularity) {

		Map<String, List<Double>> result = new HashMap<String, List<Double>>();

		int minCount = 10000000;
		for (String ctc : classesToCompare) {
			List<Double> values = averageHvValues.get(ctc);
			if (values.size() < minCount) {
				minCount = values.size();
			}
		}

		for (String ctc : classesToCompare) {
			result.put(ctc, new ArrayList<Double>());
		}

		int i = 0;
		while ((i - 1) < minCount) {
			// System.out.println(i - 1);

			Map<Double, String> sortingMap = new TreeMap<Double, String>();

			// System.out.println("--------------");
			// System.out.println("\n " + i + "\n");

			double epsilon = 0.000000001;
			int equalsFound = 0;
			for (String ctc : classesToCompare) {
				List<Double> values = averageHvValues.get(ctc);
				int j = i - 1;
				if (j < 0) {
					j = 0;
				}
				double value = values.get(j);
				if (value == 100.0) {
					equalsFound += 1;
					value -= equalsFound * epsilon;
				}
				if (value == 0.0) {
					equalsFound += 1;
					value += equalsFound * epsilon;
				}
				sortingMap.put(value, ctc);
			}

			int rank = classesToCompare.size();
			Map<String, Double> rawVals = new HashMap<String, Double>();
			for (Map.Entry<Double, String> entry : sortingMap.entrySet()) {
				double newValue = 1.0 * rank;

				if (entry.getKey() > 99.0) {
					newValue = 0.0;
				} else if (entry.getKey() < 1.0) {
					newValue = 1.0 * (classesToCompare.size() + 1);
				}

				rawVals.put(entry.getValue(), newValue);
				rank--;
			}
			// System.out.println("--------------");

			for (Map.Entry<String, Double> entry : rawVals.entrySet()) {
				List<Double> values = result.get(entry.getKey());
				values.add(entry.getValue());
			}

			i += rankGranularity;
		}

		return result;
	}

	private static Map<String, List<Double>> computeAverageRankPerformanceMannWhitneyU(List<String> classesToCompare,
			Map<String, List<Double>> averageHvValues, Map<String, List<List<Double>>> indHvValues,
			double levelOfSignificance, int rankGranularity) {

		Map<String, List<Double>> result = new HashMap<String, List<Double>>();

		int minCount = 10000000;
		for (String ctc : classesToCompare) {
			List<Double> values = averageHvValues.get(ctc);
			if (values.size() < minCount) {
				minCount = values.size();
			}
		}

		for (String ctc : classesToCompare) {
			result.put(ctc, new ArrayList<Double>());
		}

		int i = 0;
		while ((i - 1) < minCount) {
			// System.out.println(i - 1);

			Map<Double, String> sortingMap = new TreeMap<Double, String>();

			// System.out.println("--------------");
			// System.out.println("\n " + i + "\n");

			double epsilon = 0.000000001;
			int equalsFound = 0;
			int dataPointIndex = 0;
			for (String ctc : classesToCompare) {
				List<Double> values = averageHvValues.get(ctc);
				int j = i - 1;
				if (j < 0) {
					j = 0;
				}
				dataPointIndex = j;
				double value = values.get(j);
				if (value == 100.0) {
					equalsFound += 1;
					value -= equalsFound * epsilon;
				}
				if (value == 0.0) {
					equalsFound += 1;
					value += equalsFound * epsilon;
				}
				sortingMap.put(value, ctc);
			}

			// double lastHVVal = 0.0;
			int j = 0;
			int rank = classesToCompare.size();
			double[] lastHvIndValuesArray = new double[1];
			double[] newHvIndValuesArray = new double[1];
			Map<String, Double> rawVals = new HashMap<String, Double>();
			for (Map.Entry<Double, String> entry : sortingMap.entrySet()) {
				double newValue = 1.0 * rank;
				if (j == 0) {
					// lastHVVal = entry.getKey();
					lastHvIndValuesArray = extractHvIndRunValues(indHvValues, entry.getValue(), dataPointIndex);
				} else {
					newHvIndValuesArray = extractHvIndRunValues(indHvValues, entry.getValue(), dataPointIndex);
					if (!differenceIsStatisticallySignificant(newHvIndValuesArray, lastHvIndValuesArray,
							levelOfSignificance)) {
						newValue = 1.0 * rank;
					} else {
						rank--;
						newValue = 1.0 * rank;
						// lastHVVal = entry.getKey();
						lastHvIndValuesArray = newHvIndValuesArray;
					}
				}

				double avg = 0.0;
				newHvIndValuesArray = extractHvIndRunValues(indHvValues, entry.getValue(), dataPointIndex);
				for (double d : newHvIndValuesArray)
					avg += d;
				avg /= newHvIndValuesArray.length;
				// System.out.println(avg);
				if (avg > 99.0) {
					newValue = 0.0;
				}
				if (avg < 1.0) {
					newValue = 1.0 * (classesToCompare.size() + 1);
				}

				// System.out.println(entry.getValue() + " " + newValue);
				rawVals.put(entry.getValue(), newValue);
				j++;

				System.out.println("FINISHED COMPUTING DATAPOINT " + dataPointIndex + " FOR: " + entry.getValue());
			}

			for (Map.Entry<String, Double> entry : rawVals.entrySet()) {
				List<Double> values = result.get(entry.getKey());
				values.add(entry.getValue());
			}

			i += rankGranularity;
		}

		return result;
	}

	// private static Map<String, List<Double>>
	// computeAverageRankPerformanceOptimistic(List<String> classesToCompare,
	// Map<String, List<Double>> averageHvValues, int rankGranularity, double
	// relevanceFactor) {
	//
	// Map<String, List<Double>> result = new HashMap<String, List<Double>>();
	//
	// int minCount = 10000000;
	// for (String ctc : classesToCompare) {
	// List<Double> values = averageHvValues.get(ctc);
	// if (values.size() < minCount) {
	// minCount = values.size();
	// }
	// }
	//
	// for (String ctc : classesToCompare) {
	// result.put(ctc, new ArrayList<Double>());
	// }
	//
	// int i = 1;
	// while (i <= minCount) {
	// Map<Double, String> sortingMap = new TreeMap<Double, String>();
	//
	// // System.out.println("--------------");
	// // System.out.println("\n " + i + "\n");
	//
	// double epsilon = 0.000000001;
	// int equalsFound = 0;
	// for (String ctc : classesToCompare) {
	// List<Double> values = averageHvValues.get(ctc);
	// double value = values.get(i - 1);
	// if (value == 100.0) {
	// equalsFound += 1;
	// value -= equalsFound * epsilon;
	// }
	// if (value == 0.0) {
	// equalsFound += 1;
	// value += equalsFound * epsilon;
	// }
	// sortingMap.put(value, ctc);
	// }
	//
	// double lastHVVal = 0.0;
	// int j = 0;
	// int rank = classesToCompare.size();
	// Map<String, Double> rawVals = new HashMap<String, Double>();
	// for (Map.Entry<Double, String> entry : sortingMap.entrySet()) {
	// // System.out.println(entry.getKey() + " " + entry.getValue());
	// // List<Double> values = result.get(entry.getValue());
	// double newValue = 1.0 * rank;
	//
	// if (entry.getKey() > 99.0) {
	// newValue = 0.0;
	// } else if (entry.getKey() < 1.0) {
	// newValue = 1.0 * (classesToCompare.size() + 1);
	// } else if (j != 0) {
	// if ((entry.getKey() - relevanceFactor) < lastHVVal) {
	// newValue = 1.0 * rank;
	// } else {
	// rank--;
	// newValue = 1.0 * rank;
	// lastHVVal = entry.getKey();
	// }
	// }
	//
	// // values.add(newValue);
	// if (j == 0) {
	// lastHVVal = entry.getKey();
	// }
	// rawVals.put(entry.getValue(), newValue);
	// j++;
	// }
	// // System.out.println("--------------");
	//
	// double minRank = classesToCompare.size();
	// for (Map.Entry<String, Double> entry : rawVals.entrySet()) {
	// if ((entry.getValue() != 0.0) && (entry.getValue() < minRank)) {
	// minRank = entry.getValue();
	// }
	// }
	// for (Map.Entry<String, Double> entry : rawVals.entrySet()) {
	// List<Double> values = result.get(entry.getKey());
	// if ((entry.getValue() != 0.0) && (entry.getValue() !=
	// (classesToCompare.size() + 1))) {
	// double newRank = entry.getValue() - minRank + 1.0;
	// values.add(newRank);
	// } else {
	// values.add(entry.getValue());
	// }
	// }
	//
	// i += rankGranularity;
	// }
	//
	// // for (String ctc : classesToCompare) {
	// // List<Double> values = result.get(ctc);
	// // System.out.println("---------------");
	// // System.out.println(ctc);
	// // String s = "";
	// // for (Double val : values) {
	// // s += val + ", ";
	// // }
	// // System.out.println(s);
	// // System.out.println("---------------");
	// // }
	//
	// return result;
	// }

	private static Map<String, List<Double>> computeAverageRankPerformancePessimistic(List<String> classesToCompare,
			Map<String, List<Double>> averageHvValues, int rankGranularity, double relevanceFactor, double minVal,
			double maxVal) {

		Map<String, List<Double>> result = new HashMap<String, List<Double>>();

		int minCount = 10000000;
		for (String ctc : classesToCompare) {
			List<Double> values = averageHvValues.get(ctc);
			if (values.size() < minCount) {
				minCount = values.size();
			}
		}

		for (String ctc : classesToCompare) {
			result.put(ctc, new ArrayList<Double>());
		}

		int i = 0;
		while ((i - 1) < minCount) {
			Map<Double, String> sortingMap = new TreeMap<Double, String>();

			// System.out.println("--------------");
			// System.out.println("\n " + i + "\n");

			double epsilon = 0.000000001;
			int equalsFound = 0;
			for (String ctc : classesToCompare) {
				List<Double> values = averageHvValues.get(ctc);
				int j = i - 1;
				if (j < 0) {
					j = 0;
				}
				double value = values.get(j);
				if (value == 100.0) {
					equalsFound += 1;
					value -= equalsFound * epsilon;
				}
				if (value == 0.0) {
					equalsFound += 1;
					value += equalsFound * epsilon;
				}
				sortingMap.put(value, ctc);
			}

			double lastHVVal = 0.0;
			int j = 0;
			int rank = classesToCompare.size();
			Map<String, Double> rawVals = new HashMap<String, Double>();
			for (Map.Entry<Double, String> entry : sortingMap.entrySet()) {
				double newValue = 1.0 * rank;

				if (entry.getKey() > maxVal) {
					newValue = 0.0;
				} else if (entry.getKey() < minVal) {
					newValue = 1.0 * (classesToCompare.size() + 1);
				} else if (j != 0) {
					if ((entry.getKey() - relevanceFactor) < lastHVVal) {
						newValue = 1.0 * rank;
					} else {
						rank--;
						newValue = 1.0 * rank;
						lastHVVal = entry.getKey();
					}
				}
				if (j == 0) {
					lastHVVal = entry.getKey();
				}
				rawVals.put(entry.getValue(), newValue);
				j++;
			}
			// System.out.println("--------------");

			for (Map.Entry<String, Double> entry : rawVals.entrySet()) {
				List<Double> values = result.get(entry.getKey());
				values.add(entry.getValue());
			}

			i += rankGranularity;
		}

		return result;
	}

	// private static List<Double> computeIndividualThresholdPerformance(String
	// ctc, Problem problem, String problemName,
	// String metric, Double metricThreshold) throws IOException {
	//
	// List<Double> result = new ArrayList<Double>();
	// String fileName = ctc + problemName + "//" + metric + ".csv";
	// BufferedReader br = new BufferedReader(new FileReader(fileName));
	// String line;
	// QualityIndicator indicator = new QualityIndicator(problem,
	// "data\\input\\trueParetoFronts\\" + problemName + ".pareto");
	//
	// while ((line = br.readLine()) != null) {
	// String[] lineTokens = line.split("\\,");
	// if (lineTokens.length > 10) {
	// int j = 0;
	// for (int i = 0; i < lineTokens.length; i++) {
	// double val = Double.parseDouble(lineTokens[i]);
	// if (!Double.isNaN(val)) {
	// val = val / indicator.getTrueParetoFrontHypervolume();
	// if (val > metricThreshold) {
	// result.add(j * 100.0);
	// break;
	// }
	//
	// j++;
	// }
	// }
	// }
	// }
	// br.close();
	//
	// return result;
	//
	// }

	private static boolean differenceIsStatisticallySignificant(double[] newHvIndValuesArray,
			double[] lastHvIndValuesArray, double levelOfSignificance) {

		MannWhitneyTest uTest = new MannWhitneyTest(newHvIndValuesArray, lastHvIndValuesArray);
		// double probability = Math.abs(uTest.exactSP());

		double probability = Math.abs(uTest.getSP());

		// System.out.println(probability + " " + uTest.getSP() + " "
		// + newHvIndValuesArray.length + " "
		// + lastHvIndValuesArray.length);
		//
		// String s = " |";
		// for (int i = 0; i < newHvIndValuesArray.length; i++) {
		// s += newHvIndValuesArray[i] + "|, |";
		// }
		// String t = " |";
		// for (int i = 0; i < lastHvIndValuesArray.length; i++) {
		// t += lastHvIndValuesArray[i] + "|, |";
		// }
		// System.out.println(s);
		// System.out.println(t);

		if (probability < levelOfSignificance) {
			return true;
		}
		return false;
	}

	private static double[] extractHvIndRunValues(Map<String, List<List<Double>>> indHvValues, String lastCtc,
			int dataPointIndex) {
		double[] result;
		List<List<Double>> individualRunRecords = indHvValues.get(lastCtc);
		result = new double[individualRunRecords.size()];
		// System.out.println("-----");
		// System.out.println(lastCtc + ":" + dataPointIndex);

		for (int i = 0; i < result.length; i++) {
			result[i] = individualRunRecords.get(i).get(dataPointIndex);
			// System.out.println(result[i]);
		}
		// System.out.println("-----");
		return result;
	}

	private static List<List<Double>> extractIndividualConvergenceValues(String ctc, Problem problem,
			String problemName, String metric) throws IOException {
		List<List<Double>> result = new ArrayList<List<Double>>();
		int dataPointsCount = 0;
		String fileName = ctc + problemName + "//" + metric + ".csv";
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		QualityIndicator indicator = new QualityIndicator(problem,
				"data\\input\\trueParetoFronts\\" + problemName + ".pareto");

		while ((line = br.readLine()) != null) {
			String[] lineTokens = line.split("\\,");
			if (lineTokens.length > 10) {
				List<Double> currentRun = new ArrayList<Double>();

				if (lineTokens.length > dataPointsCount) {
					int elmToAdd = lineTokens.length - dataPointsCount;
					for (int i = 0; i < elmToAdd; i++) {
						currentRun.add(0.0);
					}
				}

				int j = 0;
				for (int i = 0; i < lineTokens.length; i++) {
					double val = Double.parseDouble(lineTokens[i]);
					if (!Double.isNaN(val)) {
						double value = (val / indicator.getTrueParetoFrontHypervolume()) * 100;
						if (value > 100.0) {
							value = 100.0;
						}
						currentRun.set(j, value);
						j++;
					}
				}

				result.add(currentRun);
			}
		}
		br.close();

		return result;
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		/** Main initializations */
		List<String> rankingTypeToConstruct = new ArrayList<String>();
		List<String> classesToCompare = new ArrayList<String>();
		List<Problem> problemsToUseInComparison = new ArrayList<Problem>();

		// TODO adjust the variables "nameOfAnalysis", "rankingTypeToConstruct",
		// "classesToCompare" and "problemsToUseInComparison" as necessary

		/**
		 * Generic description of the algorithms that are compared: e.g.,
		 * "allAlgortihms" = comparison between all runtime results available,
		 * "DECMOFamily" = comparison between the three DECMO variants and
		 * "DECMO2vsNSGA2" = comparison between the DECMO2 and NSGA2 solvers
		 */
		// String nameOfAnalysis = "allAlgorithms_SmallPopSize";
		String nameOfAnalysis = "allAlgorithms";

		/**
		 * Type of rankings to use when building the Hypervolume-Ranked
		 * Performance Curves (HRPCs)
		 */
		rankingTypeToConstruct.add("1_basicRanking");
		rankingTypeToConstruct.add("2_pessimisticRanking_1Percent");
		rankingTypeToConstruct.add("3_pessimisticRanking_5Percent");
		rankingTypeToConstruct.add("4_pessimisticRanking_10Percent");
		rankingTypeToConstruct.add("5_statisticalRanking");

		/**
		 * Result paths to include in comparison, i.e., algorithms to include in
		 * the comparison
		 */

		// classesToCompare.add("data//output//runtimePerformance//DECMO//SolutionSetSize50//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO//SolutionSetSize100//");
		classesToCompare.add("data//output//runtimePerformance//DECMO//SolutionSetSize200//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO//SolutionSetSize300//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO//SolutionSetSize400//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO//SolutionSetSize500//");

		// classesToCompare.add("data//output//runtimePerformance//DECMO2//SolutionSetSize50//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO2//SolutionSetSize100//");
		classesToCompare.add("data//output//runtimePerformance//DECMO2//SolutionSetSize200//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO2//SolutionSetSize300//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO2//SolutionSetSize400//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO2//SolutionSetSize500//");

		// classesToCompare.add("data//output//runtimePerformance//DECMO2++//SolutionSetSize50//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO2++//SolutionSetSize100//");
		classesToCompare.add("data//output//runtimePerformance//DECMO2++//SolutionSetSize200//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO2++//SolutionSetSize300//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO2++//SolutionSetSize400//");
		// classesToCompare.add("data//output//runtimePerformance//DECMO2++//SolutionSetSize500//");

		// classesToCompare.add("data//output//runtimePerformance//SPEA2//SolutionSetSize50//");
		// classesToCompare.add("data//output//runtimePerformance//SPEA2//SolutionSetSize100//");
		classesToCompare.add("data//output//runtimePerformance//SPEA2//SolutionSetSize200//");
		// classesToCompare.add("data//output//runtimePerformance//SPEA2//SolutionSetSize300//");
		// classesToCompare.add("data//output//runtimePerformance//SPEA2//SolutionSetSize400//");
		// classesToCompare.add("data//output//runtimePerformance//SPEA2//SolutionSetSize500//");

		// classesToCompare.add("data//output//runtimePerformance//GDE3//SolutionSetSize50//");
		// classesToCompare.add("data//output//runtimePerformance//GDE3//SolutionSetSize100//");
		classesToCompare.add("data//output//runtimePerformance//GDE3//SolutionSetSize200//");
		// classesToCompare.add("data//output//runtimePerformance//GDE3//SolutionSetSize300//");
		// classesToCompare.add("data//output//runtimePerformance//GDE3//SolutionSetSize400//");
		// classesToCompare.add("data//output//runtimePerformance//GDE3//SolutionSetSize500//");

		// classesToCompare.add("data//output//runtimePerformance//NSGA2//SolutionSetSize50//");
		// classesToCompare.add("data//output//runtimePerformance//NSGA2//SolutionSetSize100//");
		classesToCompare.add("data//output//runtimePerformance//NSGA2//SolutionSetSize200//");
		// classesToCompare.add("data//output//runtimePerformance//NSGA2//SolutionSetSize300//");
		// classesToCompare.add("data//output//runtimePerformance//NSGA2//SolutionSetSize400//");
		// classesToCompare.add("data//output//runtimePerformance//NSGA2//SolutionSetSize500//");

		classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSizeLitRecom//");
		// classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSize50//");
		// classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSize100//");
		// classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSize200//");
		// classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSize300//");
		// classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSize400//");
		// classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSize500//");
		// classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSize595//");
		// classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSize700//");
		// classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSize800//");
		// classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSize900//");
		// classesToCompare.add("data//output//runtimePerformance//MOEAD_DRA//SolutionSetSize1000//");

		/** Benchmark problems to include in the comparison */
		problemsToUseInComparison.add(new DTLZ1("Real")); // 1
		problemsToUseInComparison.add(new DTLZ2("Real")); // 2
		problemsToUseInComparison.add(new DTLZ3("Real")); // 3
		problemsToUseInComparison.add(new DTLZ4("Real")); // 4
		problemsToUseInComparison.add(new DTLZ5("Real")); // 5
		problemsToUseInComparison.add(new DTLZ6("Real")); // 6
		problemsToUseInComparison.add(new DTLZ7("Real")); // 7
		problemsToUseInComparison.add(new Kursawe("Real", 10)); // 8
		problemsToUseInComparison.add(new LZ09_F1("Real")); // 9
		problemsToUseInComparison.add(new LZ09_F2("Real")); // 10
		problemsToUseInComparison.add(new LZ09_F3("Real")); // 11
		problemsToUseInComparison.add(new LZ09_F4("Real")); // 12
		problemsToUseInComparison.add(new LZ09_F5("Real")); // 13
		problemsToUseInComparison.add(new LZ09_F6("Real")); // 14
		problemsToUseInComparison.add(new LZ09_F7("Real")); // 15
		problemsToUseInComparison.add(new LZ09_F8("Real")); // 16
		problemsToUseInComparison.add(new LZ09_F9("Real")); // 17
		problemsToUseInComparison.add(new WFG1("Real")); // 18
		problemsToUseInComparison.add(new WFG2("Real")); // 19
		problemsToUseInComparison.add(new WFG3("Real")); // 20
		problemsToUseInComparison.add(new WFG4("Real")); // 21
		problemsToUseInComparison.add(new WFG5("Real")); // 22
		problemsToUseInComparison.add(new WFG6("Real")); // 23
		problemsToUseInComparison.add(new WFG7("Real")); // 24
		problemsToUseInComparison.add(new WFG8("Real")); // 25
		problemsToUseInComparison.add(new WFG9("Real")); // 26
		problemsToUseInComparison.add(new ZDT1("Real", 30)); // 27
		problemsToUseInComparison.add(new ZDT2("Real", 30)); // 28
		problemsToUseInComparison.add(new ZDT3("Real", 10)); // 29
		problemsToUseInComparison.add(new ZDT4("Real", 10)); // 30
		problemsToUseInComparison.add(new ZDT6("Real", 10)); // 31

		for (String rankingType : rankingTypeToConstruct) {

			Map<String, Map<String, List<Double>>> performanceRankMatrices = new HashMap<String, Map<String, List<Double>>>();
			Map<String, List<Double>> averageHvConvergenceTrends = new HashMap<String, List<Double>>();

			/** Output path for comparative evaluation result files */
			String outputPath = "data//output//runtimePerformance//comparativePerformance//" + nameOfAnalysis + "//";
			// Double metricThreshold = 0.85;
			String metric = "HV";
			/**
			 * After how many consecutive runtime data points (i.e.,
			 * generations) should be rankings be performed
			 */
			int rankGranularity = 10;
			String problemName = "Problem";

			for (String ctc : classesToCompare) {
				performanceRankMatrices.put(ctc, new TreeMap<String, List<Double>>());
				// averageHvConvergenceTrends.put(ctc, new ArrayList<Double>());
			}

			int problemNo = 0;
			for (Problem problem : problemsToUseInComparison) {

				problemNo++;

				if (problem instanceof DTLZ1) {
					problemName = "DTLZ1_7";
				}
				if (problem instanceof DTLZ2) {
					problemName = "DTLZ2_12";
				}
				if (problem instanceof DTLZ3) {
					problemName = "DTLZ3_12";
				}
				if (problem instanceof DTLZ4) {
					problemName = "DTLZ4_12";
				}
				if (problem instanceof DTLZ5) {
					problemName = "DTLZ5_12";
				}
				if (problem instanceof DTLZ6) {
					problemName = "DTLZ6_12";
				}
				if (problem instanceof DTLZ7) {
					problemName = "DTLZ7_22";
				}
				if (problem instanceof ZDT1) {
					problemName = "ZDT1_30";
				}
				if (problem instanceof ZDT2) {
					problemName = "ZDT2_30";
				}
				if (problem instanceof ZDT3) {
					problemName = "ZDT3_10";
				}
				if (problem instanceof ZDT4) {
					problemName = "ZDT4_10";
				}
				if (problem instanceof ZDT6) {
					problemName = "ZDT6_10";
				}
				if (problem instanceof WFG1) {
					problemName = "WFG1_6";
				}
				if (problem instanceof WFG2) {
					problemName = "WFG2_6";
				}
				if (problem instanceof WFG3) {
					problemName = "WFG3_6";
				}
				if (problem instanceof WFG4) {
					problemName = "WFG4_6";
				}
				if (problem instanceof WFG5) {
					problemName = "WFG5_6";
				}
				if (problem instanceof WFG6) {
					problemName = "WFG6_6";
				}
				if (problem instanceof WFG7) {
					problemName = "WFG7_6";
				}
				if (problem instanceof WFG8) {
					problemName = "WFG8_6";
				}
				if (problem instanceof WFG9) {
					problemName = "WFG9_6";
				}
				if (problem instanceof LZ09_F1) {
					problemName = "LZ09_F1_30";
				}
				if (problem instanceof LZ09_F2) {
					problemName = "LZ09_F2_30";
				}
				if (problem instanceof LZ09_F3) {
					problemName = "LZ09_F3_30";
				}
				if (problem instanceof LZ09_F4) {
					problemName = "LZ09_F4_30";
				}
				if (problem instanceof LZ09_F5) {
					problemName = "LZ09_F5_30";
				}
				if (problem instanceof LZ09_F6) {
					problemName = "LZ09_F6_30";
				}
				if (problem instanceof LZ09_F7) {
					problemName = "LZ09_F7_10";
				}
				if (problem instanceof LZ09_F8) {
					problemName = "LZ09_F8_10";
				}
				if (problem instanceof LZ09_F9) {
					problemName = "LZ09_F9_30";
				}
				if (problem instanceof Kursawe) {
					problemName = "KSW_10";
				}

				Map<String, List<Double>> averageHvValues = new HashMap<String, List<Double>>();
				Map<String, List<Double>> averageRankPerformance = new HashMap<String, List<Double>>();
				Map<String, List<List<Double>>> indHvValues = new HashMap<String, List<List<Double>>>();

				// Map<String, List<Double>> indHvPerformances = new
				// HashMap<String,
				// List<Double>>();

				for (String ctc : classesToCompare) {
					List<Double> averageHvConvergenceValues = computeAverageConvergenceCurve(ctc, problem, problemName,
							metric);
					List<List<Double>> individualHvConvergnceValues = extractIndividualConvergenceValues(ctc, problem,
							problemName, metric);
					// List<Double> individualThresholdPerformance =
					// computeIndividualThresholdPerformance(ctc, problem,
					// problemName, metric, metricThreshold);

					averageHvValues.put(ctc, averageHvConvergenceValues);
					indHvValues.put(ctc, individualHvConvergnceValues);
					// indHvPerformances.put(ctc,
					// individualThresholdPerformance);
				}

				System.out.println("FOR " + problemName + ":");

				// perform the selected type of ranking comparison
				if (rankingType.equals("1_basicRanking")) {
					averageRankPerformance = computeAverageRankPerformanceBasic(classesToCompare, averageHvValues,
							rankGranularity);
				} else if (rankingType.equals("5_statisticalRanking")) {
					averageRankPerformance = computeAverageRankPerformanceMannWhitneyU(classesToCompare,
							averageHvValues, indHvValues, 0.025, rankGranularity);
				} else if (rankingType.equals("2_pessimisticRanking_1Percent")) {
					averageRankPerformance = computeAverageRankPerformancePessimistic(classesToCompare, averageHvValues,
							rankGranularity, 1, 1.0, 99.0);
				} else if (rankingType.equals("3_pessimisticRanking_5Percent")) {
					averageRankPerformance = computeAverageRankPerformancePessimistic(classesToCompare, averageHvValues,
							rankGranularity, 5, 1.0, 99.0);
				} else if (rankingType.equals("4_pessimisticRanking_10Percent")) {
					averageRankPerformance = computeAverageRankPerformancePessimistic(classesToCompare, averageHvValues,
							rankGranularity, 10, 1.0, 99.0);
				}

				for (Map.Entry<String, List<Double>> entry : averageRankPerformance.entrySet()) {
					String ctc = entry.getKey();
					List<Double> values = entry.getValue();
					Map<String, List<Double>> prmEntry = performanceRankMatrices.get(ctc);
					prmEntry.put(problemName, values);
				}
				computeOverallConvergenceTrends(classesToCompare, averageHvValues, averageHvConvergenceTrends,
						problemNo);

				outputMetricResults(classesToCompare, averageHvValues, outputPath + "//2_AverageRuntimePerformance//",
						problemName, metric);
				// outputStatDetails(classesToCompare, indHvPerformances,
				// resultPath, problemName, metric);

				System.out.println("Computation finished! - " + problemName);
			}

			outputGlobalResults(classesToCompare, performanceRankMatrices,
					outputPath + "//1_RankedPerformanceCurves//" + rankingType + "//", metric);
			outputConvergenceTrendsResults(classesToCompare, outputPath + "//2_AverageRuntimePerformance//",
					averageHvConvergenceTrends, metric);

			System.out.println("Computations finished for ranking type: " + rankingType);
		}
		System.out.println("All computations finished!");
	}

	private static void outputConvergenceTrendsResults(List<String> classesToCompare, String resultPath,
			Map<String, List<Double>> averageHvConvergenceTrends, String metric) throws IOException {

		String fileName = resultPath + "AcrossAllProblems//Global" + metric + ".csv";
		File f = new File(fileName);
		File dir = new File(f.getParent());
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Could not create directory path: " + f.getParent());
		}
		if (!f.exists()) {
			f.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false));

		int minCount = 10000000;
		for (String ctc : classesToCompare) {
			List<Double> values = averageHvConvergenceTrends.get(ctc);
			if (values.size() < minCount) {
				minCount = values.size();
			}
		}

		String header = "Comparison stage no.";
		for (String ctc : classesToCompare) {
			header += "," + ctc;
		}
		bw.write(header + "\n");

		// bw.write("0,0.0,0.0,0.0,\n");
		for (int i = 0; i < minCount; i++) {
			String s = "" + i;
			for (String ctc : classesToCompare) {
				List<Double> values = averageHvConvergenceTrends.get(ctc);
				if (values.size() > i) {
					double val = values.get(i);
					s += "," + val;
				} else {
					s += ",-1.0";
				}
			}
			bw.write(s + "\n");
		}

		bw.close();
	}

	private static void computeOverallConvergenceTrends(List<String> classesToCompare,
			Map<String, List<Double>> averageHvProblemValues, Map<String, List<Double>> averageHvConvergenceTrends,
			int problemNo) {

		if (problemNo == 1) { // averageHvConvergenceTrends is empty
			for (String ctc : classesToCompare) {
				List<Double> problemValues = averageHvProblemValues.get(ctc);
				averageHvConvergenceTrends.put(ctc, problemValues);
			}
		} else {
			for (String ctc : classesToCompare) {
				List<Double> problemValues = averageHvProblemValues.get(ctc);
				List<Double> trendValues = averageHvConvergenceTrends.get(ctc);
				for (int i = 0; i < trendValues.size(); i++) {
					trendValues.set(i, (trendValues.get(i) * (problemNo - 1) + problemValues.get(i)) / problemNo);
				}
			}
		}
	}

	private static void outputGlobalResults(List<String> classesToCompare,
			Map<String, Map<String, List<Double>>> performanceRankMatrices, String outputPath, String metric)
					throws IOException {

		String fileName = outputPath + metric + "_RankMatrices.csv";
		File f = new File(fileName);
		File dir = new File(f.getParent());
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Could not create directory path: " + f.getParent());
		}
		if (!f.exists()) {
			f.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false));

		Map<String, List<Double>> rhCurves = new TreeMap<String, List<Double>>();

		for (String ctc : classesToCompare) {
			bw.write("Results for: " + ctc + "\n");

			String rhCurveName = ctc + "-RHC";
			List<Double> rhCurveValues = new ArrayList<Double>();
			boolean initialized = false;

			double oAvg = 0.0;
			double lAvg = 0.0;
			for (Map.Entry<String, List<Double>> entry : performanceRankMatrices.get(ctc).entrySet()) {
				String s = entry.getKey() + ",";
				double avg = 0;
				double lval = 0;
				int rhCurveIndex = 0;
				if (!initialized) {
					for (int i = 0; i < entry.getValue().size(); i++) {
						rhCurveValues.add(0.0);
					}
					initialized = true;
				}
				for (Double val : entry.getValue()) {
					s += val + ",";
					avg += val;
					lval = val;
					rhCurveValues.set(rhCurveIndex, rhCurveValues.get(rhCurveIndex) + val);
					rhCurveIndex++;
				}
				avg /= entry.getValue().size();
				oAvg += avg;
				lAvg += lval;
				s += String.format("%.4f", avg);
				bw.write(s + "\n");
			}
			oAvg /= performanceRankMatrices.get(ctc).entrySet().size();
			lAvg /= performanceRankMatrices.get(ctc).entrySet().size();
			bw.write("Overall rank: " + String.format("%.4f", oAvg) + "\n");
			bw.write("Last gen. rank: " + String.format("%.4f", lAvg) + "\n");
			bw.write("\n");

			for (int i = 0; i < rhCurveValues.size(); i++) {
				rhCurveValues.set(i, rhCurveValues.get(i) / performanceRankMatrices.get(ctc).entrySet().size());
			}
			rhCurves.put(rhCurveName, rhCurveValues);
		}

		bw.write("\n");
		bw.write("\n");
		for (Map.Entry<String, List<Double>> entry : rhCurves.entrySet()) {
			String s = entry.getKey();
			for (Double value : entry.getValue()) {
				s += ", " + value;
			}
			bw.write(s + "\n");
		}

		bw.close();
	}

	private static void outputMetricResults(List<String> classesToCompare, Map<String, List<Double>> averageHvValues,
			String outputPath, String problemName, String metric) throws IOException {

		String fileName = outputPath + problemName + "//Problem" + metric + ".csv";
		File f = new File(fileName);
		File dir = new File(f.getParent());
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Could not create directory path: " + f.getParent());
		}
		if (!f.exists()) {
			f.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false));

		// for (String ctc : classesToCompare) {
		// List<Double> values = averageHvValues.get(ctc);
		// String s = "0.0";
		// for (Double val : values) {
		// s += "," + val;
		// }
		// bw.write(s + "\n");
		// }

		int minCount = 10000000;
		for (String ctc : classesToCompare) {
			List<Double> values = averageHvValues.get(ctc);
			if (values.size() < minCount) {
				minCount = values.size();
			}
		}

		String header = "Comparison stage no.";
		for (String ctc : classesToCompare) {
			header += "," + ctc;
		}
		bw.write(header + "\n");

		// bw.write("0,0.0,0.0,0.0,\n");
		for (int i = 0; i < minCount; i++) {
			String s = "" + i;
			for (String ctc : classesToCompare) {
				List<Double> values = averageHvValues.get(ctc);
				if (values.size() > i) {
					double val = values.get(i);
					s += "," + val;
				} else {
					s += ",-1.0";
				}
			}
			bw.write(s + "\n");
		}

		bw.close();
	}

	// private static void outputStatDetails(List<String> classesToCompare,
	// Map<String, List<Double>> indHvPerformances,
	// String resultPath, String problemName, String metric) throws IOException
	// {
	// String fileName = resultPath + problemName + "//" + metric +
	// "_Stats.csv";
	// File f = new File(fileName);
	// File dir = new File(f.getParent());
	// if (!dir.exists() && !dir.mkdirs()) {
	// throw new IOException("Could not create directory path: " +
	// f.getParent());
	// }
	// if (!f.exists()) {
	// f.createNewFile();
	// }
	// BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false));
	//
	// int maxCount = 0;
	// for (String ctc : classesToCompare) {
	// List<Double> values = indHvPerformances.get(ctc);
	// if (values.size() > maxCount) {
	// maxCount = values.size();
	// }
	// }
	//
	// for (int i = 0; i < maxCount; i++) {
	// String s = "";
	// for (String ctc : classesToCompare) {
	// List<Double> values = indHvPerformances.get(ctc);
	// if (s.length() > 0) {
	// s += ",";
	// }
	// if (values.size() > i) {
	// double val = values.get(i);
	// s += val;
	// } else {
	// s += "-1.0";
	// }
	// }
	// bw.write(s + "\n");
	// }
	//
	// bw.close();
	// }
}
