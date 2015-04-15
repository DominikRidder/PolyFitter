package polyfitter;

import java.util.ArrayList;
import java.util.Vector;

public interface FitterAlgorithm {
	int getDegree();
	double[] getPolynom();
	double getProblem();
	void setMaxIterations(int i);
	void setDegree(int d);
	double[] fit(ArrayList<Point> pointcloud);
}
