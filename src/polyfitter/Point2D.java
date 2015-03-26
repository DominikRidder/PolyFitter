package polyfitter;

public class Point2D implements Point{
	private double x;
	
	private double y;
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public Point2D(double x, double y){
		this.x = x;
		this.y = y;
	}

	public int getDimension() {
		return 2;
	}

	public double getElementbyNumber(int i) {
		switch(i){
		case 1: return x;
		case 2: return y;
		default:return (Double) null;
		}
	}
}
