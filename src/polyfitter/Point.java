package polyfitter;

public interface Point {
	/**
	 * Returns the Number of elements the Point contains
	 */
	public int getDimension();

	/**
	 * Returns the specific element, indicated by the Integer i, where i should be 0<=i<Point.getDimension()
	 */
	public double getElementbyNumber(int i); 
}
