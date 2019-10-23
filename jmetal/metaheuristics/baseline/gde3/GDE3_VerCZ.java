/**
a * GDE3.java
 * @author Antonio J. Nebro with modifications by Ciprian Zavoianu for runtime hypervolume tracking
 * @version 1.0  
 */
package jmetal.metaheuristics.baseline.gde3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
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
import jmetal.util.Ranking;

/**
 * This class implements the GDE3 algorithm.
 */
public class GDE3_VerCZ extends Algorithm {

	/**
	 * stores the problem to solve
	 */
	private final Problem problem_;

	private String PROBLEM_NAME = "Problem";

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public GDE3_VerCZ(Problem problem) {
		this.problem_ = problem;
	} // GDE3

	/**
	 * Runs of the GDE3 algorithm.
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated
	 *         solutions as a result of the algorithm execution
	 * @throws JMException
	 */
	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int populationSize;
		int maxEvaluations;
		int evaluations;

		SolutionSet population;
		SolutionSet offspringPopulation;
		SolutionSet union;

		Distance distance;
		Comparator dominance;

		Operator selectionOperator;
		Operator crossoverOperator;

		distance = new Distance();
		dominance = new jmetal.base.operator.comparator.DominanceComparator();

		// Differential evolution parameters
		int r1;
		int r2;
		int r3;
		int jrand;

		// Quality store array
		List<Double> generationalHV = new ArrayList<Double>();
		int reportInterval;
		int currentGen = 0;

		Solution parent[];

		// Read the parameters
		populationSize = ((Integer) this.getInputParameter("populationSize")).intValue();
		maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
		reportInterval = ((Integer) getInputParameter("reportInterval")).intValue();

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

		selectionOperator = operators_.get("selection");
		crossoverOperator = operators_.get("crossover");

		// Initialize the variables
		population = new SolutionSet(populationSize);
		evaluations = 0;

		// Create the initial solutionSet
		Solution newSolution;
		for (int i = 0; i < populationSize; i++) {
			newSolution = new Solution(problem_);
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			evaluations++;
			population.add(newSolution);
		} // for

		QualityIndicator indicator = new QualityIndicator(problem_,
				"data\\input\\trueParetoFronts\\" + PROBLEM_NAME + ".pareto");

		/** record the generational HV of the initial population */
		int cGen = evaluations / reportInterval;
		if (cGen > 0) {

			double hVal = indicator.getHypervolume(population);
			hVal = indicator.getHypervolume(population);
			System.out.println("Hypervolume: " + (evaluations / 100) + " - " + hVal);
			for (int i = 0; i < cGen; i++) {
				generationalHV.add(hVal);
			}
			currentGen = cGen;
		}

		// Generations ...
		while (evaluations < maxEvaluations) {
			// Create the offSpring solutionSet
			offspringPopulation = new SolutionSet(populationSize * 2);

			for (int i = 0; i < (populationSize); i++) {
				// Obtain parents. Two parameters are required: the population
				// and the
				// index of the current individual
				parent = (Solution[]) selectionOperator.execute(new Object[] { population, i });

				Solution child;
				// Crossover. Two parameters are required: the current
				// individual and the
				// array of parents
				child = (Solution) crossoverOperator.execute(new Object[] { population.get(i), parent });

				problem_.evaluate(child);
				problem_.evaluateConstraints(child);
				evaluations++;

				// Dominance test
				int result;
				result = dominance.compare(population.get(i), child);
				if (result == -1) { // Solution i dominates child
					offspringPopulation.add(population.get(i));
				} // if
				else if (result == 1) { // child dominates
					offspringPopulation.add(child);
				} // else if
				else { // the two solutions are non-dominated
					offspringPopulation.add(child);
					offspringPopulation.add(population.get(i));
				} // else
			} // for

			// // Ranking the offspring population
			Ranking ranking = new Ranking(offspringPopulation);

			int remain = populationSize;
			int index = 0;
			SolutionSet front = null;
			population.clear();

			// Obtain the next front
			front = ranking.getSubfront(index);

			while ((remain > 0) && (remain >= front.size())) {
				// Assign crowding distance to individuals
				distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
				// Add the individuals of this front
				for (int k = 0; k < front.size(); k++) {
					population.add(front.get(k));
				} // for

				// Decrement remain
				remain = remain - front.size();

				// Obtain the next front
				index++;
				if (remain > 0) {
					front = ranking.getSubfront(index);
				} // if
			} // while

			// remain is less than front(index).size, insert only the best one
			if (remain > 0) { // front contains individuals to insert
				while (front.size() > remain) {
					distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
					front.sort(new jmetal.base.operator.comparator.CrowdingComparator());
					front.remove(front.size() - 1);
				}
				for (int k = 0; k < front.size(); k++) {
					population.add(front.get(k));
				}

				remain = 0;
			} // if

			// Spea2Fitness spea = new Spea2Fitness(offspringPopulation);
			// spea.fitnessAssign();
			// population = spea.environmentalSelection(populationSize);

			int newGen = evaluations / reportInterval;
			if (newGen > currentGen) {
				double hVal = indicator.getHypervolume(population);
				hVal = indicator.getHypervolume(population);
				System.out.println("Hypervolume: " + (evaluations / 100) + " - " + hVal);
				for (int i = currentGen; i < newGen; i++) {
					generationalHV.add(hVal);
				}
				currentGen = newGen;
			}

		} // while

		// write generationalHV to file
		String sGenHV = "";
		for (Double d : generationalHV) {
			sGenHV += d + ",";
		}

		try {
			File hvFile = new File("data\\output\\runtimePerformance\\GDE3\\SolutionSetSize" + populationSize + "\\"
					+ PROBLEM_NAME + "\\HV.csv");
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
		Ranking ranking = new Ranking(population);
		return ranking.getSubfront(0);
	} // execute
} // GDE3
