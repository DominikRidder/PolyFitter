package polyfitter;

public class Point4D implements Point{

	private double x,y,z,s;
	
	public int getDimension() {
		return 4;
	}

	public double getElementbyNumber(int i) {
		switch(i){
		case 1: return x;
		case 2: return y;
		case 3: return z;
		case 4: return s;
		default:return (Double) null;
		}
	}

}
