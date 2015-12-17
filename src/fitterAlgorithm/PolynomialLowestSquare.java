package fitterAlgorithm;

import java.util.ArrayList;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import functions.Function;
import functions.PolynomialFunction2D;
import polyfitter.Point;

/**
 * This class is an example for an implementation of a FitterAlgorithm.
 */
public class PolynomialLowestSquare implements FitterAlgorithm {

	private RealMatrix A;

	private RealMatrix b;

	private RealMatrix polynom;

	private int degree;
	// I dont know, how the iterations fits into the algorithm. They are not
	// used yet.
	// private int iterations;

	public PolynomialLowestSquare(int degree) {
		this.degree = degree;
	}

	public double getProblem() {
		if (polynom == null) {
			System.out
					.println("You have to perform the fit method before you can get a problem.");
			return -1;
		}
		double prob = 0;
		RealMatrix C = A.multiply(polynom).subtract(b);
		RealVector D = C.getColumnVector(0);

		for (int i = 0; i < D.getDimension(); i++) {
			prob += Math.abs(D.getEntry(i));
		}
		return prob;
	}

	public Function getFunction() {
		if (polynom == null) {
			return null;
		}
		return new PolynomialFunction2D(polynom.getColumn(0));
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int d) {
		if (d < 0) {
			System.out
					.println("A negative degree don't make sense. Setting degree = 0.");
			d = 0;
		}
		degree = d;
	}

	public void setMaxIterations(int i) {
	}

	public Function fit(ArrayList<float[]> pointcloud, Function f) {
//		float[][] a = new float[0][0];
//		a = pointcloud.toArray(a);
//		fit(a);
		
		setUpAandB(pointcloud);
		QRDecomposition comp = new QRDecomposition(A);
		polynom = comp.getSolver().solve(b);
		
		return getFunction();
	}

	private void setUpAandB(ArrayList<float[]> pointcloud) {
		int numberofpoints = pointcloud.size();
		double[][] a = new double[numberofpoints][degree + 1];
		double[][] B = new double[numberofpoints][1];
		int i = 0;
		for (int c = 0; c < numberofpoints; c++) {
			B[i][0] = pointcloud.get(i)[pointcloud.get(0).length - 1];
			for (int j = degree; j >= 0; j--) {
				a[i][degree - j] = Math.pow(pointcloud.get(i)[0], j);
			}
			i++;
		}
		
		A = new BlockRealMatrix(a);

		b = new BlockRealMatrix(B);
	}

	/**
	 * Uses the formula: x = (AT * A)^-1 * (AT*b). This way the Problem become
	 * minimal.
	 */
	public double[] fit(float[][] points){
		setUpAandB(points);
		
		QRDecomposition comp = new QRDecomposition(A);
		polynom = comp.getSolver().solve(b);
		return polynom.getColumn(0);
	}
	
	private void setUpAandB(float[][] points) {
		int numberofpoints = points.length;
			double[][] a = new double[numberofpoints][degree + 1];
			double[][] B = new double[numberofpoints][1];
			int i = 0;
			for (int c = 0; c < numberofpoints; c++) {
				B[i][0] = points[i][points[0].length - 1];
				for (int j = degree; j >= 0; j--) {
					a[i][degree - j] = Math.pow(points[i][0], j);
				}
				i++;
			}

			A = new BlockRealMatrix(a);

			b = new BlockRealMatrix(B);
		
	}
}
