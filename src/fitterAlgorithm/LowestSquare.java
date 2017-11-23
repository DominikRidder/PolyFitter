package fitterAlgorithm;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.math3.linear.RealMatrix;

import functions.ExponentialFunction;
import functions.Function;

/**
 * This Class solving LowestSquare problems to a giving function a + f(x).
 * 
 * @author dridder_local
 *
 */
public class LowestSquare implements FitterAlgorithm {

	private float[][] a;

	private ExponentialFunction givenFunction;

	private int degree;

	public LowestSquare(int degree) {
		this.degree = degree;
	}

	@Override
	public int getDegree() {
		return degree;
	}

	@Override
	public Function getFunction() {
		return givenFunction;
	}

	@Override
	public double getProblem() {
		if (a == null) {
			return -1;
		}
		//
		// double prob = 0;
		// RealMatrix C = A.multiply(x).subtract(b);
		// RealVector D = C.getColumnVector(0);
		//
		// for (int i = 0; i < D.getDimension(); i++) {
		// prob += Math.abs(D.getEntry(i));
		// }

		double prob = 0;
		for (int i = 0; i < a.length; i++) {
			prob += Math.abs(givenFunction.f(a[i][0]) - a[i][1]);
		}
		return prob;
	}

	@Override
	public void setMaxIterations(int i) {
		// No effect
	}

	@Override
	public void setDegree(int d) {
		degree = d;
	}

	@Override
	public Function fit(ArrayList<float[]> pointcloud, Function f) {
		this.givenFunction = (ExponentialFunction) f;

		int numberofpoints = 0;
		for (int i = 0; i < pointcloud.size(); i++) {
			if (pointcloud.get(i)[2] == 0) {
				numberofpoints++;
			}
		}

		a = new float[numberofpoints][pointcloud.get(0).length - 1];
		int index = 0;
		for (float[] c : pointcloud) {
			if (c[2] == 0) {
				a[index++] = c;
			}
		}
		fit(a);

		return getFunction();
	}

	public static void printMatrix(String name, RealMatrix brm) {
		System.out.println(name + "{");
		for (int i = 0; i < brm.getRowDimension(); i++) {
			for (int j = 0; j < brm.getColumnDimension(); j++) {
				System.out.print(brm.getEntry(i, j) + ", ");
			}
			System.out.println();
		}
		System.out.println("}");
	}

	/**
	 * Uses the formula: x = (AT * A)^-1 * (AT*b). This way the Problem become
	 * minimal.
	 */
	public void fit(float[][] points) {
		int n = points.length - 1;

		float min = Float.MAX_VALUE;
		float help = 0;
		for (int i = 0; i < n; i++) {
			float yi = points[i][1];
			if (min > yi) {
				min = yi;
			}
		}

		if (min <= 0) {
			min *= -1;
			help = 1;
		} else if (min < 1) {
			min = 1 - min;
		} else {
			min = 0;
		}

		/******* Sums **********/
		/*
		 * double sumx2y = 0; // sum((x^2) * y) double sumylny = 0; //
		 * sum(y*ln(y)) double sumxy = 0; // sum(x*y) double sumxylny = 0; //
		 * sum(x*y*ln(y)) double sumy = 0; // sum(y)
		 * 
		 * for (int i=0; i<n; i++){ float xi = points[i][0]; float yi =
		 * points[i][1]+min;
		 * 
		 * sumx2y += xi*xi*yi; sumylny += yi*Math.log(yi); sumxy += xi*yi;
		 * sumxylny += xi*yi*Math.log(yi); sumy += yi; }
		 * /***********************
		 * 
		 * double a = (sumx2y*sumylny-sumxy*sumxylny)/(sumy*sumx2y-sumxy*sumxy);
		 * 
		 * double B = (sumy*sumxylny-sumxy*sumylny)/(sumy*sumx2y-sumxy*sumxy);
		 * 
		 * double A = Math.pow(Math.E, a);
		 */

		BigDecimal sumx2y = new BigDecimal(0); // sum((x^2) * y)
		BigDecimal sumylny = new BigDecimal(0); // sum(y*ln(y))
		BigDecimal sumxy = new BigDecimal(0); // sum(x*y)
		BigDecimal sumxylny = new BigDecimal(0); // sum(x*y*ln(y))
		BigDecimal sumy = new BigDecimal(0); // sum(y)

		BigDecimal bigmin = new BigDecimal(min);
		BigDecimal bighelp = new BigDecimal(help);
		
		for (int i = 0; i < n; i++) {
			BigDecimal xi = new BigDecimal(points[i][0]);
			BigDecimal yi = new BigDecimal(points[i][1]).add(bigmin).add(bighelp);

			sumx2y = sumx2y.add(xi.multiply(xi).multiply(yi));
			sumylny = sumylny.add(yi.multiply(new BigDecimal(Math.log(yi
					.floatValue()))));
			sumxy = sumxy.add(xi.multiply(yi));
			sumxylny = sumxylny.add(xi.multiply(yi).multiply(
					new BigDecimal(Math.log(yi.floatValue()))));
			sumy = sumy.add(yi);
		}
		/***********************/

		double a = ((sumx2y.multiply(sumylny)).subtract(sumxy
				.multiply(sumxylny)).divide(
				((sumy.multiply(sumx2y)).subtract(sumxy.multiply(sumxy))),
				BigDecimal.ROUND_HALF_EVEN)).floatValue();

		double B = (sumy.multiply(sumxylny).subtract(sumxy.multiply(sumylny)))
				.divide((sumy.multiply(sumx2y).subtract(sumxy.multiply(sumxy))),
						BigDecimal.ROUND_HALF_EVEN).floatValue();

		double A = Math.pow(Math.E, a);

		givenFunction.setA(A);
		givenFunction.setB(B);
		givenFunction.setC(-min - help);
	}

}
