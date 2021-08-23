/**
 * SPEA2.java
 * @author Juan J. Durillo with modifications by Ciprian Zavoianu for runtime hypervolume tracking
 * @version 1.0
 */

package jmetal.metaheuristics.baseline.spea2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import jmetal.problems.MO_ICOP.ICOP;
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
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.Spea2Fitness;

/**
 * This class representing the SPEA2 algorithm
 */
public class SPEA2_VerCZ extends Algorithm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1375841088594755478L;

	/**
	 * Defines the number of tournaments for creating the mating pool
	 */
	public static final int TOURNAMENTS_ROUNDS = 1;

	private String PROBLEM_NAME = "Problem";

	final static String UNKNOWN_PF = "UNKNOWN_PF";

	/**
	 * Stores the problem to solve
	 */
	private final Problem problem_;

	/**
	 * Constructor. Create a new SPEA2 instance
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public SPEA2_VerCZ(Problem problem) {
		this.problem_ = problem;
	} // Spea2

	/**
	 * Runs of the Spea2 algorithm.
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated
	 *         solutions as a result of the algorithm execution
	 * @throws JMException
	 */
	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int reportInterval, populationSize, archiveSize, maxEvaluations, evaluations;
		Operator crossoverOperator, mutationOperator, selectionOperator;
		SolutionSet solutionSet, archive, offSpringSolutionSet;

		// Quality store array
		List<Double> generationalHV = new ArrayList<Double>();
		int currentGen = 0;

		// Read the params
		populationSize = ((Integer) getInputParameter("populationSize")).intValue();
		// archiveSize = ((Integer)
		// getInputParameter("archiveSize")).intValue();
		archiveSize = populationSize;
		reportInterval = ((Integer) getInputParameter("reportInterval")).intValue();
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();

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

		if (problem_ instanceof ICOP) {
			PROBLEM_NAME = UNKNOWN_PF;
		}

		// Read the operators
		crossoverOperator = operators_.get("crossover");
		mutationOperator = operators_.get("mutation");
		selectionOperator = operators_.get("selection");

		// Initialize the variables
		solutionSet = new SolutionSet(populationSize);
		archive = new SolutionSet(archiveSize);
		evaluations = 0;

		// -> Create the initial solutionSet
		Solution newSolution;
		for (int i = 0; i < populationSize; i++) {
			newSolution = new Solution(problem_);
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			evaluations++;
			solutionSet.add(newSolution);
		}

		QualityIndicator indicator = null;
		if (!PROBLEM_NAME.equals(UNKNOWN_PF)) {
			indicator = new QualityIndicator(problem_, "data\\input\\trueParetoFronts\\" + PROBLEM_NAME + ".pareto");
		} else {
			if (!((ICOP) problem_).isVerbose()) {
				indicator = new QualityIndicator(problem_,
						"jmetal\\problems\\MO_ICOP\\bestKnownParetoFronts\\" + "d" + ((ICOP) problem_).getDimension()
								+ "_p" + ((ICOP) problem_).getProblemID() + "_k" + ((ICOP) problem_).getK() + ".csv");
			}
		}

		/** record the generational HV of the initial population */
		int cGen = evaluations / reportInterval;
		if (cGen > 0) {

			double hVal = -1.0;
			if (indicator != null) {
				hVal = indicator.getHypervolume(solutionSet);
			}
			if (!(problem_ instanceof ICOP)) {
				System.out.println("Hypervolume: " + (evaluations / 100) + " - " + hVal);
			}

			for (int i = 0; i < cGen; i++) {
				generationalHV.add(hVal);
			}
			currentGen = cGen;
		}

		/** The main loop of the algorithm */
		while (evaluations < maxEvaluations) {
			SolutionSet union = (solutionSet).union(archive);
			Spea2Fitness spea = new Spea2Fitness(union);
			spea.fitnessAssign();
			archive = spea.environmentalSelection(archiveSize);
			// Create a new offspringPopulation
			offSpringSolutionSet = new SolutionSet(populationSize);
			Solution[] parents = new Solution[2];
			while (offSpringSolutionSet.size() < populationSize) {
				int j = 0;
				do {
					j++;
					parents[0] = (Solution) selectionOperator.execute(archive);
				} while (j < SPEA2_VerCZ.TOURNAMENTS_ROUNDS); // do-while
				int k = 0;
				do {
					k++;
					parents[1] = (Solution) selectionOperator.execute(archive);
				} while (k < SPEA2_VerCZ.TOURNAMENTS_ROUNDS); // do-while

				// make the crossover
				Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
				mutationOperator.execute(offSpring[0]);
				problem_.evaluate(offSpring[0]);
				problem_.evaluateConstraints(offSpring[0]);
				offSpringSolutionSet.add(offSpring[0]);
				evaluations++;

				int newGen = evaluations / reportInterval;
				if (newGen > currentGen) {

					double hVal = -1.0;
					if (indicator != null) {
						hVal = indicator.getHypervolume(archive);
						System.out.println("Hypervolume: " + (evaluations / 100) + " - " + hVal);
					}

					for (int i = currentGen; i < newGen; i++) {
						generationalHV.add(hVal);
					}
					currentGen = newGen;
				}
			} // while
				// End Create a offSpring solutionSet
			solutionSet = offSpringSolutionSet;
		} // while

		// write generationalHV to file
		if (indicator != null) {
			String sGenHV = "";
			for (Double d : generationalHV) {
				sGenHV += d + ",";
			}

			String fName = "data\\output\\runtimePerformance\\SPEA2\\SolutionSetSize" + populationSize + "\\"
					+ PROBLEM_NAME + "\\HV.csv";

			if ((problem_ instanceof ICOP)) {
				if (!((ICOP) problem_).isVerbose()) {
					fName = "data\\output\\runtimePerformance\\SPEA2\\SolutionSetSize" + populationSize
							+ "\\MO_ICOP_GenWise\\" + "d" + ((ICOP) problem_).getDimension() + "_p"
							+ ((ICOP) problem_).getProblemID() + "_k" + ((ICOP) problem_).getK() + "\\HV.csv";
				}
			}

			try {
				File hvFile = new File(fName);
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
		}

		Ranking ranking = new Ranking(archive);
		return ranking.getSubfront(0);
	} // execute
} // Spea2
