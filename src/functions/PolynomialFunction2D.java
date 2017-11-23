package functions;

import java.text.DecimalFormat;

public class PolynomialFunction2D implements Function{

	double[] polynom;
	
	public PolynomialFunction2D(double[] polynomial){
		polynom = new double[polynomial.length];
		for (int i=0; i< polynomial.length; i++){
			polynom[i] = polynomial[i];
		}
	}
	
	public PolynomialFunction2D(int degree){
		polynom = new double[degree+1];
		for (int i=0; i<=degree; i++){
			polynom[i] = 0;
		}
	}
	
	public double f(double x) {
		double erg = 0;
		for (int i=0; i<polynom.length; i++){
			erg+= polynom[i]*Math.pow(x, polynom.length-1-i);
		}
		return erg;
	}
	
	
	public String toString(){
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuilder str = new StringBuilder();
		for (int i=0; i<polynom.length; i++){
			str.append(df.format(polynom[i])+"x^"+(polynom.length-1-i)+" ");
			if (i!= polynom.length-1){
				str.append("+");
			}
		}
		return str.toString().replace("+-", "-");
	}
}
