package polyfitter;

public class PointHelp {

	private PointHelp(){
		// this class only contains a static method
	}
	
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
