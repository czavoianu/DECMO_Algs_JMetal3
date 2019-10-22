/**
 * DRec.java
 *
 * @author Ciprian Zavoianu
 * @version 1.0
 */
package jmetal.metaheuristics.DECMO2Plus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.Variable;
import jmetal.util.JMException;

public class DRec {
	private int ID;
	private double[] weightVector;
	private Solution currSol;
	private double fitnessValue;
	private int nfeSinceLastUpdate;
	private Problem problem;
	private LinkedList<Solution> historicSolutions = new LinkedList<Solution>();

	public DRec(int iD, double[] weightVector, Solution currSol, double fitnessValue, int nfeSinceLastUpdate,
			Problem problem) {
		super();
		ID = iD;
		this.weightVector = weightVector;
		this.currSol = currSol;
		this.fitnessValue = fitnessValue;
		this.nfeSinceLastUpdate = nfeSinceLastUpdate;
		this.problem = problem;
	}

	/**
	 * @return the currSol
	 */
	public Solution getCurrSol() {
		return currSol;
	}

	/**
	 * @return the fitnessValue
	 */
	public double getFitnessValue() {
		return fitnessValue;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @return the nfeSinceLastUpdate
	 */
	public int getNfeSinceLastUpdate() {
		return nfeSinceLastUpdate;
	}

	/**
	 * @return the lambdaVector
	 */
	public double[] getWeightVector() {
		return weightVector;
	}

	/**
	 * @param currSol
	 *            the currSol to set
	 * @throws ClassNotFoundException
	 * @throws JMException
	 */
	public void setCurrSol(Solution currSol) throws ClassNotFoundException, JMException {
		this.currSol = currSol;
		this.historicSolutions.addLast(cloneSolution(currSol));
	}

	/**
	 * @param fitnessValue
	 *            the fitnessValue to set
	 */
	public void setFitnessValue(double fitnessValue) {
		this.fitnessValue = fitnessValue;
	}

	/**
	 * @param iD
	 *            the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}

	/**
	 * @param nfeSinceLastUpdate
	 *            the nfeSinceLastUpdate to set
	 */
	public void setNfeSinceLastUpdate(int nfeSinceLastUpdate) {
		this.nfeSinceLastUpdate = nfeSinceLastUpdate;
	}

	/**
	 * @param lambdaVector
	 *            the lambdaVector to set
	 */
	public void setWeightVector(double[] lambdaVector) {
		this.weightVector = lambdaVector;
	}

	public int getHistoricalSolutionsSize() {
		// System.out.println(this.ID + " : " + this.historicSolutions.size());
		return this.historicSolutions.size();
	}

	private Solution cloneSolution(Solution originalSolution) throws ClassNotFoundException, JMException {
		Solution result = null;
		result = new Solution(problem);
		Variable[] variables = result.getDecisionVariables();
		for (int k = 0; k < variables.length; k++) {
			variables[k].setValue(originalSolution.getDecisionVariables()[k].getValue());
		}
		result.setDecisionVariables(variables);
		return result;
	}

	public Solution getLocalLinearSolution(Random rndGen) throws ClassNotFoundException, JMException {
		Solution result = null;
		result = new Solution(problem);
		Variable[] variables = result.getDecisionVariables();

		for (int k = 0; k < variables.length; k++) {

			double deltaVal = this.historicSolutions.get(this.historicSolutions.size() - 1).getDecisionVariables()[k]
					.getValue()
					- this.historicSolutions.get(this.historicSolutions.size() - 2).getDecisionVariables()[k]
							.getValue();

			double newValue = this.historicSolutions.get(this.historicSolutions.size() - 1).getDecisionVariables()[k]
					.getValue() + (rndGen.nextDouble() * 0.5 * deltaVal);

			if (newValue < problem.getLowerLimit(k)) {
				newValue = problem.getLowerLimit(k) + Math.abs(problem.getLowerLimit(k) - newValue);
			}
			if (newValue > problem.getUpperLimit(k)) {
				newValue = problem.getUpperLimit(k) - Math.abs(newValue - problem.getUpperLimit(k));
			}

			if (rndGen.nextDouble() < 0.5) {
				variables[k].setValue(newValue);
			} else {
				variables[k].setValue(
						(this.historicSolutions.get(this.historicSolutions.size() - 1).getDecisionVariables()[k]
								.getValue()
						+ this.historicSolutions.get(this.historicSolutions.size() - 2).getDecisionVariables()[k]
								.getValue()) / 2.0);
			}

			// System.out
			// .println(
			// "k=" + k + " parent="
			// + this.historicSolutions.get(this.historicSolutions.size() - 1)
			// .getDecisionVariables()[k].getValue()
			// + " grandparent="
			// + this.historicSolutions.get(this.historicSolutions.size() - 2)
			// .getDecisionVariables()[k].getValue()
			// + " final value=" + variables[k].getValue());
		}

		result.setDecisionVariables(variables);
		return result;
	}

	public Solution getPresidentSolution(int maxBins, int maxHist, Random rndGen)
			throws ClassNotFoundException, JMException {
		Solution result = null;
		result = new Solution(problem);
		Variable[] variables = result.getDecisionVariables();

		List<Integer> votes = new ArrayList<Integer>();
		for (int i = 0; i < maxBins; i++) {
			votes.add(0);
		}

		int maxIt = Math.min(maxHist, this.historicSolutions.size());
		for (int k = 0; k < variables.length; k++) {
			double binWidth = (1.0 * problem.getUpperLimit(k) - problem.getLowerLimit(k)) / maxBins;
			for (int i = 0; i < maxBins; i++) {
				votes.set(i, 0);
			}
			// System.out.println("k=" + k + " L=" + problem.getLowerLimit(k) +
			// " U=" + problem.getUpperLimit(k)
			// + " bindWidth=" + binWidth);
			for (int j = 0; j < maxIt; j++) {
				int value = 5;
				if (j == 0) {
					value = 50;
				}
				if (j == 1) {
					value = 40;
				}
				if (j == 2) {
					value = 30;
				}
				if (j == 3) {
					value = 20;
				}
				if (j == 4) {
					value = 10;
				}
				int index = (int) Math.floor(
						(this.historicSolutions.get(this.historicSolutions.size() - 1 - j).getDecisionVariables()[k]
								.getValue() - problem.getLowerLimit(k)) / (binWidth));
				if (index == maxBins) {
					index = maxBins - 1;
				}
				votes.set(index, votes.get(index) + value);
				// System.out.println(" Hist=" + j + " val="
				// + this.historicSolutions.get(this.historicSolutions.size() -
				// 1 - j).getDecisionVariables()[k]
				// + " index=" + index + " value=" + value);
			}
			int max = 0;
			int maxIndex = -1;
			// System.out.println("-----VOTES------");
			for (int i = 0; i < maxBins; i++) {
				// System.out.println("bin=" + i + " votes=" + votes.get(i));
				if (max < (votes.get(i))) {
					max = votes.get(i);
					maxIndex = i;
				}
			}
			// System.out.println("---------------");
			variables[k].setValue(problem.getLowerLimit(k) + (maxIndex + rndGen.nextDouble()) * binWidth);
			// System.out.println(" final value:" + variables[k].getValue());
		}

		result.setDecisionVariables(variables);
		return result;
	}

}
