/**
 * GDE3_main.java
 *
 * @author Antonio J. Nebro with modifications by Ciprian Zavoianu for runtime hypervolume tracking
 * @version 1.0
 */
package jmetal.metaheuristics.baseline.gde3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.Problem;
import jmetal.base.SolutionSet;
import jmetal.base.operator.crossover.CrossoverFactory;
import jmetal.base.operator.selection.SelectionFactory;
import jmetal.problems.MO_ICOP.ICOP;
import jmetal.util.JMException;

public class GDE3_main_VerCZ {
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	/**
	 * @param args
	 *            Command line arguments.
	 * @throws JMException
	 * @throws IOException
	 * @throws SecurityException
	 *             Usage: three choices -
	 *             jmetal.metaheuristics.nsgaII.NSGAII_main -
	 *             jmetal.metaheuristics.nsgaII.NSGAII_main problemName -
	 *             jmetal.metaheuristics.nsgaII.NSGAII_main problemName
	 *             paretoFrontFile
	 */
	public static void main(String[] args) throws JMException, SecurityException, IOException, ClassNotFoundException {
		List<Problem> problemsToSolve = new ArrayList<Problem>();
		Algorithm algorithm; // The algorithm to use
		Operator selection;
		Operator crossover;

		// QualityIndicator indicators = null; // Object to get quality
		// indicators

		// Logger object and file to store log messages
		// logger_ = Configuration.logger_;
		// fileHandler_ = new FileHandler("GDE3.log");
		// logger_.addHandler(fileHandler_);

		/** 31 benchmark problems */
		/** 7 DTLZ benchmark problems */
		// problemsToSolve.add(new DTLZ1("Real"));
		// problemsToSolve.add(new DTLZ2("Real"));
		// problemsToSolve.add(new DTLZ3("Real"));
		// problemsToSolve.add(new DTLZ4("Real"));
		// problemsToSolve.add(new DTLZ5("Real"));
		// problemsToSolve.add(new DTLZ6("Real"));
		// problemsToSolve.add(new DTLZ7("Real"));
		// // /** 1 benchmark problem based on Kursawe's function */
		// problemsToSolve.add(new Kursawe("Real", 10));
		// // /** 9 LZ09 benchmark problems */
		// problemsToSolve.add(new LZ09_F1("Real"));
		// problemsToSolve.add(new LZ09_F2("Real"));
		// problemsToSolve.add(new LZ09_F3("Real"));
		// problemsToSolve.add(new LZ09_F4("Real"));
		// problemsToSolve.add(new LZ09_F5("Real"));
		// problemsToSolve.add(new LZ09_F6("Real"));
		// problemsToSolve.add(new LZ09_F7("Real"));
		// problemsToSolve.add(new LZ09_F8("Real"));
		// problemsToSolve.add(new LZ09_F9("Real"));
		// // /** 9 WFG benchmark problems */
		// problemsToSolve.add(new WFG1("Real"));
		// problemsToSolve.add(new WFG2("Real"));
		// problemsToSolve.add(new WFG3("Real"));
		// problemsToSolve.add(new WFG4("Real"));
		// problemsToSolve.add(new WFG5("Real"));
		// problemsToSolve.add(new WFG6("Real"));
		// problemsToSolve.add(new WFG7("Real"));
		// problemsToSolve.add(new WFG8("Real"));
		// problemsToSolve.add(new WFG9("Real"));
		// // /** 5 ZDT benchmark problems */
		// problemsToSolve.add(new ZDT1("Real", 30));
		// problemsToSolve.add(new ZDT2("Real", 30));
		// problemsToSolve.add(new ZDT3("Real", 10));
		// problemsToSolve.add(new ZDT4("Real", 10));
		// /** ZDT5 is not real-valued */
		// problemsToSolve.add(new ZDT6("Real"));

		/** Benchmark problems with UNKNOWN PFs */
		Integer problemID = 51;
		Integer dimension = 5;
		Integer objCount = 3;
		Integer k = 3;
		if (args.length != 0) {
			problemID = Integer.parseInt(args[0]);
			dimension = Integer.parseInt(args[1]);
			k = Integer.parseInt(args[2]);
		}

		problemsToSolve.add(new ICOP("Real", problemID, dimension, k, false, objCount));

		int algRepeats = 45;

		for (Problem problem : problemsToSolve) {
			for (int i = 0; i < algRepeats; i++) {
				algorithm = new GDE3_VerCZ(problem);

				// Algorithm parameters
				algorithm.setInputParameter("populationSize", 200);
				algorithm.setInputParameter("reportInterval", 100);
				algorithm.setInputParameter("maxEvaluations", 50000);

				// Crossover operator
				crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover");
				crossover.setParameter("CR", 0.3);
				crossover.setParameter("F", 0.5);

				// Add the operators to the algorithm
				selection = SelectionFactory.getSelectionOperator("DifferentialEvolutionSelection");

				algorithm.addOperator("crossover", crossover);
				algorithm.addOperator("selection", selection);

				// Execute the Algorithm
				long initTime = System.currentTimeMillis();
				SolutionSet finalNonDominatedSet = algorithm.execute();
				long estimatedTime = System.currentTimeMillis() - initTime;

				// Result messages
				// logger_.info("Total execution time: " + estimatedTime +
				// "ms");
				// logger_.info("Variables values have been writen to file
				// VAR");
				// population.printVariablesToFile("VAR");
				// logger_.info("Objectives values have been writen to file
				// FUN");
				// population.printObjectivesToFile("FUN");

				// if (indicators != null) {
				// logger_.info("Quality indicators");
				// logger_.info("Hypervolume: " +
				// indicators.getHypervolume(population));
				// logger_.info("GD : " + indicators.getGD(population));
				// logger_.info("IGD : " + indicators.getIGD(population));
				// logger_.info("Spread : " + indicators.getSpread(population));
				// logger_.info("Epsilon : " +
				// indicators.getEpsilon(population));
				// } // if

				/** Show execution runtime info */
				if ((problem instanceof ICOP)) {
					if (!((ICOP) problem).isVerbose()) {
						System.out.println("Total execution time: " + estimatedTime
								+ "ms. Final non-dominted solution set size: " + finalNonDominatedSet.size());
					}
				} else {
					System.out.println("Total execution time: " + estimatedTime
							+ "ms. Final non-dominted solution set size: " + finalNonDominatedSet.size());
				}

			} // for algRepeats
		} // for problemsToSolve
	}// main
} // GDE3_main
