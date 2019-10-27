/**
 * MOEAD_main.java
 *
 * @author Antonio J. Nebro with modifications by Ciprian Zavoianu for runtime hypervolume tracking
 * @version 1.0
 * 
 * This class executes the algorithm described in:
 *   H. Li and Q. Zhang, 
 *   "Multiobjective Optimization Problems with Complicated Pareto Sets,  MOEA/D 
 *   and NSGA-II". IEEE Trans on Evolutionary Computation, vol. 12,  no 2,  
 *   pp 284-302, April/2009.  
 */
package jmetal.metaheuristics.baseline.moead;

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

public class MOEAD_main_VerCZ {
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	/**
	 * @param args
	 *            Command line arguments. The first (optional) argument
	 *            specifies the problem to solve.
	 * @throws JMException
	 * @throws IOException
	 * @throws SecurityException
	 *             Usage: three options - jmetal.metaheuristics.moead.MOEAD_main
	 *             - jmetal.metaheuristics.moead.MOEAD_main problemName -
	 *             jmetal.metaheuristics.moead.MOEAD_main problemName
	 *             ParetoFrontFile
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws JMException, SecurityException, IOException, ClassNotFoundException {
		List<Problem> problemsToSolve = new ArrayList<Problem>();
		Algorithm algorithm; // The algorithm to use
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator

		QualityIndicator indicators = null; // Object to get quality indicators

		// Logger object and file to store log messages
		logger_ = Configuration.logger_;
		fileHandler_ = new FileHandler("MOEAD.log");
		logger_.addHandler(fileHandler_);

		/** 31 benchmark problems */
		/** 7 DTLZ benchmark problems */
		problemsToSolve.add(new DTLZ1("Real")); // 3obj
		problemsToSolve.add(new DTLZ2("Real")); // 3obj
		problemsToSolve.add(new DTLZ3("Real")); // 3obj
		problemsToSolve.add(new DTLZ4("Real")); // 3obj
		problemsToSolve.add(new DTLZ5("Real")); // 3obj
		problemsToSolve.add(new DTLZ6("Real")); // 3obj
		problemsToSolve.add(new DTLZ7("Real")); // 3obj
		/** 1 benchmark problem based on Kursawe's function */
		problemsToSolve.add(new Kursawe("Real", 10)); // 2obj
		/** 9 LZ09 benchmark problems */
		problemsToSolve.add(new LZ09_F1("Real")); // 2obj
		problemsToSolve.add(new LZ09_F2("Real")); // 2obj
		problemsToSolve.add(new LZ09_F3("Real")); // 2obj
		problemsToSolve.add(new LZ09_F4("Real")); // 2obj
		problemsToSolve.add(new LZ09_F5("Real")); // 2obj
		problemsToSolve.add(new LZ09_F6("Real")); // 3obj
		problemsToSolve.add(new LZ09_F7("Real")); // 2obj
		problemsToSolve.add(new LZ09_F8("Real")); // 2obj
		problemsToSolve.add(new LZ09_F9("Real")); // 2obj
		/** 9 WFG benchmark problems */
		problemsToSolve.add(new WFG1("Real")); // 2obj
		problemsToSolve.add(new WFG2("Real")); // 2obj
		problemsToSolve.add(new WFG3("Real")); // 2obj
		problemsToSolve.add(new WFG4("Real")); // 2obj
		problemsToSolve.add(new WFG5("Real")); // 2obj
		problemsToSolve.add(new WFG6("Real")); // 2obj
		problemsToSolve.add(new WFG7("Real")); // 2obj
		problemsToSolve.add(new WFG8("Real")); // 2obj
		problemsToSolve.add(new WFG9("Real")); // 2obj
		/** 5 ZDT benchmark problems */
		problemsToSolve.add(new ZDT1("Real", 30)); // 2obj
		problemsToSolve.add(new ZDT2("Real", 30)); // 2obj
		problemsToSolve.add(new ZDT3("Real", 10)); // 2obj
		problemsToSolve.add(new ZDT4("Real", 10)); // 2obj
		/** ZDT5 is not real-valued */
		problemsToSolve.add(new ZDT6("Real")); // 2obj

		int algRepeats = 1;

		for (Problem problem : problemsToSolve) {
			for (int i = 0; i < algRepeats; i++) {

				// algorithm = new MOEAD_VerCZ(problem);
				algorithm = new MOEAD_DRA_VerCZ(problem);

				// Algorithm parameters
				// lit. recommendation for 2 objectives
				// algorithm.setInputParameter("populationSize", 300);
				// lit. recommendation for 3 objectives is 595
				algorithm.setInputParameter("populationSize", 500);
				algorithm.setInputParameter("reportInterval", 100);
				algorithm.setInputParameter("maxEvaluations", 50000);

				// Directory with the files containing the weight vectors used
				// in
				// Q. Zhang, W. Liu, and H Li, The Performance of a New Version
				// of
				// MOEA/D
				// on CEC09 Unconstrained MOP Test Instances Working Report
				// CES-491,
				// School
				// of CS & EE, University of Essex, 02/2009.
				// http://dces.essex.ac.uk/staff/qzhang/MOEAcompetition/CEC09final/code/ZhangMOEADcode/moead0305.rar
				algorithm.setInputParameter("dataDirectory", "data\\input\\MOEADWeights");

				// Crossover operator
				crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover");
				crossover.setParameter("CR", 1.0);
				crossover.setParameter("F", 0.5);

				// Mutation operator
				mutation = MutationFactory.getMutationOperator("PolynomialMutation");
				mutation.setParameter("probability", 1.0 / problem.getNumberOfVariables());
				mutation.setParameter("distributionIndex", 20.0);

				algorithm.addOperator("crossover", crossover);
				algorithm.addOperator("mutation", mutation);

				// Execute the Algorithm
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
					logger_.info("EPSILON    : " + indicators.getEpsilon(population));
					logger_.info("GD         : " + indicators.getGD(population));
					logger_.info("IGD        : " + indicators.getIGD(population));
					logger_.info("Spread     : " + indicators.getSpread(population));
				} // if
			} // for algRepeats
		} // for problemsToSolve
	} // main
} // MOEAD_main
