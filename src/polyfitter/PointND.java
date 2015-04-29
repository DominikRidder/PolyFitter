package polyfitter;

import java.util.ArrayList;
import java.util.Collection;

public class PointND implements Point{
	
	private ArrayList<Double> elements = new ArrayList<Double>();
	
	public PointND(){}
	
	public PointND(double[] d){
		for (double num: d){
			elements.add(num);
		}
	}
	
	public PointND(Collection<Double> d){
		for (double num: d){
			elements.add(num);
		}
	}
	
	public int getDimension() {
		return elements.size();
	}

	public double getElementbyNumber(int i) {
		return elements.get(i);
	}

	public void addElement(double d){
		elements.add(d);
	}
	
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append("(");
		for (double d: elements){
			s.append(d+"/");
		}
		s.append(")");
		return s.toString();
	}
}
