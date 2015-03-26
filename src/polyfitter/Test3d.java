package polyfitter;


import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.plugin.SurfacePlotter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
//I tried to find out how i can plot a surface with imagej, in this class.
public class Test3d {

	public static void main(String[] agrs) {
		SurfacePlotter sp = new SurfacePlotter();
		ImageStack is = new ImageStack();

		is.drawSphere(32, 12, 65, 32);

		BufferedImage im = new BufferedImage(1000, 1000,
				BufferedImage.TYPE_BYTE_GRAY);

		WritableRaster ra = im.getRaster();

		double[] d = { 0., 100., 100. , 1000};

		double mult = 1;
		double max = 0;
		
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 1000; j++) {
				if (funktion(i, j) > max){
					max = funktion(i, j);
				}
			}
		}
		
		mult = 255/max;
		
		System.out.println("mult="+mult);
		System.out.println("max="+max);
		
		if (mult > 1){
			mult = 1;
		}
		
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 1000; j++) {
				d[0] = funktion(i,j)*mult;
				ra.setPixel(i, j, d);
			}
		}
		
		
		ImageProcessor ip = new ByteProcessor(im);
		
		
		ImagePlus imgplus = new ImagePlus("test" , ip);
		ImageWindow imgw = new ImageWindow(imgplus);
		ImageWindow.centerNextImage();
		
		
		WindowManager.addWindow(imgw);
		System.out.println(WindowManager.getImageCount());
		System.out.println(WindowManager.getCurrentImage());
		sp.run("");
		sp.makeSurfacePlot(ip);
	}
	
	public static double funktion(int i, int j){
		return 1000-j;
	}
}
