package polyfitter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.omg.Messaging.SyncScopeHelper;

import fitterAlgorithm.PolynomialLowestSquare;
import functions.PolynomialFunction2D;
import imagehandling.KeyMap;
import imagehandling.Volume;

public class MainVol {

	public static void main(String[] args) {
		Volume vol = new Volume(
				"/opt/dridder_local/TestDicoms/TestSort6/15.05.19-17:00:13-DST-1.3.12.2.1107.5.2.32.35135/003_vg_gre_m0w__w3dfl_ipat_68");
		// int x = 100;
		// int y = 100;
//		int z = 30;
		int[] arr = new int[1];

		ArrayList<Point> toFit = new ArrayList<Point>();
		ArrayList<BufferedImage> data = vol.getData();
		String str_echo_numbers = vol.getSlice(vol.size() - 1).getAttribute(
				KeyMap.KEY_ECHO_NUMBERS_S);
		int width = data.get(0).getWidth();
		int height = data.get(0).getHeight();
		int echo_numbers = Integer.parseInt(str_echo_numbers);
		int perEcho = vol.size() / echo_numbers;
		double maxprob = 0;
		ArrayList<Point> worstPoints = new ArrayList<Point>();
		double minprob = 1000000;
		int counter = 0;
		
		double starttime = System.currentTimeMillis()/1000;
		Polyfitter fitter = new Polyfitter();
		fitter.setAlgorithm(new PolynomialLowestSquare(1));
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < perEcho; z++) {
					toFit.clear();
					for (int i = 0; i < echo_numbers; i++) {
						toFit.add(new Point2D(i, data.get(i * perEcho + z)
								.getRaster().getPixel(x, y, arr)[0]));
					}

					fitter.removePoints();
					for (Point p : toFit) {
						fitter.addPoint(p);
					}

					fitter.fit();
					double prob = fitter.getProblem();
					if (prob > maxprob){
						worstPoints = fitter.getPointcloud();
						maxprob = prob;
					}else if (prob < minprob){
						minprob = prob;
					}
					counter++;
				}
			}
		}
		double endtime = System.currentTimeMillis()/1000;
		
		System.out.println("Time: "+(endtime-starttime)+".sek");
		System.out.println("minprob = "+minprob);
		System.out.println("maxprob = "+maxprob);
		System.out.println("counter = " + counter);
		
		fitter.removePoints();
		for (Point p: worstPoints){
			fitter.addPoint(p);
		}
		fitter.fit();
		System.out.println(fitter);
		fitter.plot();
	}
}


