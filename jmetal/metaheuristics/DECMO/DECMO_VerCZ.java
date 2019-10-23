/**
 * DECMO_VerCZ.java
 *
 * @author Ciprian Zavoianu
 * @version 1.0
 */
package jmetal.metaheuristics.DECMO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.operator.crossover.CrossoverFactory;
import jmetal.base.operator.mutation.MutationFactory;
import jmetal.base.operator.selection.SelectionFactory;
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
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Spea2Fitness;

/**
 * This class implements the GDE3 algorithm.
 */
public class DECMO_VerCZ extends Algorithm {

	/**
	 * stores the problem to solve
	 */
	private final Problem problem_;

	private static String PROBLEM_NAME = "Problem";

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public DECMO_VerCZ(Problem problem) {
		this.problem_ = problem;
	} // GDE3

	/**
	 * Run the DECMO algorithm.
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated
	 *         solutions as a result of the algorithm execution
	 * @throws JMException
	 */
	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int populationSize;
		int maxIterations;
		int evaluations;
		int iterations;

		/** Initializations */

		Random rndGen = new Random(1234567L);

		SolutionSet pool1;
		SolutionSet pool2;
		SolutionSet offspringPop1;
		SolutionSet offspringPop2;
		SolutionSet union;

		int reportInterval;
		int currentGen = 0;

		Distance distance;
		Comparator dominance;

		// Read the DECMO parameters
		populationSize = ((Integer) this.getInputParameter("individualPopulationSize")).intValue();
		maxIterations = ((Integer) this.getInputParameter("maxIterations")).intValue();
		reportInterval = ((Integer) getInputParameter("reportInterval")).intValue();

		/** Fixed fitness sharing interval */
		int mixInterval = populationSize / 10;

		/**
		 * Fixed parameterizations for the genetic operators that are to be
		 * applied when evolving the two subpopulations
		 */
		Operator selectionOperator1 = SelectionFactory.getSelectionOperator("BinaryTournament");
		Operator selectionOperator2 = SelectionFactory.getSelectionOperator("DifferentialEvolutionSelection");
		Operator crossoverOperator1 = CrossoverFactory.getCrossoverOperator("SBXCrossover");
		crossoverOperator1.setParameter("probability", 1.0);
		crossoverOperator1.setParameter("distributionIndex", 20.0);
		Operator mutationOperator1 = MutationFactory.getMutationOperator("PolynomialMutation");
		mutationOperator1.setParameter("probability", 1.0 / problem_.getNumberOfVariables());
		mutationOperator1.setParameter("distributionIndex", 20.0);
		Operator crossoverOperator2 = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover");
		crossoverOperator2.setParameter("CR", 0.2);
		crossoverOperator2.setParameter("F", 0.5);

		distance = new Distance();
		dominance = new jmetal.base.operator.comparator.DominanceComparator();

		// Array for storing runtime Pareto Front quality
		List<Double> generationalHV = new ArrayList<Double>();

		Solution parent1[] = new Solution[2];
		Solution parent2[];
		Solution parent3[];

		if (problem_ instanceof DTLZ1) {
			PROBLEM_NAME = "DTLZ1_7";
		}
		if (problem_ instanceof DTLZ2) {
			PROBLEM_NAME = "DTLZ2_12";
		}
		if (problem_ instanceof DTLZ3) {
			PROBLEM_NAME = "DTLZ3_12";
		}
		if (problem_ instanceof DTLZ4) {
			PROBLEM_NAME = "DTLZ4_12";
		}
		if (problem_ instanceof DTLZ5) {
			PROBLEM_NAME = "DTLZ5_12";
		}
		if (problem_ instanceof DTLZ6) {
			PROBLEM_NAME = "DTLZ6_12";
		}
		if (problem_ instanceof DTLZ7) {
			PROBLEM_NAME = "DTLZ7_22";
		}
		if (problem_ instanceof ZDT6) {
			PROBLEM_NAME = "ZDT6_10";
		}
		if (problem_ instanceof ZDT4) {
			PROBLEM_NAME = "ZDT4_10";
		}
		if (problem_ instanceof ZDT3) {
			PROBLEM_NAME = "ZDT3_10";
		}
		if (problem_ instanceof ZDT2) {
			PROBLEM_NAME = "ZDT2_30";
		}
		if (problem_ instanceof ZDT1) {
			PROBLEM_NAME = "ZDT1_30";
		}
		if (problem_ instanceof WFG1) {
			PROBLEM_NAME = "WFG1_6";
		}
		if (problem_ instanceof WFG2) {
			PROBLEM_NAME = "WFG2_6";
		}
		if (problem_ instanceof WFG3) {
			PROBLEM_NAME = "WFG3_6";
		}
		if (problem_ instanceof WFG4) {
			PROBLEM_NAME = "WFG4_6";
		}
		if (problem_ instanceof WFG5) {
			PROBLEM_NAME = "WFG5_6";
		}
		if (problem_ instanceof WFG6) {
			PROBLEM_NAME = "WFG6_6";
		}
		if (problem_ instanceof WFG7) {
			PROBLEM_NAME = "WFG7_6";
		}
		if (problem_ instanceof WFG8) {
			PROBLEM_NAME = "WFG8_6";
		}
		if (problem_ instanceof WFG9) {
			PROBLEM_NAME = "WFG9_6";
		}
		if (problem_ instanceof LZ09_F1) {
			PROBLEM_NAME = "LZ09_F1_30";
		}
		if (problem_ instanceof LZ09_F2) {
			PROBLEM_NAME = "LZ09_F2_30";
		}
		if (problem_ instanceof LZ09_F3) {
			PROBLEM_NAME = "LZ09_F3_30";
		}
		if (problem_ instanceof LZ09_F4) {
			PROBLEM_NAME = "LZ09_F4_30";
		}
		if (problem_ instanceof LZ09_F5) {
			PROBLEM_NAME = "LZ09_F5_30";
		}
		if (problem_ instanceof LZ09_F6) {
			PROBLEM_NAME = "LZ09_F6_30";
		}
		if (problem_ instanceof LZ09_F7) {
			PROBLEM_NAME = "LZ09_F7_10";
		}
		if (problem_ instanceof LZ09_F8) {
			PROBLEM_NAME = "LZ09_F8_10";
		}
		if (problem_ instanceof LZ09_F9) {
			PROBLEM_NAME = "LZ09_F9_30";
		}
		if (problem_ instanceof Kursawe) {
			PROBLEM_NAME = "KSW_10";
		}

		int pool1Size = populationSize;
		int pool2Size = populationSize;

		// Initialize the main subpopulation variables
		pool1 = new SolutionSet(pool1Size);
		pool2 = new SolutionSet(pool2Size);

		evaluations = 0;
		iterations = 0;

		// Create the initial subpopulation pools
		// pool1
		Solution newSolution;
		for (int i = 0; i < pool1Size; i++) {
			newSolution = new Solution(problem_);
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			evaluations++;
			pool1.add(newSolution);
		}
		// pool2
		for (int i = 0; i < pool2Size; i++) {
			newSolution = new Solution(problem_);
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			evaluations++;
			pool2.add(newSolution);
		}

		int mix = mixInterval;

		QualityIndicator indicator = new QualityIndicator(problem_,
				"data\\input\\trueParetoFronts\\" + PROBLEM_NAME + ".pareto");

		boolean initialPopulation = true;

		// The main evolutionary cycle
		while (iterations < maxIterations) {
			SolutionSet combi = new SolutionSet();
			if (!initialPopulation) {
				offspringPop1 = new SolutionSet(pool1Size * 3);
				offspringPop2 = new SolutionSet(pool2Size * 3);

				// evolve pool1
				for (int i = 0; i < pool1Size; i++) {
					parent1[0] = (Solution) selectionOperator1.execute(pool1);
					parent1[1] = (Solution) selectionOperator1.execute(pool1);

					Solution child1;
					child1 = ((Solution[]) crossoverOperator1.execute(parent1))[0];
					mutationOperator1.execute(child1);

					problem_.evaluate(child1);
					problem_.evaluateConstraints(child1);
					evaluations++;

					offspringPop1.add(child1);
				} // for pool1

				// evolve pool2
				for (int i = 0; i < pool2Size; i++) {
					parent2 = (Solution[]) selectionOperator2.execute(new Object[] { pool2, i });

					Solution child2;
					child2 = (Solution) crossoverOperator2.execute(new Object[] { pool2.get(i), parent2 });

					problem_.evaluate(child2);
					problem_.evaluateConstraints(child2);
					evaluations++;

					int result;
					result = dominance.compare(pool2.get(i), child2);
					if (result == -1) { // Solution i dominates child
						offspringPop2.add(pool2.get(i));
					} // if
					else if (result == 1) { // child dominates
						offspringPop2.add(child2);
					} // else if
					else { // the two solutions are non-dominated
						offspringPop2.add(child2);
						offspringPop2.add(pool2.get(i));
					} // else
				} // for pool2

				Solution ind1 = pool1.get(rndGen.nextInt(pool1Size));
				Solution ind2 = pool2.get(rndGen.nextInt(pool2Size));

				offspringPop1.add(ind2);
				offspringPop2.add(ind1);

				offspringPop1 = offspringPop1.union(pool1);
				Spea2Fitness spea1 = new Spea2Fitness(offspringPop1);
				spea1.fitnessAssign();
				pool1 = spea1.environmentalSelection(pool1Size);

				Spea2Fitness spea2 = new Spea2Fitness(offspringPop2);
				spea2.fitnessAssign();
				pool2 = spea2.environmentalSelection(pool2Size);

				mix--;
				if (mix == 0) {
					// Time to perform fitness sharing
					mix = mixInterval;
					combi = (combi.union(pool1)).union(pool2);
					System.out.println("Combi size: " + combi.size());
					Spea2Fitness spea5 = new Spea2Fitness(combi);
					spea5.fitnessAssign();
					combi = spea5.environmentalSelection(pool1Size / 10);
					pool1 = pool1.union(combi);
					pool2 = pool2.union(combi);

					System.out.println("Sizes: " + pool1.size() + " " + pool2.size());

					spea1 = new Spea2Fitness(pool1);
					spea1.fitnessAssign();
					pool1 = spea1.environmentalSelection(pool1Size);

					spea2 = new Spea2Fitness(pool2);
					spea2.fitnessAssign();
					pool2 = spea2.environmentalSelection(pool2Size);
				}
			} // if initialPopulation
			if (initialPopulation) {
				initialPopulation = false;
			}
			iterations++;

			double hVal1 = indicator.getHypervolume(pool1);
			double hVal2 = indicator.getHypervolume(pool2);
			System.out.println("Hypervolume: " + iterations + " - " + hVal1 + " - " + hVal2);

			int newGen = evaluations / reportInterval;
			if (newGen > currentGen) {
				combi = (combi.union(pool1)).union(pool2);
				Spea2Fitness spea5 = new Spea2Fitness(combi);
				spea5.fitnessAssign();
				combi = spea5.environmentalSelection(2 * populationSize);
				double hVal = indicator.getHypervolume(combi);
				for (int i = currentGen; i < newGen; i++) {
					generationalHV.add(hVal);
				}
				currentGen = newGen;
			}
		} // while (main evolutionary cycle)

		// write runtime generational HV to file
		String sGenHV = "";
		for (Double d : generationalHV) {
			sGenHV += d + ",";
		}

		try {
			File hvFile = new File("data\\output\\runtimePerformance\\DECMO\\SolutionSetSize" + 2 * populationSize
					+ "\\" + PROBLEM_NAME + "\\HV.csv");
			File dir = new File(hvFile.getParent());
			if (!dir.exists() && !dir.mkdirs()) {
				System.out.println("Could not create directory path: ");
			}
			if (!hvFile.exists()) {
				hvFile.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(hvFile, true));
			bw.write(sGenHV + "\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return the first non-dominated front
		/**
		 * Return the final combined non-dominated set of maximum size =
		 * (pool1Size + pool2Size)
		 */
		SolutionSet combiIni = new SolutionSet();
		combiIni = ((combiIni.union(pool1)).union(pool2));
		Spea2Fitness speaF = new Spea2Fitness(combiIni);
		speaF.fitnessAssign();
		combiIni = speaF.environmentalSelection(pool1Size + pool2Size);
		return combiIni;
	} // execute

	private void saveLastArchiveToFile(SolutionSet archive) throws IOException {
		File paretoFile = new File(
				"data\\input\\trueParetoFronts\\" + PROBLEM_NAME + "_" + problem_.getNumberOfVariables() + ".pareto");
		File dir = new File(paretoFile.getParent());
		if (!dir.exists() && !dir.mkdirs()) {
			System.out.println("Could not create directory path: ");
		}
		if (!paretoFile.exists()) {
			paretoFile.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(paretoFile, false));

		System.out.println(archive.size());
		for (int i = 0; i < archive.size(); i++) {
			Solution sol = archive.get(i);
			String s = "";
			for (int j = 0; j < problem_.getNumberOfObjectives(); j++) {
				s += sol.getObjective(j) + " ";
			}
			bw.write(s + "\n");
		}

		bw.close();
	}
} // DECMO_VerCZ
