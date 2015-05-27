package polyfitter;

import java.util.Vector;

public class Main {

	public static void main(String agrs[]) {
		Polyfitter fitter = new Polyfitter();
		
		// setting algorithm and degree
		fitter.setAlgorithmLowestSquare();
		fitter.setDegree(5);
		
		// adding points
		fitter.addPoint(create4DPoint(10, 20, 30, 40));
		fitter.addPoint(create4DPoint(5, 40, 14, 50));
		fitter.addPoint(create4DPoint(20, 8, 50, 60));
		fitter.addPoint(create4DPoint(40, 60, 3, 70));
		fitter.addPoint(create4DPoint(25, 10, 48, 80));
		fitter.addPoint(create4DPoint(0, 0, 0, 90));
//		fitter.addPoint(create4DPoint(0, -1000, 500, 90));

		// choosing x y and z
		Vector<Integer> choice = new Vector<Integer>();
		choice.add(1);
		choice.add(3);
//		choice.add(0);

		// the order of the Options matter
//		fitter.optimazationOptions.setOptimizationOutPut(true);
//		fitter.optimazationOptions.canRemoveLastPoint(100, 1000); // max removeable Points = 100 / break for problem <1000
//		fitter.optimazationOptions.setOptimizationOutPut(false);
//		fitter.optimazationOptions.searchBetterDegree(10,0); // max degree change = 10 / break for problem <0
		
		// performing the fit
		fitter.fit(choice);
		
		// the toString() method is a nice way to look up the fit
		System.out.println("\n"+fitter);
		
		// this function makes the fitting visible
		fitter.plot(true); // false -> only 2d plot/data
		
	}
	
	public static PointND create4DPoint(double x1, double x2, double x3, double x4){
		double a[] = {x1, x2, x3, x4};
		return new PointND(a);
	}
}
