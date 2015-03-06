package polyfitter;

import java.util.ArrayList;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * This class is an example for an implementation of a FitterAlgorithm.
 */
public class LowestSquare implements FitterAlgorithm {

	private RealMatrix A;

	private RealMatrix b;

	private RealMatrix polynom;

	private int degree;

	private boolean degreeset;

	// I dont know, how the iterations fits into the algorithm. They are not
	// used yet.
	// private int iterations;

	public LowestSquare() {
		degreeset = false;
	}

	public void setMaxIterations(int i) {
	}

	public void setDegree(int d) {
		if (d < 0) {
			System.out
					.println("A negative degree don't make sense. Setting degree = 0.");
			d = 0;
		}
		degree = d;
		degreeset = true;
	}

	public double getProblem() {
		if (polynom == null){
			System.out.println("You have to perform the fit method before you can get a problem.");
                        return -1;
                }
                double prob = 0;
		RealMatrix C = A.multiply(polynom).subtract(b);
		RealVector D = C.getColumnVector(0);

                for(int i=0; i<D.getDimension();i++){
                prob += Math.abs(D.getEntry(i));
                }
		return prob;
	}

	/**
	 * Uses the formula: (AT * A)^-1 * (AT*b). This way the Problem x become
	 * minimal.
	 */
	public double[] fit(float[][] points) {
		if (points[0].length == 3){
			if (degree != 1){
			System.out.println("This FitterAlgorithm cant fit 3d Points yet, except for degree = 1.");
			System.exit(1);
			}
			return fit3d(points);
		}
		int numberofpoints = points.length;
		if (!degreeset) {
			System.out.println("The degree of the polynom is not set. Setting highest possible degree (d = "
							+ (numberofpoints - 1) + ").");
			degree = numberofpoints-1;
		}
		if (degree >= numberofpoints) {
			System.out
					.println("The degree is to high for this algorithm. Setting highest possible degree (d = "
							+ (numberofpoints - 1) + ").");
			degree = numberofpoints - 1;
		}

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
		RealMatrix AT = A.transpose();
		b = new BlockRealMatrix(B);

		RealMatrix C = AT.multiply(A);

		C = new LUDecomposition(C).getSolver().getInverse();

		RealMatrix D = AT.multiply(b);

		C = C.multiply(D);

		polynom = C;

		return polynom.getColumn(0);
	}
	
	public double[] fit3d(float[][] points) {
		int numberofpoints = points.length;
		if (!degreeset) {
			System.out.println("The degree of the polynom is not set. Setting highest possible degree (d = "
							+ (numberofpoints - 1) + ").");
			degree = numberofpoints-1;
		}
		if (degree >= numberofpoints) {
			System.out
					.println("The degree is to high for this algorithm. Setting highest possible degree (d = "
							+ (numberofpoints - 1) + ").");
			degree = numberofpoints - 1;
		}

		double[][] a = new double[numberofpoints][degree + 2];
		double[][] B = new double[numberofpoints][1];
		
		int deg = degree;
		for (int i=0; i<points.length; i++){
			B[i][0] = points[i][2];
			for (int j = deg ; j>=0 ; j--){
				a[i][deg-j] = Math.pow(points[i][0], j) * Math.pow(points[i][1], deg-j); 
			}
			a[i][a[0].length-1] = 1;
			degree--;
		}

		A = new BlockRealMatrix(a);
		RealMatrix AT = A.transpose();
		b = new BlockRealMatrix(B);

		RealMatrix C = AT.multiply(A);

		C = new LUDecomposition(C).getSolver().getInverse();

		RealMatrix D = AT.multiply(b);

		C = C.multiply(D);

		polynom = C;

		return polynom.getColumn(0);
	}
	
	public double[] getPolynom() {
		if (polynom == null){
			return new double[0];
		}
		return polynom.getColumn(0);
	}

	public int getDegree() {
		return degree;
	}

	public double[] fit(ArrayList<float[]> pointcloud) {
		float[][] a= new float[pointcloud.size()][pointcloud.get(0).length];
		int index=0;
		for (float[] b : pointcloud){
			a[index++] = b;
		}
		return fit(a);
	}
}
