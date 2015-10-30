package fitterAlgorithm;

import java.util.ArrayList;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import polyfitter.Point;
import functions.ExponentialFunction;
import functions.Function;

/**
 * This Class solving LowestSquare problems to a giving function a + f(x).
 * 
 * @author dridder_local
 *
 */
public class LowestSquare implements FitterAlgorithm {

	private RealMatrix A;

	private RealMatrix b;

	private RealMatrix x;

	private ExponentialFunction givenFunction;

	private int degree;

	public LowestSquare(int degree){
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
		if (x == null) {
			System.out
					.println("You have to perform the fit method before you can get a problem.");
			return -1;
		}
		double prob = 0;
		RealMatrix C = A.multiply(x).subtract(b);
		RealVector D = C.getColumnVector(0);

		for (int i = 0; i < D.getDimension(); i++) {
			prob += Math.abs(D.getEntry(i));
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
	public Function fit(ArrayList<Point> pointcloud, Function f) {
		this.givenFunction = (ExponentialFunction) f;
		float[][] a = new float[pointcloud.size()][pointcloud.get(0)
				.getDimension()];
		int index = 0;
		for (Point c : pointcloud) {
			float[] b = new float[c.getDimension()];
			for (int i = 0; i < c.getDimension(); i++) {
				b[i] = (float) Math.log(c.getElementbyNumber(i));
			}
			a[index++] = b;
		}
		fit(a);

		return getFunction();
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
	
	/**
	 * Uses the formula: x = (AT * A)^-1 * (AT*b). This way the Problem become
	 * minimal.
	 */
	public double[] fit(float[][] points){
		
		setUpAandB(points);

		RealMatrix AT = A.transpose();
		
		RealMatrix C = AT.multiply(A);

		C = new QRDecomposition(C).getSolver().getInverse();
		
		RealMatrix D = AT.multiply(b);

		C = C.multiply(D);

		x = C;

//		printMatrix("A", A);
//		printMatrix("b", b);
//		printMatrix("x", x);
		
		double values[] = x.getColumn(0);
		givenFunction.setA(Math.pow(Math.E, values[1]));
		givenFunction.setB(values[0]);
		return x.getColumn(0);
	}
	
	private void setUpAandB(float[][] points) {
		int numberofpoints = points.length;
		if (points[0].length <3){
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
		}else{
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
