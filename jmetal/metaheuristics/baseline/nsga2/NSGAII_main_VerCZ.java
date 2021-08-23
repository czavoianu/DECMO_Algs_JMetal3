/**
 * NSGAII_main.java
 *
 * @author Juan J. Durillo
 * @author Antonio J. Nebro
 * @author with modifications by Ciprian Zavoianu for runtime hypervolume tracking
 * @version 1.0
 *   This implementation of NSGA-II makes use of a QualityIndicator object
 *   to obtained the convergence speed of the algorithm. This version is used
 *   in the paper:
 *     A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba 
 *     "A Study of Convergence Speed in Multi-Objective Metaheuristics." 
 *     To be presented in: PPSN'08. Dortmund. September 2008.
 *     
 *   Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 *   included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba 
 *                  "On the Effect of the Steady-State Selection Scheme in 
 *                  Multi-Objective Genetic Algorithms"
 *                  5th International Conference, EMO 2009, pp: 183-197. 
 *                  April 2009)
 *   
 */
package jmetal.metaheuristics.baseline.nsga2;

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
import jmetal.problems.MO_ICOP.ICOP;

public class NSGAII_main_VerCZ {
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	/**
	 * @param args
	 *            Command line arguments.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// count = 0;
		// for (String cp : passList) {
		// count++;
		// System.out.println(count + " " + cp);
		// Process proc;
		// try {
		//
		// File[] drives = File.listRoots();
		// int count1 = drives.length;
		//
		// List<String> cmdList = new ArrayList<String>();
		// cmdList.add("cmd");
		// cmdList.add("/c");
		// // cmdList.add(
		// // "start cmd.exe /K \"cd.. && cd.. && cd.. && cd
		// // Projects\\OPP\\TC && truecrypt.exe /v D:\\utd.tc /lw /q /s /p
		// // "
		// // + cp + " && exit\"");
		//
		// cmdList.add(
		// "start cmd.exe /K \"cd.. && cd.. && cd.. && cd Projects\\OPP\\TC &&
		// truecrypt.exe /v E:\\Personal\\rawE.dump /lw /q /s /p "
		// + cp + " && exit\"");
		//
		// ProcessBuilder pb = new ProcessBuilder();
		// pb.command(cmdList);
		// proc = pb.start();
		//
		// proc.waitFor();
		// BufferedReader reader = new BufferedReader(new
		// InputStreamReader(proc.getInputStream()));
		// String line;
		// while ((line = reader.readLine()) != null) {
		// System.out.println(line);
		// }
		//
		// drives = File.listRoots();
		// int count2 = drives.length;
		//
		// if (count2 - count1 > 0) {
		// System.out.println("correct");
		// } else {
		// System.out.println("wrong");
		// }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		List<Problem> problemsToSolve = new ArrayList<Problem>(); // The
		// problems
		// to solve
		Algorithm algorithm; // The algorithm to use
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		Operator selection; // Selection operator

		// QualityIndicator indicators = null; // Object to get quality
		// indicators

		// Logger object and file to store log messages
		// logger_ = Configuration.logger_;
		// fileHandler_ = new FileHandler("NSGAII.log");
		// logger_.addHandler(fileHandler_);

		/** 31 benchmark problems with known PFs */
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

		int algRepeats = 5;

		for (Problem problem : problemsToSolve) {
			for (int i = 0; i < algRepeats; i++) {

				algorithm = new NSGAII_VerCZ(problem);

				// Algorithm parameters
				algorithm.setInputParameter("populationSize", 200);
				algorithm.setInputParameter("reportInterval", 100);
				algorithm.setInputParameter("maxEvaluations", 50000);

				// Mutation and Crossover for Real codification
				crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover");
				crossover.setParameter("probability", 0.9);
				crossover.setParameter("distributionIndex", 20.0);
				mutation = MutationFactory.getMutationOperator("PolynomialMutation");
				mutation.setParameter("probability", 1.0 / problem.getNumberOfVariables());
				mutation.setParameter("distributionIndex", 20.0);

				// Selection Operator
				selection = SelectionFactory.getSelectionOperator("BinaryTournament2");

				// Add the operators to the algorithm
				algorithm.addOperator("crossover", crossover);
				algorithm.addOperator("mutation", mutation);
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
				//
				// int evaluations = ((Integer)
				// algorithm.getOutputParameter("evaluations")).intValue();
				// logger_.info("Speed : " + evaluations + " evaluations");
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
	} // main
} // NSGAII_main
