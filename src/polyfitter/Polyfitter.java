package polyfitter;

import fitterAlgorithm.FitterAlgorithm;
import fitterAlgorithm.PolynomialLowestSquare;
import polyfitter.Point;
import functions.Function;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.gui.Plot;
import ij.plugin.SurfacePlotter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagehandling.KeyMap;
import imagehandling.Volume;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class Polyfitter {

	/**
	 * f is the function
	 */
	private Function f;

	/**
	 * algo is the Algorithm, that will perform the Polynomialfitting
	 */
	private FitterAlgorithm algo;

	/**
	 * set of points, that the polynom should nearly fit
	 */
	private ArrayList<Point> pointcloud;

	/**
	 * to check if new Points match up to the old one
	 */
	private int dimension;

	/**
	 * with the vector v, the user can choose what is x,y and z
	 */
	private Vector<Integer> v;

	/**
	 * Optimazation command list
	 */
	private ArrayList<Optimize> optimize = new ArrayList<Optimize>();

	/**
	 * The optimazationOption can be used to find a smaller Problem.
	 */
	public final Optimazation optimazationOptions = new Optimazation(optimize);

	/**
	 * Default Constructer (doing nothing)
	 */
	public Polyfitter() {
	}

	/**
	 * The given file should contain points in the form: x1,...,k1 for max 3
	 * values per point. Every point should have his own line.
	 */
	public Polyfitter(String path) {
		setPoints(path);
	}

	public ArrayList<Point> getPointcloud() {
		return (ArrayList<Point>) pointcloud.clone();
	}

	/**
	 * Use this method, if you need a value of the polynom to a given Point p.
	 * 
	 * @param p
	 * @return
	 */
	public double getValue(Point p) {
		switch (p.getDimension()) {
		case 1:
			return getValue(p.getElementbyNumber(0));
		case 2:
			return getValue3d(p.getElementbyNumber(0), p.getElementbyNumber(1));
		default:
			return 0;
		}
	}

	public void setFunction(Function f) {
		this.f = f;
	}

	/**
	 * Private method to get the Value of an 2D function.
	 * 
	 * @param d
	 * @return
	 */
	private double getValue(double d) {
		f = algo.getFunction();
		return f.f(d);
	}

	/**
	 * Returning the Function of the fit.
	 * 
	 * @return
	 */
	public Function getFunction() {
		if (pointcloud == null) {
			System.out.println("At first you have to set some Points.");
			return null;
		}
		defaultAlgorithm("You have to set an algorithm to get a Polynom.");
		double[] poly;

		f = algo.getFunction();
		if (f == null) {
			System.out
					.println("fit() have to be called first. Performing fit methode to set a polynom...");
			f = fit();
		}
		return f;
	}

	/**
	 * This Method can be used to lookup how the polynom is builded. For
	 * example: "1.4324x^2 + 483x^1 ...."
	 * 
	 * @return the String Representation of the Polynom
	 */
	public String getPolynomRepresentation() {
		if (algo == null) {
			defaultAlgorithm("There have to be a Algorithm to get a Polynomial reprensantation.");
		}
		if (dimension == 3) {
			return getPolynomRepresentation3d();
		}
		String str = "";
		f = algo.getFunction();
		// if (poly.length == 0) {
		// System.out
		// .println("You have to perform the fit method before u can get a Polynom.");
		// return "<<not set>>";
		// }
		// for (int i = 0; i < poly.length; i++) {
		// if (i != 0 && poly[i] >= 0) {
		// str += "+ ";
		// } else if (poly[i] < 0) {
		// str += "- ";
		// }
		// str += Math.abs(poly[i]) + "x^" + (poly.length - i - 1) + " ";
		// }
		return f.toString();
	}

	/**
	 * This method returning the 3dPolynomRepresentation as a String.
	 * 
	 * @return
	 */
	private String getPolynomRepresentation3d() {
		String str = "";
		int j = 0;
		f = algo.getFunction();
		// for (int grenze = algo.getDegree(); grenze >= 0; grenze--) {
		// for (int i = 0; i <= grenze; i++) {
		// if (poly[j] >= 0) {
		// str += "+ ";
		// } else if (poly[j] < 0) {
		// str += "- ";
		// }
		// str += Math.abs(poly[j++]) + "x^" + (grenze - i) + "y^" + i
		// + " ";
		// }
		// }

		return str;
	}

	/**
	 * This method returning the Value of a 3d function to 2 given values x and
	 * y.
	 * 
	 * @param d
	 * @param t
	 * @return
	 */
	private double getValue3d(double d, double t) {
		double value = 0;
		int j = 0;
		f = algo.getFunction();
		// for (int grenze = algo.getDegree(); grenze >= 0; grenze--) {
		// for (int i = 0; i <= grenze; i++) {
		// value += poly[j++] * Math.pow(d, (grenze - i)) * Math.pow(t, i);
		// }
		// }
		return f.f(d);
	}

	/**
	 * Returning the absolute Sum of the y(or z) evaluation.
	 * 
	 * @return
	 */
	public double getProblem() {
		return algo.getProblem();
	}

	/**
	 * This method is used, to set up the Algorithm, which should find a
	 * function to the given data.
	 * 
	 * @param algo
	 */
	public void setAlgorithm(FitterAlgorithm algo) {
		if (this.algo != null) {
			System.out
					.println("The algorithm was already set. The result of the fitting by the old algorithm is lost now.");
			int degree = this.algo.getDegree();
			this.algo = algo;
		} else {
			this.algo = algo;
		}
	}

	// /**
	// * This Method setting the used Algorithm to the LowestSquare Algorithm.
	// * Instead of this method, you could also write fitter.setAlgorithm(new
	// * LowestSquare());
	// */
	// public void setAlgorithmLowestSquare() {
	// setAlgorithm(new PolynomialLowestSquare());
	// }

	public void setPoints(String path) {
		pointcloud = null;
		dimension = 0;
		addPoints(path);
	}

	public void setPoints(float[][] pointcloud) {
		this.pointcloud = null;
		dimension = 0;
		addPoints(pointcloud);
	}

	public void setDegree(int i) {
		defaultAlgorithm("There must be a Algortihmen to set a degree.");
		algo.setDegree(i);
	}

	public void addPoints(float[][] pointcloud) {
		if (this.pointcloud == null) {
			this.pointcloud = new ArrayList<Point>();
		}
		int dim = pointcloud[0].length;
		for (float[] a : pointcloud) {
			switch (dim) {
			case 1:
				this.pointcloud.add(new Point1D(a[0]));
				break;
			case 2:
				this.pointcloud.add(new Point2D(a[0], a[1]));
				break;
			case 3:
				this.pointcloud.add(new Point3D(a[0], a[1], a[2]));
				break;
			default:
				break;
			}
		}
	}

	public void addPoints(String path) {
		readPoints(path);
	}

	public void addPoint(double x) {
		if (!dimensionequal(1)) {
			System.out
					.println("addPoint failed. You can not mix points from diffent dimensions.");
			return;
		}
		float[][] pointstoadd = { { (float) x } };
		addPoints(pointstoadd);
	}

	public void addPoint(double x, double y) {
		if (!dimensionequal(2)) {
			System.out
					.println("addPoint failed. You can not mix points from diffent dimensions.");
			return;
		}
		float[][] pointstoadd = { { (float) x, (float) y } };
		addPoints(pointstoadd);
	}

	public void addPoint(double x, double y, double z) {
		if (!dimensionequal(3)) {
			System.out
					.println("addPoint failed. You can not mix points from diffent dimensions.");
			return;
		}
		float[][] pointstoadd = { { (float) x, (float) y, (float) z } };
		addPoints(pointstoadd);
	}

	/**
	 * This Method removing all points from the pointcloud, with the method
	 * "arraylist.clear".
	 */
	public void removePoints() {
		if (pointcloud != null) {
			pointcloud.clear();
		}
	}

	/**
	 * This Method performing the fit.
	 * 
	 * @return thepolynom as an array
	 */
	public Function fit() {
		if (pointcloud == null) {
			System.out.println("Fitting failed. There are no Points to fit.");
			return null;
		}
		defaultAlgorithm("There must be a algorithm to perform the fit.");
		if (dimension == 1 && algo.getDegree() != 0) {
			System.out
					.println("Setting degree = 0, because the fit would not make sense with higher degree right here.");
			algo.setDegree(0);
		}
		return useOptimasation(pointcloud);
	}

	/**
	 * With the Vector v, you can choose which elements of the Points will be
	 * used to perform the fit.
	 * 
	 * @param v
	 * @return
	 */
	public Function fit(Vector<Integer> v) {
		if (pointcloud == null) {
			System.out.println("Fitting failed. There are no Points to fit.");
			return null;
		}
		defaultAlgorithm("There must be a algorithm to perform the fit.");
		if (dimension == 1 && algo.getDegree() != 0) {
			System.out
					.println("Setting degree = 0, because the fit would not make sense with higher degree right here.");
			algo.setDegree(0);
		}

		this.v = v;

		return fithelp();
	}

	/**
	 * A Little Helpfull method to fit to a given Vector.
	 * 
	 * @return
	 */
	private Function fithelp() {
		Vector<Integer> copyv = v;
		ArrayList<Point> copyp = pointcloud;
		int copyd = dimension;

		dimension = v.size();
		pointcloud = getPointsToFit();
		v = null;

		f = this.fit();

		v = copyv;
		while (copyp.size() > pointcloud.size()) {
			copyp.remove(copyp.size() - 1);
		}
		pointcloud = copyp;
		dimension = copyd;

		return f;
	}

	/**
	 * This Method perform the OptimazationOptions.
	 * 
	 * @param pointcloud
	 * @return
	 */
	private Function useOptimasation(ArrayList<Point> pointcloud) {
		boolean ausgabe = false;
		for (Optimize o : optimize) {
			switch (o.command) {
			case Optimize.SEARCH_DEGREE:
				searchbetterdegree(o.arg, ausgabe, o.epsilon);
				break;
			case Optimize.OUTPUT:
				ausgabe = o.arg == 1 ? true : false;
				break;
			case Optimize.REMOVE_POINTS:
				searchbetterPoints(o.arg, ausgabe, o.epsilon);
				break;
			}
		}
		return algo.fit(pointcloud, f);
	}

	/**
	 * This Method removes the last point of the pointcloud over and over again,
	 * as long the problem of the fit getting smaller or only 2 Points left or
	 * the problem is smaller than the given prob.
	 * 
	 * @param arg
	 * @param ausgabe
	 * @param prob
	 */
	private void searchbetterPoints(int arg, boolean ausgabe, double prob) {
		if (ausgabe) {
			System.out.println("Starting searchbetterPoints...");
		}
		int startsize = pointcloud.size();
		while (pointcloud.size() > 2) {
			algo.fit(pointcloud, f);
			double problem1 = algo.getProblem();
			if (problem1 < prob) {
				if (ausgabe) {
					System.out
							.println("SearchbetterPoints terminated, because of the given problem("
									+ prob + ")");
				}
				break;
			}
			Point p = pointcloud.get(pointcloud.size() - 1);
			pointcloud.remove(pointcloud.size() - 1);
			algo.fit(pointcloud, f);
			double problem2 = algo.getProblem();
			if (problem2 < problem1) {
				if (ausgabe) {
					System.out.println("\tRemoving last Point ("
							+ pointcloud.size() + " Points left)");
				}
				continue;
			}
			pointcloud.add(p);
			if (ausgabe) {
				System.out
						.println("SearchbetterPoints terminated, because it cant find a smaller problem.");
			}
			break;
		}
		System.out.println("Removed " + (startsize - pointcloud.size())
				+ " Point(s).");
	}

	/**
	 * This Method trys to find a degree for which the Problem of a fit is lower
	 * than it is at the moment.
	 * 
	 * @param arg
	 * @param ausgabe
	 * @param prob
	 */
	private void searchbetterdegree(int arg, boolean ausgabe, double prob) {
		if (ausgabe) {
			System.out.println("Starting searchbetterDegree...");
		}
		int stepsize = arg;
		int degree = algo.getDegree();
		int maxUP = degree + stepsize;
		int maxdown = degree - stepsize;
		algo.fit(pointcloud, f);
		double problem1 = algo.getProblem();

		while (true) {
			if (problem1 < prob) {
				break;
			}
			if (degree - stepsize >= 0) {
				algo.setDegree(degree - stepsize);
				algo.fit(pointcloud, f);
				double problem2 = algo.getProblem();
				if (problem2 < problem1 && maxdown <= (degree - stepsize)) {
					degree -= stepsize;
					problem1 = problem2;
					if (ausgabe) {
						System.out.println("\tReducing degree from "
								+ (degree + stepsize) + " to " + degree);
					}
					continue;
				}
			}
			algo.setDegree(degree + stepsize);
			algo.fit(pointcloud, f);
			double problem2 = algo.getProblem();
			if (problem2 < problem1 && (degree + stepsize) <= maxUP) {
				degree += stepsize;
				problem1 = problem2;
				if (ausgabe) {
					System.out.println("\tIncreasing degree from "
							+ (degree - stepsize) + " to " + degree);
				}
				continue;
			}
			if (stepsize > 1) {
				stepsize /= 2;
				continue;
			}
			break;
		}
		if (ausgabe) {
			System.out.println("SearchbetterDegree teminated. Result degree = "
					+ degree + "\nChanged degree by: "
					+ Math.abs((maxUP - arg) - degree) + ".");
		}
		algo.setDegree(degree);
	}

	/**
	 * Returning a new Pointcloud, which only inhabitate the choosen elements,
	 * of the given Vector v.
	 * 
	 * @return
	 */
	private ArrayList<Point> getPointsToFit() {
		Integer[] a = new Integer[0];
		ArrayList<Point> tofit = new ArrayList<Point>();
		for (Point p : pointcloud) {
			PointND pnd = new PointND();
			for (Integer att : v.toArray(a)) {
				pnd.addElement(p.getElementbyNumber(att));
			}
			tofit.add(pnd);
		}
		return tofit;
	}

	public void plot() {
		if (v != null) {
			plothelp(true);
			return;
		}
		switch (dimension) {
		case 1:
			plot1D();
			break;
		case 2:
			plot2D();
			break;
		case 3:
			plot3D(true);
			break;
		}
	}

	/**
	 * This Method changing the pointcloud to the choosen slice(by a Vector) and
	 * performing the plot. Afterwards the old pointcloud getting restored.
	 * 
	 * @param b
	 */
	private void plothelp(boolean b) {
		Vector<Integer> copyv = v;
		ArrayList<Point> copyp = pointcloud;
		int copyd = dimension;

		dimension = v.size();
		pointcloud = getPointsToFit();
		v = null;

		plot(b);

		v = copyv;
		pointcloud = copyp;
		dimension = copyd;
	}

	/**
	 * The toString method is a nice way to look up the Polyfitter. It contains
	 * the used Algorithm, the PolynomialReprasentation, a table of values and
	 * the problem of the fit.
	 */
	public String toString() {

		if (v != null) {
			return toStringhelp();
		}

		String str = "Algorithm: ";

		if (algo == null) {
			str += "<not set>";
		} else {
			str += algo.getClass().getName();
		}
		str += "\n";

		str += "Polynom: " + getPolynomRepresentation() + "\n";

		switch (dimension) {
		case 1:
			str += "\tx\t|\tp(X)";
			break;
		case 2:
			str += "\tx\t|\ty\t|\tp(x)";
			break;
		case 3:
			str += "\tx\t|\ty\t|\tz\t|\tp(x)";
			str += "\n";
			str += String3d();
			return str;
		default:
			break;
		}
		if (pointcloud != null) {
			str += "\n";
			for (Point point : pointcloud) {
				for (int j = 0; j < pointcloud.get(0).getDimension(); j++) {
					str += "\t" + point.getElementbyNumber(j) + "\t|";
				}
				str += "\t" + getValue(point.getElementbyNumber(0)) + "\n";
			}
		} else {
			str += "No Points are given\n";
		}
		str += "Problem: ";
		if (algo.getProblem() < 0) {
			str += "<<not set>>";
		} else {
			str += algo.getProblem();
		}
		return str;
	}

	/**
	 * This Method is a help to get a String to a given Vector.
	 * 
	 * @return
	 */
	private String toStringhelp() {
		Vector<Integer> copyv = v;
		ArrayList<Point> copyp = pointcloud;
		int copyd = dimension;

		dimension = v.size();
		pointcloud = getPointsToFit();
		v = null;

		String str = this.toString();

		v = copyv;
		pointcloud = copyp;
		dimension = copyd;

		return str;
	}

	/**
	 * This method setting up the table for the 3d fit.
	 * 
	 * @return
	 */
	private String String3d() {
		String str = "";
		for (Point pointcloud1 : pointcloud) {
			for (int j = 0; j < pointcloud.get(0).getDimension(); j++) {
				str += "\t" + pointcloud1.getElementbyNumber(j) + "\t|";
			}
			str += "\t"
					+ getValue3d(pointcloud1.getElementbyNumber(0),
							pointcloud1.getElementbyNumber(1)) + "\n";
		}
		str += "Problem: ";
		if (algo.getProblem() < 0) {
			str += "<<not set>>";
		} else {
			str += algo.getProblem();
		}
		return str;
	}

	/**
	 * This Method is helpfull, to check if a point fits to the dimension, given
	 * by the other Points.
	 * 
	 * @param i
	 * @return
	 */
	private boolean dimensionequal(int i) {
		if (dimension == 0) {
			dimension = i;
		}
		if (dimension != i) {
			return false;
		}
		return true;
	}

	/**
	 * I guess this method needs an Update.
	 * 
	 * @param path
	 */
	private void readPoints(String path) {
		ArrayList<float[]> points = new ArrayList<float[]>();
		try (Scanner sc = new Scanner(new FileReader(path))) {

			while (sc.hasNextLine()) {
				String str[] = sc.nextLine().split(",");
				float[] a = new float[str.length];
				int i = 0;

				for (String s : str) {
					a[i++] = Float.parseFloat(s);
				}
				points.add(a);
			}
		} catch (IOException e) {
		}
		if (pointcloud == null) {
			pointcloud = new ArrayList<Point>();
		}
		for (float[] a : points) {
			pointcloud.add(floatToPoint(a));
		}
		dimensionequal(pointcloud.get(0).getDimension());
	}

	/**
	 * Method to create Points to given float numbers. If the array.length of
	 * the given number is higher than 3, the returned Point is an instance of
	 * PointND.
	 * 
	 * @param a
	 * @return
	 */
	private static Point floatToPoint(float[] a) {
		Point p = null;
		switch (a.length) {
		case 1:
			p = new Point1D(a[0]);
			break;
		case 2:
			p = new Point2D(a[0], a[1]);
			break;
		case 3:
			p = new Point3D(a[0], a[1], a[2]);
			break;
		case 4:
			p = new Point4D(a[0], a[1], a[2], a[3]);
			break;
		default:
			p = new PointND();
			break;
		}

		return p;
	}

	/**
	 * Method to setting a difault Algorithm, if the user forgott, to set one.
	 * 
	 * @param str
	 */
	private void defaultAlgorithm(String str) {
		if (algo == null) {
			System.out.print(str);
			System.out
					.println(" Setting Default algorithm: LowestSquare (degree 2).");
			algo = new PolynomialLowestSquare(2);
		}
	}

	/**
	 * Method to Plot a 1 Dimensional function.
	 */
	private void plot1D() {
		int xmin = 0;
		int xmax = 0;
		double ymin = -0.01;
		double ymax = 0.01;

		Plot p = new Plot("PolyFitter", "X", "");
		p.setFrameSize(1000, 100);
		double[] x = new double[pointcloud.size()];
		double[] y = new double[pointcloud.size()];
		int i = 0;
		for (Point a : pointcloud) {
			x[i] = a.getElementbyNumber(0);
			if (x[i] >= xmax) {
				xmax = (int) x[i] + 2;
			} else if (x[i] <= xmin) {
				xmin = (int) x[i] - 1;
			}
			y[i] = 0;
			i++;
		}
		p.setLimits(xmin, xmax, ymin, ymax);
		p.addPoints(x, y, Plot.X);

		y = new double[1];
		y[0] = 0;
		x = new double[1];
		// x[0] = algo.getFunction();

		p.addPoints(x, y, Plot.CIRCLE);

		p.draw();
		p.show();
	}

	/**
	 * Method to Plot a 2 Dimensional function.
	 */
	private void plot2D() {
		int xmin = 0;
		int xmax = 0;
		int ymin = 0;
		int ymax = 0;
		double dx = 0.1;
		Plot p = new Plot("PolyFitter", "X", "Y");
		p.setFrameSize(1000, 1000);
		double[] x = new double[pointcloud.size()];
		double[] y = new double[pointcloud.size()];
		int i = 0;
		for (Point a : pointcloud) {
			x[i] = a.getElementbyNumber(0);
			if (x[i] + 2 >= xmax) {
				xmax = (int) x[i] + 2;
			}
			if (x[i] - 1 <= xmin) {
				xmin = (int) x[i] - 1;
			}
			y[i] = a.getElementbyNumber(1);
			if (y[i] + 2 >= ymax) {
				ymax = (int) y[i] + 2;
			}
			if (y[i] - 1 <= ymin) {
				ymin = (int) y[i] - 1;
			}
			i++;
		}
		for (double a = xmin; a + dx < xmax; a += dx) {
			if (getValue(a + dx) + 2 > ymax) {
				ymax = (int) getValue(a + dx) + 2;
			}
			if (getValue(a + dx) - 1 < ymin) {
				ymin = (int) getValue(a + dx) - 1;
			}
		}
		p.setLimits(xmin, xmax, ymin, ymax);
		p.addPoints(x, y, Plot.X);
		for (double a = xmin; a + dx < xmax; a += dx) {
			p.drawLine(a, getValue(a), a + dx, getValue(a + dx));
		}
		p.draw();
		p.show();
	}

	/**
	 * Method to Plot a 3 dimensional function.
	 * 
	 * @param d3
	 */
	private void plot3D(boolean d3) {
		SurfacePlotter sp = new SurfacePlotter();

		BufferedImage im = new BufferedImage(500, 500,
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster ra = im.getRaster();

		double[] d = { 0., 0., 0. };

		double mult = 1;
		double max = 0;
		double min = 100000000;

		double minx = 100000;
		double miny = 100000;
		double maxx = 1;
		double maxy = 1;
		for (Point p : pointcloud) {
			if (p.getElementbyNumber(0) < minx) {
				minx = p.getElementbyNumber(0);
			}
			if (p.getElementbyNumber(1) < miny) {
				miny = p.getElementbyNumber(1);
			}
			if (p.getElementbyNumber(0) > maxx) {
				maxx = p.getElementbyNumber(0);
			}
			if (p.getElementbyNumber(1) > maxy) {
				maxy = p.getElementbyNumber(1);
			}
		}
		if (minx == 0) {
			minx = 0.1;
		}
		if (miny == 0) {
			miny = 0.1;
		}

		for (double j = minx; j < 500 + minx; j++) {
			for (double i = miny; i < 500 + miny; i++) {
				double e = getValue3d(i * maxy / 500, j * maxx / 500);
				if (e < min) {
					min = e;
				}
			}
		}

		min = -min;

		for (double j = minx; j < 500 + minx; j++) {
			for (double i = miny; i < 500 + miny; i++) {
				double e = getValue3d(i * maxy / 500, j * maxx / 500) + min;
				if (e > max) {
					max = e;
				}
			}
		}

		mult = 255 / max;

		for (double j = minx; j < 500 + minx; j++) {
			for (double i = miny; i < 500 + miny; i++) {
				d[0] = (getValue3d(i * maxy / 500, j * maxx / 500) + min)
						* mult;
				ra.setPixel((int) (i - miny), (int) (j - minx), d);
			}
		}

		ImageProcessor ip = new ByteProcessor(im);

		ImagePlus imgplus = new ImagePlus("2d data", ip);
		ImageWindow imgw = new ImageWindow(imgplus);
		ImageWindow.centerNextImage();
		JTextField jt = new JTextField(
				"x=<<not set>>/ y=<<not set>>/ z=<<not set>>");
		jt.setEditable(false);
		Window w = ImageWindow.getWindows()[0];
		w.setSize(new Dimension((int) w.getSize().getWidth(), (int) w.getSize()
				.getHeight() + 50));
		w.setMaximumSize(w.getSize());
		w.setMinimumSize(w.getSize());
		jt.setSize(new Dimension(500, 500));
		w.add(jt);

		if (d3 == true) {
			WindowManager.addWindow(imgw);
			sp.run("");
		}

		java.awt.Point p = null;
		while (w.isActive()) {
			try {
				if (p != imgw.getMousePosition()) {
					jt.setSize(jt.getPreferredSize());
					p = imgw.getMousePosition();
					Dimension d1 = imgw.getSize();
					double x = ((p.getX() - (d1.getWidth() - 500) / 2));
					double y = ((p.getY() - (d1.getHeight() - 487) / 2));
					if (x < 0) {
						x = 0;
					}
					if (y < 0) {
						y = 0;
					}
					if (x > 500) {
						x = 500;
					}
					if (y > 500) {
						y = 500;
					}
					x = (x + minx) * maxx / 500;
					y = (y + miny) * maxy / 500;
					String str = "x = " + x + "/ y = " + y + "/ z = "
							+ getValue3d(x, y);
					if (!jt.getText().equals(str)) {
						jt.setText(str);
					}
				}
				Thread.sleep(500);
			} catch (NullPointerException | InterruptedException e) {

			}
		}

	}

	public void addPoint(Point point) {
		if (point.getDimension() == 0) {
			System.out
					.println("You cannot put an empty point into the pointcloud.");
			return;
		}
		if (pointcloud == null) {
			dimension = point.getDimension();
			pointcloud = new ArrayList<Point>();
		}
		pointcloud.add(point);
	}

	public void plot(boolean plot3d) {
		if (v != null) {
			plothelp(plot3d);
			return;
		}
		switch (dimension) {
		case 1:
			plot1D();
			break;
		case 2:
			plot2D();
			break;
		case 3:
			plot3D(plot3d);
			break;
		}
	}

	public class Optimazation {
		private ArrayList<Optimize> arg = new ArrayList<>();

		public Optimazation(ArrayList<Optimize> listener) {
			arg = listener;
		}

		public void canRemoveLastPoint(int maxRemoveable, double problem) {
			arg.add(new Optimize(Optimize.REMOVE_POINTS, maxRemoveable, problem));
		}

		public void searchBetterDegree(int maxDegreeChange, double problem) {
			if (maxDegreeChange <= 0) {
				System.out
						.println("Optimazation.searchBetterDegree not acepted, because of not guilty argument.");
				return;
			}
			arg.add(new Optimize(Optimize.SEARCH_DEGREE, maxDegreeChange));
		}

		public void setOptimizationOutPut(boolean b) {
			arg.add(new Optimize(Optimize.OUTPUT, b == true ? 1 : 0));
		}
	}

	private class Optimize {
		static final int OUTPUT = 0, REMOVE_POINTS = 1, SEARCH_DEGREE = 2;
		int command;
		int arg;
		double epsilon;

		public Optimize(int c, int a) {
			command = c;
			arg = a;
		}

		public Optimize(int c, int a, double ep) {
			command = c;
			arg = a;
			epsilon = ep;
		}
	}

	public static void fitVolume(String path, boolean reversedGrayScaling) {
		Volume vol = new Volume(path);
		// int x = 100;
		// int y = 100;
		// int z = 1;

		int[] arr = new int[1];

		ArrayList<Point> toFit = new ArrayList<Point>();
		ArrayList<BufferedImage> data = vol.getData();
		String str_echo_numbers = vol.getSlice(vol.size() - 1).getAttribute(
				KeyMap.KEY_ECHO_NUMBERS_S);
		int width = data.get(0).getWidth();
		int height = data.get(0).getHeight();
		int echo_numbers = Integer.parseInt(str_echo_numbers);
		int perEcho = vol.size() / echo_numbers;
		int maxprob = 1;

		int counter = 0;

		double starttime = System.currentTimeMillis() / 1000;

		Polyfitter fitter = new Polyfitter();
		fitter.setAlgorithm(new PolynomialLowestSquare(1));

		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		ArrayList<BufferedImage> buffimg = new ArrayList<BufferedImage>();

		int[] probrasta = new int[width * height];

		for (int z = 0; z < echo_numbers; z++) {
			BufferedImage probimg = new BufferedImage(width, height,
					BufferedImage.TYPE_BYTE_GRAY);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					toFit.clear();
					for (int i = 0; i < echo_numbers; i++) {
						toFit.add(new Point2D(i, data.get(i * perEcho + z)
								.getRaster().getPixel(x, y, arr)[0]));
					}

					fitter.removePoints();
					for (Point p : toFit) {
						fitter.addPoint(p);
					}

					fitter.fit();
					int prob = (int) fitter.getProblem();

					probrasta[x + width * y] = prob;
					if (prob > maxprob) {
						maxprob = prob;
					}
					counter++;
				}
			}

			WritableRaster r = probimg.getRaster();

			for (int i = 0; i < width * height; i++) {
				probrasta[i] = probrasta[i] * 255 / maxprob;
				if (reversedGrayScaling){
					probrasta[i] = 255 - probrasta[i];
				}
			}

			r.setPixels(0, 0, width, height, probrasta);

			probimg.setData(r);

			buffimg.add(probimg);

		}
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		frame.getContentPane().add(new JLabel(new ImageIcon(image)));
		JSlider slide = new JSlider(JSlider.HORIZONTAL, 0, buffimg.size() - 1,
				0);
		JTextField tf = new JTextField("0");
		tf.setMaximumSize(new Dimension(50,50));
		JPanel jp = new JPanel(new GridLayout(2, 1));
		jp.add(tf);
		jp.add(slide);
		jp.setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
		jp.setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
		frame.getContentPane().add(jp);
		frame.setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
		frame.setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
		frame.getContentPane().setMaximumSize(
				new Dimension(image.getWidth(), image.getHeight()));
		frame.getContentPane().setMinimumSize(
				new Dimension(image.getWidth(), image.getHeight()));
		frame.setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
		frame.setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
		frame.pack();
		frame.setVisible(true);
		frame.repaint();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		double endtime = System.currentTimeMillis() / 1000;

		System.out.println("Time: " + (endtime - starttime) + ".sek");
		System.out.println("Highest problem: " + maxprob);
		System.out.println("Number of fittings: " + counter);
		int actual = -1;
		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (actual != slide.getValue()) {
				actual = slide.getValue();
				tf.setText("" + actual);
				image.getGraphics().drawImage(
						buffimg.get(actual).getScaledInstance(image.getWidth(),
								image.getHeight(), BufferedImage.SCALE_FAST),
						0, 0, null);
				frame.repaint();
			}
			try{
				int test = Integer.parseInt(tf.getText());
			if (test != actual && test>=0 && test<=slide.getMaximum()) {
				actual = Integer.parseInt(tf.getText());
				slide.setValue(actual);
				image.getGraphics().drawImage(
						buffimg.get(actual).getScaledInstance(image.getWidth(),
								image.getHeight(), BufferedImage.SCALE_FAST),
						0, 0, null);
				frame.repaint();
			}
			}catch(NumberFormatException e){
				
			}
		}
	}
}
