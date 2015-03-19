package polyfitter;

public class Main {

	public static void main(String agrs[]) {
		Polyfitter fitter = new Polyfitter();

		// setting algorithm and degree
		fitter.setAlgorithmLowestSquare();
		fitter.setDegree(2);
		
		// adding points
		fitter.addPoint(0, 3, 2);
		fitter.addPoint(1, 9, 5);
		fitter.addPoint(2, 5, 8);

		// performing the fit
		fitter.fit();
		// the toString() method is a nice way to look up the fit
		System.out.println(fitter);
		// this function makes the fitting visible
		System.out.println(fitter.getPolynom());
		fitter.plot();
	}

}
