package polyfitter;

import java.util.Vector;

public class Main {

	public static void main(String agrs[]) {
		Polyfitter fitter = new Polyfitter();
		
		// setting algorithm and degree
		fitter.setAlgorithmLowestSquare();
		fitter.setDegree(30);
		
		// adding points
		fitter.addPoint(create4DPoint(10, 20, 30, 40));
		fitter.addPoint(create4DPoint(5, 40, 14, 50));
		fitter.addPoint(create4DPoint(20, 8, 50, 60));
		fitter.addPoint(create4DPoint(40, 60, 3, 70));
		fitter.addPoint(create4DPoint(25, 10, 48, 80));
		fitter.addPoint(create4DPoint(0, 0, 0, 90));

		// choosing x y and z
		Vector<Integer> choice = new Vector<Integer>();
		choice.add(0);
		choice.add(1);
//		choice.add(2);

		fitter.optimazationOptions.makeOptimazationVisible();
		fitter.optimazationOptions.chooseBestDegree();
		
		// performing the fit
		fitter.fit(choice);
		
		// the toString() method is a nice way to look up the fit
		System.out.println(fitter);
		
		// this function makes the fitting visible
//		fitter.plot();
		fitter.plot(true);
	}
	
	public static PointND create4DPoint(double x1, double x2, double x3, double x4){
		double a[] = {x1, x2, x3, x4};
		return new PointND(a);
	}
}
