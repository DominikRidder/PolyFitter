package polyfitter;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.gui.Plot;
import ij.plugin.SurfacePlotter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.plaf.synth.SynthSpinnerUI;
import javax.vecmath.Point3d;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class Polyfitter {

	/**
	 * algo is the Algorithm, that will perform the Polynomialfitting
	 */
	private FitterAlgorithm algo;

	/**
	 * set of points, that the polynom should nearly fit
	 */
	private ArrayList<Point> pointcloud;

	private int dimension;

	private Vector<Integer> v;

	private ArrayList<Optimize> optimize = new ArrayList<Optimize>();

	public final Optimazation optimazationOptions = new Optimazation(optimize);

	public Polyfitter() {
	}

	/**
	 * The given file should contain points in the form: x1,...,k1 for max 3
	 * values per point. Every point should have his own line.
	 */
	public Polyfitter(String path) {
		setPoints(path);
	}

	/**
	 * The Object in the ArrayList should be an instance of the class
	 * java.awt.Point or the class javax.vecmath.Point3d
	 *
	 * @param pointcloud
	 *            ; pointcloud.add(new Point()) or pointcloud.add(new Point3d())
	 */
	public Polyfitter(ArrayList<Object> pointcloud) {
		setPoints(pointcloud);
	}

	public Polyfitter(float[][] pointcloud) {
		setPoints(pointcloud);
	}

	public ArrayList<Point> getPointcloud() {
		return pointcloud;
	}

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

	private double getValue(double d) {
		double[] a = algo.getPolynom();
		double value = 0;
		for (int i = 0; i < a.length; i++) {
			value += a[a.length - 1 - i] * Math.pow(d, i);
		}
		return value;
	}

	public double[] getPolynom() {
		if (pointcloud == null) {
			System.out.println("At first you have to set some Points.");
			return new double[0];
		}
		defaultAlgorithm("You have to set an algorithm to get a Polynom.");
		double[] poly;
		try {
			poly = algo.getPolynom();
		} catch (NullPointerException e) {
			System.out
					.println("fit() have to be called first. Performing fit methode to set a polynom...");
			poly = fit();
		}
		return poly;
	}

	public String getPolynomRepresentation() {
		if (algo == null) {
			defaultAlgorithm("There have to be a Algorithm to get a Polynomial reprensantation.");
		}
		if (dimension == 3) {
			return getPolynomRepresentation3d();
		}
		String str = "";
		double poly[] = algo.getPolynom();
		if (poly.length == 0) {
			System.out
					.println("You have to perform the fit method before u can get a Polynom.");
			return "<<not set>>";
		}
		for (int i = 0; i < poly.length; i++) {
			if (i != 0 && poly[i] >= 0) {
				str += "+ ";
			} else if (poly[i] < 0) {
				str += "- ";
			}
			str += Math.abs(poly[i]) + "x^" + (poly.length - i - 1) + " ";
		}
		return str;
	}

	public String getPolynomRepresentation3d() {
		String str = "";
		int j = 0;
		double poly[] = algo.getPolynom();
		for (int grenze = algo.getDegree(); grenze >= 0; grenze--) {
			for (int i = 0; i <= grenze; i++) {
				if (poly[j] >= 0) {
					str += "+ ";
				} else if (poly[j] < 0) {
					str += "- ";
				}
				str += Math.abs(poly[j++]) + "x^" + (grenze - i) + "y^" + i
						+ " ";
			}
		}

		return str;
	}

	private double getValue3d(double d, double t) {
		double value = 0;
		int j = 0;
		double poly[] = algo.getPolynom();
		for (int grenze = algo.getDegree(); grenze >= 0; grenze--) {
			for (int i = 0; i <= grenze; i++) {
				value += poly[j++] * Math.pow(d, (grenze - i)) * Math.pow(t, i);
			}
		}
		return value;
	}

	public double getProblem() {
		return algo.getProblem();
	}

	public void setAlgorithm(FitterAlgorithm algo) {
		if (this.algo != null) {
			System.out
					.println("The algorithm was already set. The result of the fitting by the old algorithm is lost now.");
			int degree = this.algo.getDegree();
			this.algo = algo;
			this.algo.setDegree(degree);
		} else {
			this.algo = algo;
		}
	}

	public void setAlgorithmLowestSquare() {
		setAlgorithm(new LowestSquare());
	}

	public void setPoints(String path) {
		pointcloud = null;
		dimension = 0;
		addPoints(path);
	}

	public void setPoints(ArrayList<Object> pointcloud) {
		dimension = 0;
		this.pointcloud = null;
		addPoints(pointcloud);
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

	public void addPoints(ArrayList<Object> pointcloud) {
		float pointstoadd[][] = null;
		if (pointcloud.get(0).getClass().getName().equals("java.awt.Point")) {
			if (!dimensionequal(2)) {
				System.out
						.println("addPoints failed. You can not mix points from diffent dimensions.");
				return;
			}
			pointstoadd = new float[pointcloud.size()][2];
			for (int i = 0; i < pointcloud.size(); i++) {
				pointstoadd[i][0] = ((java.awt.Point) (pointcloud.get(i))).x;
				pointstoadd[i][1] = ((java.awt.Point) (pointcloud.get(i))).y;
			}
			addPoints(pointstoadd);
		} else if (pointcloud.get(0).getClass().getName()
				.equals("javax.vecmath.Point3d")) {
			if (!dimensionequal(3)) {
				System.out
						.println("addPoints failed. You can not mix points from diffent dimensions.");
				return;
			}
			pointstoadd = new float[pointcloud.size()][3];
			for (int i = 0; i < pointcloud.size(); i++) {
				pointstoadd[i][0] = (float) ((Point3d) pointcloud.get(i)).x;
				pointstoadd[i][1] = (float) ((Point3d) pointcloud.get(i)).y;
				pointstoadd[i][2] = (float) ((Point3d) pointcloud.get(i)).z;
			}
			addPoints(pointstoadd);
		} else {
			throw new RuntimeException("The class "
					+ pointcloud.get(0).getClass().getName()
					+ " is not supported");
		}
	}

	public void removePoints() {
		pointcloud.clear();
	}

	public double[] fit() {
		if (pointcloud == null) {
			System.out.println("Fitting failed. There are no Points to fit.");
			return new double[0];
		}
		defaultAlgorithm("There must be a algorithm to perform the fit.");
		if (dimension == 1 && algo.getDegree() != 0) {
			System.out
					.println("Setting degree = 0, because the fit would not make sense with higher degree right here.");
			algo.setDegree(0);
		}
		return useOptimasation(pointcloud);
	}

	public double[] fit(Vector<Integer> v) {
		if (pointcloud == null) {
			System.out.println("Fitting failed. There are no Points to fit.");
			return new double[0];
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

	private double[] fithelp() {
		Vector<Integer> copyv = v;
		ArrayList<Point> copyp = pointcloud;
		int copyd = dimension;

		dimension = v.size();
		pointcloud = getPointsToFit();
		v = null;

		double pol[] = this.fit();

		v = copyv;
		while (copyp.size() > pointcloud.size()){
			copyp.remove(copyp.size()-1);
		}
		pointcloud = copyp;
		dimension = copyd;

		return pol;
	}

	private double[] useOptimasation(ArrayList<Point> pointcloud) {
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
		return algo.fit(pointcloud);
	}

	private void searchbetterPoints(int arg, boolean ausgabe, double prob) {
		if (ausgabe) {
			System.out.println("Starting searchbetterPoints...");
		}
		int startsize = pointcloud.size();
		while (pointcloud.size() > 2) {
			algo.fit(pointcloud);
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
			algo.fit(pointcloud);
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

	private void searchbetterdegree(int arg, boolean ausgabe, double prob) {
		if (ausgabe) {
			System.out.println("Starting searchbetterDegree...");
		}
		int stepsize = arg;
		int degree = algo.getDegree();
		int maxUP = degree + stepsize;
		int maxdown = degree - stepsize;
		algo.fit(pointcloud);
		double problem1 = algo.getProblem();

		while (true) {
			if (problem1 < prob) {
				break;
			}
			if (degree - stepsize >= 0) {
				algo.setDegree(degree - stepsize);
				algo.fit(pointcloud);
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
			algo.fit(pointcloud);
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
			for (Point pointcloud1 : pointcloud) {
				for (int j = 0; j < pointcloud.get(0).getDimension(); j++) {
					str += "\t" + pointcloud1.getElementbyNumber(j) + "\t|";
				}
				str += "\t" + getValue(pointcloud1.getElementbyNumber(0))
						+ "\n";
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

	private boolean dimensionequal(int i) {
		if (dimension == 0) {
			dimension = i;
		}
		if (dimension != i) {
			return false;
		}
		return true;
	}

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
			p = new Point4D();
			break;
		default:
			p = new PointND();
			break;
		}

		return p;
	}

	private void defaultAlgorithm(String str) {
		if (algo == null) {
			System.out.print(str);
			System.out.println(" Setting Default algorithm: LowestSquare.");
			algo = new LowestSquare();
		}
	}

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
		x[0] = algo.getPolynom()[0];

		p.addPoints(x, y, Plot.CIRCLE);

		p.draw();
		p.show();
	}

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

	private void plot3D(boolean d3) {
		SurfacePlotter sp = new SurfacePlotter();

		BufferedImage im = new BufferedImage(500, 500,
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster ra = im.getRaster();

		double[] d = { 0., 0., 0. };

		double mult = 1;
		double max = 0;
		double min = 100000000;

		double minx=100000;
		double miny=100000;
		double maxx=1;
		double maxy=1;
		for (Point p: pointcloud){
			if (p.getElementbyNumber(0) < minx){
				minx = p.getElementbyNumber(0);
			}
			if (p.getElementbyNumber(1) < miny){
				miny = p.getElementbyNumber(1);
			}
			if (p.getElementbyNumber(0) > maxx){
				maxx = p.getElementbyNumber(0);
			}
			if (p.getElementbyNumber(1) > maxy){
				maxy = p.getElementbyNumber(1);
			}
		}
		if (minx == 0){
			minx = 0.1;
		}
		if (miny == 0){
			miny = 0.1;
		}
		
		for (double j = minx; j < 500+minx; j++) {
			for (double i = miny; i < 500+miny; i++) {
				double e = getValue3d(i * maxy/500, j * maxx/500);
				if (e < min) {
					min = e;
				}
			}
		}

		min = -min;

		for (double j = minx; j < 500+minx; j++) {
			for (double i = miny; i < 500+miny; i++) {
				double e = getValue3d(i * maxy/500, j * maxx/500) + min;
				if (e > max) {
					max = e;
				}
			}
		}

		mult = 255 / max;

		for (double j = minx; j < 500+minx; j++) {
			for (double i = miny; i < 500+miny; i++) {
				d[0] = (getValue3d(i * maxy/500, j * maxx/500) + min) * mult;
				ra.setPixel((int) (i-miny), (int) (j-minx), d);
			}
		}

		ImageProcessor ip = new ByteProcessor(im);

		ImagePlus imgplus = new ImagePlus("2d data", ip);
		ImageWindow imgw = new ImageWindow(imgplus);
		ImageWindow.centerNextImage();
		JTextField jt = new JTextField("x=<<not set>>/ y=<<not set>>/ z=<<not set>>");
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
					x = (x+minx)*maxx/500;
					y = (y+miny)*maxy/500;
					String str = "x = " + x + "/ y = " + y + "/ z = "
							+ getValue3d(x, y);
					if (! jt.getText().equals(str)){
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
}
