package polyfitter;

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
		case 1: return x;
		case 2: return y;
		case 3: return z;
		default:return (Double) null;
		}
	}
}