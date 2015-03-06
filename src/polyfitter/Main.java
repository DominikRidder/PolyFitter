package polyfitter;

public class Main {

	public static void main(String agrs[]) {
		Polyfitter fitter = new Polyfitter();

		// setting algorithm and degree
		fitter.setAlgorithmLowestSquare();
		fitter.setDegree(2);

		// adding points
		fitter.addPoint(0, 0);
		fitter.addPoint(1, 1);
		fitter.addPoint(2, 5);
		fitter.addPoint(4, 14);
		fitter.addPoint(8, 60);
		fitter.addPoint(10, 105);

		// performing the fit
		fitter.fit();
		// the toString() method is a nice way to look up the fit
		System.out.println(fitter);
		// this function makes the fitting visible
		fitter.plot();
	}

}
