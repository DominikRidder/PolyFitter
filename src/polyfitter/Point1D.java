package polyfitter;

/**
 * Point class, which contains 1 double elements.
 *
 */
public class Point1D implements Point{
	private double x;
	
	public double getX(){
		return x;
	}
	
	public Point1D(double x){
		this.x = x;
	}

	public int getDimension() {
		return 1;
	}

	public double getElementbyNumber(int i) {
		switch(i){
		case 0: return x;
		default:throw new RuntimeException("");
		}
	}
}
