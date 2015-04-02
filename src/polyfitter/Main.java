package polyfitter;

public class Main {

	public static void main(String agrs[]) {
		Polyfitter fitter = new Polyfitter();

		// setting algorithm and degree
		fitter.setAlgorithmLowestSquare();
		fitter.setDegree(2);
		
		// adding points
		fitter.addPoint(3, 1 );
		fitter.addPoint(10, 5);
		fitter.addPoint(20, 40);
		fitter.addPoint(30, 40);
		fitter.addPoint(40, 2);

		// performing the fit
		fitter.fit();
		
		// the toString() method is a nice way to look up the fit
		System.out.println(fitter);
		
		// this function makes the fitting visible
		fitter.plot();
	}

}
