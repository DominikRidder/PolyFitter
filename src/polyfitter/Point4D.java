package polyfitter;

public class Point4D implements Point{

	private double x,y,z,s;
	
	public int getDimension() {
		return 4;
	}

	public double getElementbyNumber(int i) {
		switch(i){
		case 0: return x;
		case 1: return y;
		case 2: return z;
		case 3: return s;
		default: throw new RuntimeException("");
		}
	}

}
