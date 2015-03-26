package polyfitter;

import java.util.ArrayList;

public class PointND implements Point{

	private ArrayList<Double> elements;
	
	public int getDimension() {
		return elements.size();
	}

	public double getElementbyNumber(int i) {
		return elements.get(i);
	}

}
