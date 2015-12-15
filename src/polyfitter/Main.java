package polyfitter;

import java.util.ArrayList;
import java.util.Collections;

import fitterAlgorithm.LowestSquare;
import fitterAlgorithm.PolynomialLowestSquare;

public class Main {

	public static void main(String agrs[]) {

		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i <= 6; i++) {
			for (int j = 1; j <= 6; j++) {
				list.add(i+j);
			}
		}
		Collections.sort(list);
		int k = 0;
		int counter = 0;
		for (int i = 0; i < list.size(); i++) {
			int next = list.get(i);
			if (k != next) {
				if (counter != 0) {
					System.out.println(counter + " mal die " + k);
				}
				k++;
				counter = 0;
				if (k < 70) {
					i--;
				}
			} else {
				counter++;
			}
		}
//		System.out.println(counter + " mal die " + k);
		// Polyfitter fitter = new Polyfitter();
		// int[] xvalues = {10,4,0,6,10};
		// int[] yvalues = {1,3,5,3,3};
		//
		// for (int i=0; i<xvalues.length; i++){
		// fitter.addPoint(xvalues[i], yvalues[i]);
		// System.out.println("x="+xvalues[i]+" liefert "+(-1./18*(double)xvalues[i]+10./3));
		// }
		// for (int i=0; i<xvalues.length; i++){
		// System.out.println("y="+yvalues[i]+" liefert "+(-1./2*(double)yvalues[i]+9));
		// }
		//
		// fitter.setAlgorithm(new PolynomialLowestSquare(1));
		//
		// fitter.fit();
		// fitter.plot();
		// System.out.println(fitter);

		// // setting algorithm
		// fitter.setAlgorithm(new PolynomialLowestSquare(2));
		// // or setting a function
		// // fitter.setFunction(new PolynomialFunction(2));
		//
		// // adding points
		// fitter.addPoint(new Point4D(10, 1, 30, 1));
		// fitter.addPoint(new Point4D(5, 2, 14, 4));
		// fitter.addPoint(new Point4D(20, 3, 50, 9));
		// fitter.addPoint(new Point4D(40, 4, 3, 16));
		// fitter.addPoint(new Point4D(25, 5, 48, 25));
		// fitter.addPoint(new Point4D(0, 0, 0, 0));
		// // fitter.addPoint(create4DPoint(0, -1000, 500, 90));
		//
		// // choosing x y and z
		// Vector<Integer> choice = new Vector<Integer>();
		// choice.add(1);
		// choice.add(3);
		// // choice.add(0);
		//
		// // the order of the Options matter
		// // fitter.optimazationOptions.setOptimizationOutPut(true);
		// // fitter.optimazationOptions.canRemoveLastPoint(100, 1000); // max
		// removeable Points = 100 / break for problem <1000
		// // fitter.optimazationOptions.setOptimizationOutPut(false);
		// // fitter.optimazationOptions.searchBetterDegree(10,0); // max degree
		// change = 10 / break for problem <0
		//
		// // performing the fit
		// fitter.fit(choice);
		//
		// // the toString() method is a nice way to look up the fit
		// System.out.println("\n"+fitter);
		//
		// // this function makes the fitting visible
		// fitter.plot(true); // false -> only 2d plot/data

	}
}
