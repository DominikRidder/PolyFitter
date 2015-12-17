package fitterAlgorithm;

import java.util.ArrayList;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import polyfitter.Point;
import functions.Function;
import functions.PolynomialFunction2D;

public class LRDecomposition implements FitterAlgorithm {

	private RealMatrix A;

	private RealMatrix b;

	private RealMatrix polynom;

	private RealMatrix R;

	private RealMatrix L;

	private int degree;

	// I dont know, how the iterations fits into the algorithm. They are not
	// used yet.
	// private int iterations;

	public LRDecomposition(int degree) {
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
		double[] d = polynom.getColumn(0);
		double[] rev = new double[d.length];
		for (int i=0; i<rev.length; i++){
			rev[rev.length-1-i] = d[i];
		}
		return new PolynomialFunction2D(rev);
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
		float[][] a = new float[pointcloud.size()][pointcloud.get(0)
				.length];
		int index = 0;
		for (float[] c : pointcloud) {
			a[index++] = c;
		}
		fit(a);

		return getFunction();
	}

	/**
	 * Uses the formula: x = (AT * A)^-1 * (AT*b). This way the Problem become
	 * minimal.
	 */
	public double[] fit(float[][] points) {

		setUpAandB(points);

		if (R == null) {
			R = new BlockRealMatrix(A.getData());
			int n = degree+1;
			L = new BlockRealMatrix(n, n);
			for (int i=0; i<n; i++){
				L.setEntry(i, i, 1);
			}

			// n-1 Iterationsschritte
			for (int i = 0; i < n - 1; i++) {
				// Zeilen der Restmatrix werden durchlaufen
				for (int k = i+1; k < n; k++) {
					// Berechnung von L
					L.setEntry(k, i, R.getEntry(k, i) / R.getEntry(i, i));// Achtung:
																			// vorher
																			// Prüfung
																			// auf
																			// Nullwerte
																			// notwendig
					// Spalten der Restmatrix werden durchlaufen
					for (int j = i; j < n; j++) {
						// Berechnung von R
						R.setEntry(k, j, R.getEntry(k, j) - L.getEntry(k, i)
								* R.getEntry(i, j));
					}
				}
			}
		}

		int n = degree+1;
		
		polynom = new BlockRealMatrix(n, 1);

		RealMatrix y = new BlockRealMatrix(n, 1);
		
		for (int i = 0; i<n; i++){
			double yi = 0;
			for (int k= 0; k<i; k++){
				yi += L.getEntry(i, k) * y.getEntry(k, 0); 
			}
			yi = (b.getEntry(i, 0)-yi)/L.getEntry(i, i);
			y.setEntry(i, 0, yi);
		}
		
		for (int i = n-1; i>=0; i--){
			double xi = 0;
			for (int k= 1; k<n; k++){
				xi += R.getEntry(i, k) * polynom.getEntry(k, 0); 
			}
			xi = (y.getEntry(i, 0)-xi)/R.getEntry(i, i);
			polynom.setEntry(i, 0, xi);
		}
//		printMatrix("A", A);
//		printMatrix("R", R);
//		printMatrix("L", L);
//		printMatrix("y", y);
//		printMatrix("polynom", polynom);
		return polynom.getColumn(0);
	}

	private void printMatrix(String name, RealMatrix brm){
		System.out.println(name+"{");
		for (int i=0; i<brm.getRowDimension(); i++){
			for (int j=0; j<brm.getColumnDimension(); j++){
				System.out.print(brm.getEntry(i, j)+ ", ");
			}
			System.out.println();
		}
		System.out.println("}");
	}
	
	private void setUpAandB(float[][] points) {
		int numberofpoints = points.length;
		if (points[0].length < 3) {
			double[][] a = new double[numberofpoints][degree + 1];
			double[][] B = new double[numberofpoints][1];
			int i = 0;
			for (int c = 0; c < numberofpoints; c++) {
				B[i][0] = points[i][points[0].length - 1];
				for (int j = degree; j >= 0; j--) {
					a[i][j] = Math.pow(points[i][0], j);
				}
				i++;
			}

			A = new BlockRealMatrix(a);

			b = new BlockRealMatrix(B);
		} else {
			int counter = 0;
			for (int x = degree; x >= 0; x--) {
				for (int y = degree; y >= 0; y--) {
					counter++;
				}
			}

			double[][] a = new double[numberofpoints][counter];
			double[][] B = new double[numberofpoints][1];

			int pos;

			for (int j = 0; j < numberofpoints; j++) {
				B[j][0] = points[j][2];
				pos = 0;
				for (int x = degree; x >= 0; x--) {
					for (int y = degree; y >= 0; y--) {
						a[j][pos++] = Math.pow(points[j][0], x)
								* Math.pow(points[j][1], y);
					}
				}
			}

			A = new BlockRealMatrix(a);

			b = new BlockRealMatrix(B);
		}

	}
}
