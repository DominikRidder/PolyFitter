package polyfitter;

import java.util.ArrayList;

public interface FitterAlgorithm {
	int getDegree();
	double[] getPolynom();
	double getProblem();
	void setMaxIterations(int i);
	void setDegree(int d);
	double[] fit(ArrayList<Point> pointcloud);
}
