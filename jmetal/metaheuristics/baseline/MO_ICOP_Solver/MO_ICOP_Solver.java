package jmetal.metaheuristics.baseline.MO_ICOP_Solver;

import java.util.ArrayList;
import java.util.List;

import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.Problem;
import jmetal.base.SolutionSet;
import jmetal.base.operator.crossover.CrossoverFactory;
import jmetal.base.operator.mutation.MutationFactory;
import jmetal.base.operator.selection.SelectionFactory;
import jmetal.metaheuristics.DECMO.DECMO_VerCZ;
import jmetal.metaheuristics.DECMO2.DECMO2_VerCZ;
import jmetal.metaheuristics.DECMO2Plus.DECMO2Plus_VerCZ;
import jmetal.metaheuristics.baseline.gde3.GDE3_VerCZ;
import jmetal.metaheuristics.baseline.moead.MOEAD_DRA_VerCZ;
import jmetal.metaheuristics.baseline.nsga2.NSGAII_VerCZ;
import jmetal.metaheuristics.baseline.spea2.SPEA2_VerCZ;
import jmetal.problems.MO_ICOP.ICOP;

public class MO_ICOP_Solver {
	/**
	 * @param args
	 *            args[0] - the name of the solver: "NSGA2", "SPEA2", "GDE3",
	 *            "MOEAD", "DECMO", "DECMO2", "DECMO2++"; args[1] - ICOP
	 *            problemID; args[2] - ICOP problem dimension args[3] - k value
	 *            (used during interpolation).
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		List<Problem> problemsToSolve = new ArrayList<Problem>(); // The
		// problems
		// to solve
		Algorithm algorithm; // The algorithm to use
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		Operator selection; // Selection operator

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
		String algName = "NSGA2";
		Integer problemID = 1;
		Integer dimension = 5;
		Integer k = 4;
		if (args.length != 0) {
			algName = args[0].toUpperCase();
			problemID = Integer.parseInt(args[1]);
			dimension = Integer.parseInt(args[2]);
			k = Integer.parseInt(args[3]);
		}

		Problem pb = new ICOP("Real", problemID, dimension, k, true, 2);

		long initTime = 0;
		long estimatedTime = 0;
		SolutionSet finalNonDominatedSet = null;

		switch (algName) {
		case "NSGA2":
			algorithm = new NSGAII_VerCZ(pb);
			// Algorithm parameters
			algorithm.setInputParameter("populationSize", 200);
			algorithm.setInputParameter("reportInterval", 100);
			algorithm.setInputParameter("maxEvaluations", 50000);
			// Mutation and Crossover for Real codification
			crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover");
			crossover.setParameter("probability", 0.9);
			crossover.setParameter("distributionIndex", 20.0);
			mutation = MutationFactory.getMutationOperator("PolynomialMutation");
			mutation.setParameter("probability", 1.0 / pb.getNumberOfVariables());
			mutation.setParameter("distributionIndex", 20.0);
			// Selection Operator
			selection = SelectionFactory.getSelectionOperator("BinaryTournament2");
			// Add the operators to the algorithm
			algorithm.addOperator("crossover", crossover);
			algorithm.addOperator("mutation", mutation);
			algorithm.addOperator("selection", selection);
			// Execute the Algorithm
			initTime = System.currentTimeMillis();
			finalNonDominatedSet = algorithm.execute();
			estimatedTime = System.currentTimeMillis() - initTime;
			// System.out.println(algName);
			break;
		case "SPEA2":
			algorithm = new SPEA2_VerCZ(pb);
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
			mutation.setParameter("probability", 1.0 / pb.getNumberOfVariables());
			mutation.setParameter("distributionIndex", 20.0);
			// Selection operator
			selection = SelectionFactory.getSelectionOperator("BinaryTournament");
			// Add the operators to the algorithm
			algorithm.addOperator("crossover", crossover);
			algorithm.addOperator("mutation", mutation);
			algorithm.addOperator("selection", selection);
			initTime = System.currentTimeMillis();
			finalNonDominatedSet = algorithm.execute();
			estimatedTime = System.currentTimeMillis() - initTime;
			// System.out.println(algName);
			break;
		case "GDE3":
			algorithm = new GDE3_VerCZ(pb);
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
			initTime = System.currentTimeMillis();
			finalNonDominatedSet = algorithm.execute();
			estimatedTime = System.currentTimeMillis() - initTime;
			// System.out.println(algName);
			break;
		case "MOEAD":
			algorithm = new MOEAD_DRA_VerCZ(pb);
			// Algorithm parameters
			algorithm.setInputParameter("populationSize", 300);
			algorithm.setInputParameter("reportInterval", 100);
			algorithm.setInputParameter("maxEvaluations", 50000);
			// Data directory for pre-computed directional decomposition weights
			algorithm.setInputParameter("dataDirectory", "data\\input\\MOEADWeights");
			// Crossover operator
			crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover");
			crossover.setParameter("CR", 1.0);
			crossover.setParameter("F", 0.5);
			// Mutation operator
			mutation = MutationFactory.getMutationOperator("PolynomialMutation");
			mutation.setParameter("probability", 1.0 / pb.getNumberOfVariables());
			mutation.setParameter("distributionIndex", 20.0);
			algorithm.addOperator("crossover", crossover);
			algorithm.addOperator("mutation", mutation);
			// Execute the Algorithm
			initTime = System.currentTimeMillis();
			finalNonDominatedSet = algorithm.execute();
			estimatedTime = System.currentTimeMillis() - initTime;
			// System.out.println(algName);
			break;
		case "DECMO":
			algorithm = new DECMO_VerCZ(pb);
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
			initTime = System.currentTimeMillis();
			finalNonDominatedSet = algorithm.execute();
			estimatedTime = System.currentTimeMillis() - initTime;
			// System.out.println(algName);
			break;
		case "DECMO2":
			algorithm = new DECMO2_VerCZ(pb);
			/** DECMO2 parameters */
			algorithm.setInputParameter("individualPopulationSize", 100);
			algorithm.setInputParameter("reportInterval", 100);
			algorithm.setInputParameter("maxEvaluations", 50000);
			algorithm.setInputParameter("dataDirectory", "data\\input\\DECMO2Weights");
			/** Execute the Algorithm */
			initTime = System.currentTimeMillis();
			finalNonDominatedSet = algorithm.execute();
			estimatedTime = System.currentTimeMillis() - initTime;
			// System.out.println(algName);
			break;
		case "DECMO2++":
			algorithm = new DECMO2Plus_VerCZ(pb);
			/** DECMO2++ parameters */
			algorithm.setInputParameter("individualPopulationSize", 100);
			algorithm.setInputParameter("reportInterval", 100);
			algorithm.setInputParameter("maxEvaluations", 50000);
			algorithm.setInputParameter("dataDirectory", "data\\input\\DECMO2PlusWeights");
			/** Execute the Algorithm */
			initTime = System.currentTimeMillis();
			finalNonDominatedSet = algorithm.execute();
			estimatedTime = System.currentTimeMillis() - initTime;
			// System.out.println(algName);
			break;
		default:
			throw new RuntimeException("Algorithm" + algName
					+ " is not implemented! Implemented algorithms are: NSGA2, SPEA2, GDE3, MOEAD, DECMO, DECMO2, DECMO2++");
		}

		/** Show execution runtime info */
		if (!(pb instanceof ICOP)) {
			System.out.println("Total execution time: " + estimatedTime + "ms. Final non-dominted solution set size: "
					+ finalNonDominatedSet.size());
		}

	} // main
} // NSGAII_main
