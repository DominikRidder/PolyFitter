package polyfitter;

/**
 * Point class, which contains 3 double elements.
 *
 */
public class Point3D implements Point{
	private double x;
	
	private double y;
	
	private double z;
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getZ(){
		return z;
	}
	
	public Point3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getDimension() {
		return 3;
	}

	public double getElementbyNumber(int i) {
		switch(i){
		case 0: return x;
		case 1: return y;
		case 2: return z;
		default:throw new RuntimeException("");
		}
	}
}
