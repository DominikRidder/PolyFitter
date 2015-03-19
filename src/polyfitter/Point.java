package polyfitter;

public class Point{
	private float [] xyz;
	
	public float getX(){
		if (xyz.length <1){
			System.out.println("x is not set.");
			return 0;
		}
		return xyz[0];
	}
	
	public float getY(){
		if (xyz.length <2){
			System.out.println("y is not set.");
			return 0;
		}
		return xyz[1];
	}
	
	public float getZ(){
		if (xyz.length <3){
			System.out.println("z is not set.");
			return 0;
		}
		return xyz[2];
	}
	
	public Point(){
		xyz = new float[0];
	}
	
	public Point(double x){
		xyz = new float[1];
		xyz[0] = (float) x;
	}
	
	public Point(float x){
		xyz = new float[1];
		xyz[0] = x;
	}
	public Point(double x, double y){
		xyz = new float[2];
		xyz[0] = (float) x;
		xyz[1] = (float) y;
	}
	
	public Point(float x,float y){
		xyz = new float[2];
		xyz[0] = x;
		xyz[1] = y;
	}
	public Point(double x, double y, double z){
		xyz = new float[3];
		xyz[0] = (float) x;
		xyz[1] = (float) y;
		xyz[2] = (float) z;
	}
	
	public Point(float x,float y, float z){
		xyz = new float[3];
		xyz[0] = x;
		xyz[1] = y;
		xyz[2] = z;
	}
}
