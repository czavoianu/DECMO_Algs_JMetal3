/**
 * DECMO_main_VerCZ.java
 *
 * @author Ciprian Zavoianu
 * @version 1.0
 */
package jmetal.metaheuristics.DECMO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import jmetal.base.Algorithm;
import jmetal.base.Problem;
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
import jmetal.util.JMException;

public class DECMO_main_VerCZ {
	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	/**
	 * @param args
	 *            Command line arguments.
	 * @throws JMException
	 * @throws IOException
	 * @throws SecurityException
	 */
	public static void main(String[] args) throws JMException, SecurityException, IOException, ClassNotFoundException {
		List<Problem> problemsToSolve = new ArrayList<Problem>();
		Algorithm algorithm; // The algorithm to use

		/** 31 benchmark problems */
		/** 7 DTLZ benchmark problems */
		problemsToSolve.add(new DTLZ1("Real"));
		problemsToSolve.add(new DTLZ2("Real"));
		problemsToSolve.add(new DTLZ3("Real"));
		problemsToSolve.add(new DTLZ4("Real"));
		problemsToSolve.add(new DTLZ5("Real"));
		problemsToSolve.add(new DTLZ6("Real"));
		problemsToSolve.add(new DTLZ7("Real"));
		/** 1 benchmark problem based on Kursawe's function */
		problemsToSolve.add(new Kursawe("Real", 10));
		/** 9 LZ09 benchmark problems */
		problemsToSolve.add(new LZ09_F1("Real"));
		problemsToSolve.add(new LZ09_F2("Real"));
		problemsToSolve.add(new LZ09_F3("Real"));
		problemsToSolve.add(new LZ09_F4("Real"));
		problemsToSolve.add(new LZ09_F5("Real"));
		problemsToSolve.add(new LZ09_F6("Real"));
		problemsToSolve.add(new LZ09_F7("Real"));
		problemsToSolve.add(new LZ09_F8("Real"));
		problemsToSolve.add(new LZ09_F9("Real"));
		/** 9 WFG benchmark problems */
		problemsToSolve.add(new WFG1("Real"));
		problemsToSolve.add(new WFG2("Real"));
		problemsToSolve.add(new WFG3("Real"));
		problemsToSolve.add(new WFG4("Real"));
		problemsToSolve.add(new WFG5("Real"));
		problemsToSolve.add(new WFG6("Real"));
		problemsToSolve.add(new WFG7("Real"));
		problemsToSolve.add(new WFG8("Real"));
		problemsToSolve.add(new WFG9("Real"));
		/** 5 ZDT benchmark problems */
		problemsToSolve.add(new ZDT1("Real", 30));
		problemsToSolve.add(new ZDT2("Real", 30));
		problemsToSolve.add(new ZDT3("Real", 10));
		problemsToSolve.add(new ZDT4("Real", 10));
		/** ZDT5 is not real-valued */
		problemsToSolve.add(new ZDT6("Real"));

		/**
		 * number of times each problem should be solved (i.e., independent
		 * algorithm iterations)
		 */
		int algRepeats = 1;
		for (Problem problem : problemsToSolve) {
			for (int i = 0; i < algRepeats; i++) {
				algorithm = new DECMO_VerCZ(problem);

				/** DECMO parameters */
				/**
				 * The size of each subpopulation. The total population size is
				 * double the subpopulation size
				 */
				algorithm.setInputParameter("individualPopulationSize", 100);
				/**
				 * The number of fitness evaluations to be performed during the
				 * optimization
				 */
				algorithm.setInputParameter("maxEvaluations", 50000);
				/**
				 * After how many new individuals is algorithm progress (i.e.,
				 * hypervolume) computed. In total the algorithm will evaluate
				 * populationSize*2*maxIterations individuals
				 */
				algorithm.setInputParameter("reportInterval", 100);

				/** Execute the Algorithm */
				long initTime = System.currentTimeMillis();
				SolutionSet finalNonDominatedSet = algorithm.execute();
				long estimatedTime = System.currentTimeMillis() - initTime;

				/** Show execution info */
				System.out.println("Total execution time: " + estimatedTime
						+ "ms. Final non-dominted solution set size: " + finalNonDominatedSet.size());
			} // for repeats
		} // for problems to solve
	}// main
} // DECMO_main_VerCZ
