/**
 * SPEA2_main.java
 *
 * @author Juan J. Durillo with modifications by Ciprian Zavoianu for runtime hypervolume tracking
 * @version 1.0
 */
package jmetal.metaheuristics.baseline.spea2;

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
import jmetal.util.Configuration;
import jmetal.util.JMException;

public class SPEA2_main_VerCZ {
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	/**
	 * @param args
	 *            Command line arguments. The first (optional) argument
	 *            specifies the problem to solve.
	 * @throws JMException
	 * @throws IOException
	 * @throws SecurityException
	 *             Usage: three options -
	 *             jmetal.metaheuristics.mocell.MOCell_main -
	 *             jmetal.metaheuristics.mocell.MOCell_main problemName -
	 *             jmetal.metaheuristics.mocell.MOCell_main problemName
	 *             ParetoFrontFile
	 */
	public static void main(String[] args) throws JMException, IOException, ClassNotFoundException {
		List<Problem> problemsToSolve = new ArrayList<Problem>(); // The problem
																	// to solve
		Algorithm algorithm; // The algorithm to use
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		Operator selection; // Selection operator

		// Logger object and file to store log messages
		logger_ = Configuration.logger_;
		fileHandler_ = new FileHandler("SPEA2.log");
		logger_.addHandler(fileHandler_);

		QualityIndicator indicators = null; // Object to get quality indicators

		// Logger object and file to store log messages
		logger_ = Configuration.logger_;
		fileHandler_ = new FileHandler("MOEAD.log");
		logger_.addHandler(fileHandler_);

		/** 31 benchmark problems */
		/** 7 DTLZ benchmark problems */
		problemsToSolve.add(new DTLZ1("Real"));
		problemsToSolve.add(new DTLZ2("Real"));
		problemsToSolve.add(new DTLZ3("Real"));
		problemsToSolve.add(new DTLZ4("Real"));
		problemsToSolve.add(new DTLZ5("Real"));
		problemsToSolve.add(new DTLZ6("Real"));
		problemsToSolve.add(new DTLZ7("Real"));
		// /** 1 benchmark problem based on Kursawe's function */
		problemsToSolve.add(new Kursawe("Real", 10));
		// /** 9 LZ09 benchmark problems */
		problemsToSolve.add(new LZ09_F1("Real"));
		problemsToSolve.add(new LZ09_F2("Real"));
		problemsToSolve.add(new LZ09_F3("Real"));
		problemsToSolve.add(new LZ09_F4("Real"));
		problemsToSolve.add(new LZ09_F5("Real"));
		problemsToSolve.add(new LZ09_F6("Real"));
		problemsToSolve.add(new LZ09_F7("Real"));
		problemsToSolve.add(new LZ09_F8("Real"));
		problemsToSolve.add(new LZ09_F9("Real"));
		// /** 9 WFG benchmark problems */
		problemsToSolve.add(new WFG1("Real"));
		problemsToSolve.add(new WFG2("Real"));
		problemsToSolve.add(new WFG3("Real"));
		problemsToSolve.add(new WFG4("Real"));
		problemsToSolve.add(new WFG5("Real"));
		problemsToSolve.add(new WFG6("Real"));
		problemsToSolve.add(new WFG7("Real"));
		problemsToSolve.add(new WFG8("Real"));
		problemsToSolve.add(new WFG9("Real"));
		// /** 5 ZDT benchmark problems */
		problemsToSolve.add(new ZDT1("Real", 30));
		problemsToSolve.add(new ZDT2("Real", 30));
		problemsToSolve.add(new ZDT3("Real", 10));
		problemsToSolve.add(new ZDT4("Real", 10));
		/** ZDT5 is not real-valued */
		problemsToSolve.add(new ZDT6("Real"));

		int algRepeats = 50;

		for (Problem problem : problemsToSolve) {
			for (int i = 0; i < algRepeats; i++) {

				algorithm = new SPEA2_VerCZ(problem);

				// Algorithm parameters
				algorithm.setInputParameter("populationSize", 200);
				/** fixed to population size in SPEA2_VerCZ.java class */
				// algorithm.setInputParameter("archiveSize", 200);
				algorithm.setInputParameter("reportInterval", 100);
				algorithm.setInputParameter("maxEvaluations", 50000);

				// Mutation and crossover for real codification
				crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover");
				crossover.setParameter("probability", 0.9);
				crossover.setParameter("distributionIndex", 20.0);
				mutation = MutationFactory.getMutationOperator("PolynomialMutation");
				mutation.setParameter("probability", 1.0 / problem.getNumberOfVariables());
				mutation.setParameter("distributionIndex", 20.0);

				// Selection operator
				selection = SelectionFactory.getSelectionOperator("BinaryTournament");

				// Add the operators to the algorithm
				algorithm.addOperator("crossover", crossover);
				algorithm.addOperator("mutation", mutation);
				algorithm.addOperator("selection", selection);

				// Execute the algorithm
				long initTime = System.currentTimeMillis();
				SolutionSet population = algorithm.execute();
				long estimatedTime = System.currentTimeMillis() - initTime;

				// Result messages
				logger_.info("Total execution time: " + estimatedTime + "ms");
				logger_.info("Objectives values have been writen to file FUN");
				population.printObjectivesToFile("FUN");
				logger_.info("Variables values have been writen to file VAR");
				population.printVariablesToFile("VAR");

				if (indicators != null) {
					logger_.info("Quality indicators");
					logger_.info("Hypervolume: " + indicators.getHypervolume(population));
					logger_.info("GD         : " + indicators.getGD(population));
					logger_.info("IGD        : " + indicators.getIGD(population));
					logger_.info("Spread     : " + indicators.getSpread(population));
					logger_.info("Epsilon    : " + indicators.getEpsilon(population));
				} // if
			} // for algRepeats
		} // for problemsToSolve
	}// main
} // SPEA2_main.java
