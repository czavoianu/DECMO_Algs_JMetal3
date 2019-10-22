/**
 * DirectionRec.java
 *
 * @author Ciprian Zavoianu
 * @version 1.0
 */
package jmetal.metaheuristics.DECMO2;

import jmetal.base.Solution;

public class DirectionRec {
	private int ID;
	private double[] weightVector;
	private Solution currSol;
	private double fitnessValue;
	private int nfeSinceLastUpdate;

	public DirectionRec(int iD, double[] weightVector, Solution currSol, double fitnessValue, int nfeSinceLastUpdate) {
		super();
		ID = iD;
		this.weightVector = weightVector;
		this.currSol = currSol;
		this.fitnessValue = fitnessValue;
		this.nfeSinceLastUpdate = nfeSinceLastUpdate;
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
	 */
	public void setCurrSol(Solution currSol) {
		this.currSol = currSol;
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

}
