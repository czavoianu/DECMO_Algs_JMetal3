/**
 * CompRec.java
 *
 * @author Ciprian Zavoianu
 * @version 1.0
 */
package jmetal.metaheuristics.DECMO2;

public class CompRec implements Comparable<CompRec> {

	private int ID;
	private double value;

	public CompRec(int iD, double value) {
		super();
		ID = iD;
		this.value = value;
	}

	@Override
	public int compareTo(CompRec o) {
		if (this.value > o.getValue()) {
			return 1;
		}
		if (this.value < o.getValue()) {
			return -1;
		}
		return 0;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param iD
	 *            the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

}
