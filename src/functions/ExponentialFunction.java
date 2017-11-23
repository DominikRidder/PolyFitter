package functions;

import java.text.DecimalFormat;

public class ExponentialFunction implements Function{

	private double a,b,c;
	
	public double f(double x) {
		return a*Math.pow(Math.E, b*x)+c;
	}
	
	public void setA(double a){
		this.a = a;
	}

	public void setB(double b) {
		this.b = b;
	}

	public void setC(float c) {
		this.c = c;
		
	}
	
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		return (df.format(a)+"*e^("+df.format(b)+")+"+df.format(c)).replace("+-", "-");
	}

}
