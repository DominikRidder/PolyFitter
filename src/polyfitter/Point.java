package polyfitter;

public class Point extends java.awt.Point{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	float x;
	
	float y;
	
	public Point(){
		x=0;
		y=0;
	}
	
	public Point(double x, double y){
		this.x = (float) x;
		this.y = (float) y;
	}
	
	public Point(float x,float y){
		this.x = x;
		this.y = y;
	}
}
