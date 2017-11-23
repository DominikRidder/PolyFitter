package fitterAlgorithm;

import java.util.ArrayList;

import functions.Function;

public interface FitterAlgorithm {
	int getDegree();

	Function getFunction();

	double getProblem();

	void setMaxIterations(int i);

	void setDegree(int d);

	Function fit(ArrayList<float[]> pointcloud, Function f);
}
