/**
 * MOEAD_DRA.java
 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 * @author with modifications by Ciprian Zavoianu for runtime hypervolume tracking
 * @version 1.0
 * 
 * Reference: Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of 
 * MOEA/D on CEC09 Unconstrained MOP Test Instances, Working Report CES-491, 
 * School of CS & EE, University of Essex, 02/2009
 */
package jmetal.metaheuristics.baseline.moead;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

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
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

public class MOEAD_DRA_VerCZ extends Algorithm {

	private String PROBLEM_NAME = "Problem";

	private final Problem problem_;
	/**
	 * Population size
	 */
	private int populationSize_;
	/**
	 * Stores the population
	 */
	private SolutionSet population_;
	/**
	 * Stores the values of the individuals
	 */
	private Solution[] savedValues_;

	private double[] utility_;
	private int[] frequency_;

	/**
	 * Z vector (ideal point)
	 */
	double[] z_;
	/**
	 * Lambda vectors
	 */
	// Vector<Vector<Double>> lambda_ ;
	double[][] lambda_;
	/**
	 * T: neighbour size
	 */
	int T_;
	/**
	 * Neighborhood
	 */
	int[][] neighborhood_;
	/**
	 * delta: probability that parent solutions are selected from neighbourhood
	 */
	double delta_;
	/**
	 * nr: maximal number of solutions replaced by each child solution
	 */
	int nr_;
	Solution[] indArray_;
	String functionType_;
	int evaluations_;
	/**
	 * Operators
	 */
	Operator crossover_;
	Operator mutation_;

	String dataDirectory_;

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public MOEAD_DRA_VerCZ(Problem problem) {
		problem_ = problem;

		functionType_ = "_TCHE1";

	} // DMOEA

	public void comp_utility() {
		double f1, f2, uti, delta;
		for (int n = 0; n < populationSize_; n++) {
			f1 = fitnessFunction(population_.get(n), lambda_[n]);
			f2 = fitnessFunction(savedValues_[n], lambda_[n]);
			delta = f2 - f1;
			if (delta > 0.001)
				utility_[n] = 1.0;
			else {
				uti = 0.95 * (1.0 + delta / 0.001) * utility_[n];
				utility_[n] = uti < 1.0 ? uti : 1.0;
			}

			savedValues_[n] = new Solution(population_.get(n));
		}

	}

	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int maxEvaluations, reportInterval;

		// Quality store array
		List<Double> generationalHV = new ArrayList<Double>();
		int currentGen = 0;

		evaluations_ = 0;
		maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
		populationSize_ = ((Integer) this.getInputParameter("populationSize")).intValue();
		dataDirectory_ = this.getInputParameter("dataDirectory").toString();
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

		QualityIndicator indicator = new QualityIndicator(problem_,
				"data\\input\\trueParetoFronts\\" + PROBLEM_NAME + ".pareto");

		population_ = new SolutionSet(populationSize_);
		savedValues_ = new Solution[populationSize_];
		utility_ = new double[populationSize_];
		frequency_ = new int[populationSize_];
		for (int i = 0; i < utility_.length; i++) {
			utility_[i] = 1.0;
			frequency_[i] = 0;
		}
		indArray_ = new Solution[problem_.getNumberOfObjectives()];

		T_ = 20;
		delta_ = 0.9;
		nr_ = 2;

		T_ = (int) (0.1 * populationSize_);
		delta_ = 0.9;
		nr_ = (int) (0.01 * populationSize_);

		neighborhood_ = new int[populationSize_][T_];

		z_ = new double[problem_.getNumberOfObjectives()];
		// lambda_ = new Vector(problem_.getNumberOfObjectives()) ;
		lambda_ = new double[populationSize_][problem_.getNumberOfObjectives()];

		crossover_ = operators_.get("crossover"); // default: DE crossover
		mutation_ = operators_.get("mutation"); // default: polynomial mutation

		// STEP 1. Initialization
		// STEP 1.1. Compute euclidean distances between weight vectors and find
		// T
		initUniformWeight();
		initNeighborhood();

		// STEP 1.2. Initialize population
		initPopulation();

		/** record the generational HV of the initial population */
		int cGen = evaluations_ / reportInterval;
		if (cGen > 0) {
			double hVal = indicator.getHypervolume(population_);
			hVal = indicator.getHypervolume(population_);
			System.out.println("Hypervolume: " + (evaluations_ / 100) + " - " + hVal);
			for (int i = 0; i < cGen; i++) {
				generationalHV.add(hVal);
			}
			currentGen = cGen;
		}
		System.out.println("Total init evaluations = " + evaluations_);

		// STEP 1.3. Initialize z_
		initIdealPoint();

		int gen = 0;
		// STEP 2. Update
		do {
			int[] permutation = new int[populationSize_];
			Utils_VerCZ.randomPermutation(permutation, populationSize_);
			List<Integer> order = tour_selection(10);

			// System.out.println("La lista: ");
			// for (int i = 0; i < order.size() ; i++) {
			// System.out.print(order.get(i)+" ");
			// }
			// System.out.println();
			for (int i = 0; i < order.size(); i++) {
				// int n = permutation[i]; // or int n = i;
				int n = order.get(i); // or int n = i;
				frequency_[n]++;

				int type;
				double rnd = PseudoRandom.randDouble();

				// STEP 2.1. Mating selection based on probability
				if (rnd < delta_) // if (rnd < realb)
				{
					type = 1; // neighborhood
				} else {
					type = 2; // whole population
				}
				Vector<Integer> p = new Vector<Integer>();
				matingSelection(p, n, 2, type);

				// STEP 2.2. Reproduction
				Solution child;
				Solution[] parents = new Solution[3];

				parents[0] = population_.get(p.get(0));
				parents[1] = population_.get(p.get(1));
				parents[2] = population_.get(n);

				// Apply DE crossover
				child = (Solution) crossover_.execute(new Object[] { population_.get(n), parents });

				// Apply mutation
				mutation_.execute(child);

				// Evaluation
				problem_.evaluate(child);

				evaluations_++;

				// STEP 2.3. Repair. Not necessary

				// STEP 2.4. Update z_
				updateReference(child);

				// STEP 2.5. Update of solutions
				updateProblem(child, n, type);

				int newGen = evaluations_ / reportInterval;
				if (newGen > currentGen) {
					double hVal = indicator.getHypervolume(population_);
					hVal = indicator.getHypervolume(population_);
					System.out.println("Hypervolume: " + (evaluations_ / 100) + " - " + hVal);
					for (int j = currentGen; j < newGen; j++) {
						generationalHV.add(hVal);
					}
					currentGen = newGen;
				}

			} // for

			gen++;
			if (gen % 30 == 0) {
				comp_utility();
			}
		} while (evaluations_ < maxEvaluations);

		for (int i = 0; i < populationSize_; i++) {
			System.out.println(frequency_[i]);
		}

		// write generationalHV to file
		String sGenHV = "";
		int c = 0;
		for (Double d : generationalHV) {
			c++;
			if (c * reportInterval <= maxEvaluations) {
				sGenHV += d + ",";
			}
		}

		try {
			File hvFile = new File("data\\output\\runtimePerformance\\MOEAD_DRA\\SolutionSetSize" + populationSize_
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

		return population_;
	}

	double fitnessFunction(Solution individual, double[] lambda) {
		double fitness;
		fitness = 0.0;

		if (functionType_.equals("_TCHE1")) {
			double maxFun = -1.0e+30;

			for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
				double diff = Math.abs(individual.getObjective(n) - z_[n]);

				double feval;
				if (lambda[n] == 0) {
					feval = 0.0001 * diff;
				} else {
					feval = diff * lambda[n];
				}
				if (feval > maxFun) {
					maxFun = feval;
				}
			} // for

			fitness = maxFun;
		} // if
		else {
			System.out.println("MOEAD.fitnessFunction: unknown type " + functionType_);
			System.exit(-1);
		}
		return fitness;
	} // fitnessEvaluation

	/**
	*
	*/
	void initIdealPoint() throws JMException, ClassNotFoundException {
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			z_[i] = 1.0e+30;
			indArray_[i] = new Solution(problem_);
			problem_.evaluate(indArray_[i]);
			evaluations_++;
		} // for

		for (int i = 0; i < populationSize_; i++) {
			updateReference(population_.get(i));
		} // for
	} // initIdealPoint

	/**
	*
	*/
	public void initNeighborhood() {
		double[] x = new double[populationSize_];
		int[] idx = new int[populationSize_];

		for (int i = 0; i < populationSize_; i++) {
			// calculate the distances based on weight vectors
			for (int j = 0; j < populationSize_; j++) {
				x[j] = Utils_VerCZ.distVector(lambda_[i], lambda_[j]);
				// x[j] = dist_vector(population[i].namda,population[j].namda);
				idx[j] = j;
				// System.out.println("x["+j+"]: "+x[j]+
				// ". idx["+j+"]: "+idx[j]) ;
			} // for

			// find 'niche' nearest neighboring subproblems
			Utils_VerCZ.minFastSort(x, idx, populationSize_, T_);
			// minfastsort(x,idx,population.size(),niche);

			for (int k = 0; k < T_; k++) {
				neighborhood_[i][k] = idx[k];
				// System.out.println("neg["+i+","+k+"]: "+ neighborhood_[i][k])
				// ;
			}
		} // for
	} // initNeighborhood

	/**
	*
	*/
	public void initPopulation() throws JMException, ClassNotFoundException {
		for (int i = 0; i < populationSize_; i++) {
			Solution newSolution = new Solution(problem_);

			problem_.evaluate(newSolution);
			evaluations_++;
			population_.add(newSolution);
			savedValues_[i] = new Solution(newSolution);
		} // for
	} // initPopulation

	/**
	 * initUniformWeight
	 */
	public void initUniformWeight() {
		if ((problem_.getNumberOfObjectives() == 2) && (populationSize_ < 300)) {
			for (int n = 0; n < populationSize_; n++) {
				double a = 1.0 * n / (populationSize_ - 1);
				lambda_[n][0] = a;
				lambda_[n][1] = 1 - a;
				System.out.println(lambda_[n][0]);
				System.out.println(lambda_[n][1]);
			} // for
		} // if
		else {
			String dataFileName;
			dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" + populationSize_ + ".dat";

			System.out.println(dataDirectory_);
			System.out.println(dataDirectory_ + "/" + dataFileName);

			DistributionGenerator_VerCZ dg = new DistributionGenerator_VerCZ();
			dg.createDistribution(problem_.getNumberOfObjectives(), populationSize_,
					dataDirectory_ + "/" + dataFileName);

			try {
				// Open the file
				FileInputStream fis = new FileInputStream(dataDirectory_ + "/" + dataFileName);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isr);

				int numberOfObjectives = 0;
				int i = 0;
				int j = 0;
				String aux = br.readLine();
				while (aux != null) {
					StringTokenizer st = new StringTokenizer(aux);
					j = 0;
					numberOfObjectives = st.countTokens();
					while (st.hasMoreTokens()) {
						double value = (new Double(st.nextToken())).doubleValue();
						lambda_[i][j] = value;
						// System.out.println("lambda["+i+","+j+"] = " + value)
						// ;
						j++;
					}
					aux = br.readLine();
					i++;
				}
				br.close();
			} catch (Exception e) {
				System.out.println(
						"initUniformWeight: failed when reading for file: " + dataDirectory_ + "/" + dataFileName);
				e.printStackTrace();
			}
		} // else

		// System.exit(0) ;
	} // initUniformWeight

	/**
	*
	*/
	public void matingSelection(Vector<Integer> list, int cid, int size, int type) {
		// list : the set of the indexes of selected mating parents
		// cid : the id of current subproblem
		// size : the number of selected mating parents
		// type : 1 - neighborhood; otherwise - whole population
		int ss;
		int r;
		int p;

		ss = neighborhood_[cid].length;
		while (list.size() < size) {
			if (type == 1) {
				r = PseudoRandom.randInt(0, ss - 1);
				p = neighborhood_[cid][r];
				// p = population[cid].table[r];
			} else {
				p = PseudoRandom.randInt(0, populationSize_ - 1);
			}
			boolean flag = true;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) == p) // p is in the list
				{
					flag = false;
					break;
				}
			}

			// if (flag) list.push_back(p);
			if (flag) {
				list.addElement(p);
			}
		}
	} // matingSelection

	public List<Integer> tour_selection(int depth) {

		// selection based on utility
		List<Integer> selected = new ArrayList<Integer>();
		List<Integer> candidate = new ArrayList<Integer>();

		for (int k = 0; k < problem_.getNumberOfObjectives(); k++)
			selected.add(k); // select first m weights
		for (int n = problem_.getNumberOfObjectives(); n < populationSize_; n++)
			candidate.add(n); // set of unselected weights

		while (selected.size() < (int) (populationSize_ / 5.0)) {
			// int best_idd = (int) (rnd_uni(&rnd_uni_init)*candidate.size()),
			// i2;
			int best_idd = (int) (PseudoRandom.randDouble() * candidate.size());
			// System.out.println(best_idd);
			int i2;
			int best_sub = candidate.get(best_idd);
			int s2;
			for (int i = 1; i < depth; i++) {
				i2 = (int) (PseudoRandom.randDouble() * candidate.size());
				s2 = candidate.get(i2);
				// System.out.println("Candidate: "+i2);
				if (utility_[s2] > utility_[best_sub]) {
					best_idd = i2;
					best_sub = s2;
				}
			}
			selected.add(best_sub);
			candidate.remove(best_idd);
		}
		return selected;
	}

	/**
	 * @param individual
	 * @param id
	 * @param type
	 */
	void updateProblem(Solution indiv, int id, int type) {
		// indiv: child solution
		// id: the id of current subproblem
		// type: update solutions in - neighborhood (1) or whole population
		// (otherwise)
		int size;
		int time;

		time = 0;

		if (type == 1) {
			size = neighborhood_[id].length;
		} else {
			size = population_.size();
		}
		int[] perm = new int[size];

		Utils_VerCZ.randomPermutation(perm, size);

		for (int i = 0; i < size; i++) {
			int k;
			if (type == 1) {
				k = neighborhood_[id][perm[i]];
			} else {
				k = perm[i]; // calculate the values of objective function
								// regarding the current subproblem
			}
			double f1, f2;

			f1 = fitnessFunction(population_.get(k), lambda_[k]);
			f2 = fitnessFunction(indiv, lambda_[k]);

			if (f2 < f1) {
				population_.replace(k, new Solution(indiv));
				// population[k].indiv = indiv;
				time++;
			}
			// the maximal number of solutions updated is not allowed to exceed
			// 'limit'
			if (time >= nr_) {
				return;
			}
		}
	} // updateProblem

	/**
	 * 
	 * @param individual
	 */
	void updateReference(Solution individual) {
		for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
			if (individual.getObjective(n) < z_[n]) {
				z_[n] = individual.getObjective(n);

				indArray_[n] = individual;
			}
		}
	} // updateReference
} // MOEAD
