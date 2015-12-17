package fitterAlgorithm;

import java.net.StandardSocketOptions;
import java.util.ArrayList;

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
	public Function fit(ArrayList<float[]> pointcloud, Function f) {
		this.givenFunction = (ExponentialFunction) f;
		float[][] a = new float[pointcloud.size()][pointcloud.get(0)
				.length];
		int index = 0;
		for (float[] c : pointcloud) {
			a[index++] = c;
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
	public void fit(float[][] points){
		int n = points.length;
		
		/******* Sums **********/
		double sumx2y = 0; // sum((x^2) * y)
		double sumylny = 0; // sum(y*ln(y))
		double sumxy = 0; // sum(x*y)
		double sumxylny = 0; // sum(x*y*ln(y))
		double sumy = 0; // sum(y)
		
		for (int i=0; i<n; i++){
			float xi = points[i][0];
			float yi = points[i][1];
			
			sumx2y += xi*xi*yi;
			sumylny += yi*Math.log(yi);
			sumxy += xi*yi;
			sumxylny += xi*yi*Math.log(yi);
			sumy += yi;
		}
		/***********************/
//		
//		System.out.println("sumx2y = "+sumx2y);
//		System.out.println("sumylny = "+sumylny);
//		System.out.println("sumxy = "+sumxy);
//		System.out.println("sumxylny = "+sumxylny);
//		System.out.println("sumy = "+sumy);
		
		double a = (sumx2y*sumylny-sumxy*sumxylny)/(sumy*sumx2y-sumxy*sumxy);
		
		double B = (sumy*sumxylny-sumxy*sumylny)/(sumy*sumx2y-sumxy*sumxy);
		
		double A = Math.pow(Math.E, a);
		
		givenFunction.setA(A);
		givenFunction.setB(B);
	}

}
