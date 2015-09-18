package polyfitter;

import fitterAlgorithm.PolynomialLowestSquare;
import ij.gui.Plot;
import imagehandling.KeyMap;
import imagehandling.Volume;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class VolumeFitter{

	Polyfitter fitter;
	
	JComponent c;
	
	ArrayList<BufferedImage> data;
	
	int width,height,echo_numbers,perEcho;
	
	public BufferedImage getbla(String path, Point roi, int slice, boolean alsolog){
		Volume vol = new Volume(path);

		data = vol.getData();
		String str_echo_numbers = vol.getSlice(vol.size() - 1).getAttribute(
				KeyMap.KEY_ECHO_NUMBERS_S).replace(" ", "");

//		// width of the images
//		width = data.get(0).getWidth();
//		// height of the images
//		height = data.get(0).getHeight();
		// Number of echo sequences
		echo_numbers = Integer.parseInt(str_echo_numbers);
		// Number of Images per echo sequence
		perEcho = vol.size() / echo_numbers;

		// the programm for the fitting
		Polyfitter fitter = new Polyfitter();
		fitter.setAlgorithm(new PolynomialLowestSquare(echo_numbers-1));

		// Just the window, to display the fit
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		ArrayList<BufferedImage>buffimg = new ArrayList<BufferedImage>(echo_numbers);
		
		for (int e=0; e<echo_numbers; e++){
			buffimg.add(data.get(slice+perEcho*e));
		}
		
		int iArray[] = new int[4];
		int itest[] = new int[100];
		for (int i=0; i<buffimg.size(); i++){
			BufferedImage img = buffimg.get(i);
			iArray = img.getRaster().getPixel((int) roi.getElementbyNumber(0), (int) roi.getElementbyNumber(1), itest);
			fitter.addPoint(i, iArray[0], Math.log10(iArray[0])*10);
		}
		
		return fitter.plotVolume(alsolog);
	}

}
