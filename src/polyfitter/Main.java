package polyfitter;

import fitterAlgorithm.PolynomialLowestSquare;

public class Main {

	public static void main(String agrs[]) {
		Polyfitter p = new Polyfitter();

		int nparams = 2;
		int pointspertask = 8;
		int tasks = 126 * 126 * 126;
		System.out.println("Number of tasks: " + tasks);
		System.out.println("pointcloud size: " + tasks * pointspertask);

		System.out.println("Calculating Points ...");
		double start = System.currentTimeMillis();

		float[][][] pointcloud = new float[tasks][pointspertask][nparams];
		for (int j = 0; j < tasks; j++) {
			for (int i = 0; i < pointspertask; i++) {
				pointcloud[j][i][0] = i + 1;
				pointcloud[j][i][1] = (float) Math.random() * 255;
			}
		}
		System.out.println("Done. It took me "
				+ (System.currentTimeMillis() - start) + " [ms]");

		p.setAlgorithm(new PolynomialLowestSquare(nparams));

		System.out.println("Fitting ...");
		start = System.currentTimeMillis();
		for (int j = 0; j < tasks; j++) {
			p.setPoints(pointcloud[j]);
		}
		System.out.println("Done. It took me "
				+ (System.currentTimeMillis() - start) + " [ms]");

	}
}
