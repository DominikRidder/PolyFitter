package polyfitter;

/**
 * This Class only contains a single method, which is usefull, to create the
 * minimal Point class, to a given array of elements.
 */
public class PointHelp {

	private PointHelp() {
		// this class only contains a static method
	}

	/**
	 * This method creating the minimal Point class, to a given array of double elements.
	 * @param elements
	 * @return
	 */
	public static Point createPoint(double[] elements) {
		switch (elements.length) {
		case 0:
			System.out.println("A point need at least 1 element!");
			return null;
		case 1:
			return new Point1D(elements[0]);
		case 2:
			return new Point2D(elements[0], elements[1]);
		case 3:
			return new Point3D(elements[0], elements[1], elements[2]);
		case 4:
			return new Point4D(elements[0], elements[1], elements[2],
					elements[3]);
		default:
			return new PointND(elements);
		}
	}
}
