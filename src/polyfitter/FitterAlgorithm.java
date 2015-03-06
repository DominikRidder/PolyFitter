package polyfitter;

import java.util.ArrayList;

public interface FitterAlgorithm {
	int getDegree();
	double[] getPolynom();
	void setMaxIterations(int i);
	void setDegree(int d);
	double[] fit(ArrayList<float[]> pointcloud);
	double getProblem();
}
