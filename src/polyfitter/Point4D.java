package polyfitter;

/**
 * Point class, which contains 4 double elements.
 *
 */
public class Point4D implements Point{

	private double x1,x2,x3,x4;
	
	public Point4D(double x1, double x2, double x3,double x4){
		this.x1=x1;
		this.x2=x2;
		this.x3=x3;
		this.x4=x4;
	}
	
	public int getDimension() {
		return 4;
	}

	public double getElementbyNumber(int i) {
		switch(i){
		case 0: return x1;
		case 1: return x2;
		case 2: return x3;
		case 3: return x4;
		default: throw new RuntimeException("");
		}
	}

}
