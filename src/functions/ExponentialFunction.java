package functions;

public class ExponentialFunction implements Function{

	private double a,b;
	
	public double f(double x) {
		return a*Math.pow(Math.E, b*x);
	}
	
	public void setA(double a){
		this.a = a;
	}

	public void setB(double b) {
		this.b = b;
	}

}
