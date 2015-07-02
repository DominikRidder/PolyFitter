package polyfitter;

import fitterAlgorithm.PolynomialLowestSquare;
import imagehandling.KeyMap;
import imagehandling.Volume;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class VolumeFitter implements Runnable{
	
	private static int echo_numbers = 0;
	private static int perEcho = 0;
	private static int counter = 0;
	private static int width = 0;
	private static int height = 0;
	private static ArrayList<BufferedImage> buffimg;
	private static boolean reversedGrayScaling = false;
	private static ArrayList<BufferedImage> data;
	private static boolean finished = false;
	private static int finishc = 0;
	
	public static void fitVolume(String path, boolean reversedGrayScaling) {
		Volume vol = new Volume(path);

		VolumeFitter.reversedGrayScaling = reversedGrayScaling;
		data = vol.getData();
		String str_echo_numbers = vol.getSlice(vol.size() - 1).getAttribute(
				KeyMap.KEY_ECHO_NUMBERS_S).replace(" ", "");

		// width of the images
		width = data.get(0).getWidth();
		// height of the images
		height = data.get(0).getHeight();
		// Number of echo sequences
		echo_numbers = Integer.parseInt(str_echo_numbers);
		// Number of Images per echo sequence
		perEcho = vol.size() / echo_numbers;
		double starttime = System.currentTimeMillis() / 1000;

		// the programm for the fitting
		Polyfitter fitter = new Polyfitter();
		fitter.setAlgorithm(new PolynomialLowestSquare(1));

		// Just the window, to display the fit
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		buffimg = new ArrayList<BufferedImage>(perEcho);
		
		for (int i=0; i< perEcho; i++){
			buffimg.add(null);
		}
		
		new Thread(new VolumeFitter()).start();
		
		while (!finished){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Frame stuff
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		frame.getContentPane().add(new JLabel(new ImageIcon(image)));
		JSlider slide = new JSlider(JSlider.HORIZONTAL, 0, buffimg.size() - 1,
				0);
		JTextField tf = new JTextField("0");
		tf.setMaximumSize(new Dimension(50, 50));
		JPanel jp = new JPanel(new GridLayout(2, 1));
		jp.add(tf);
		jp.add(slide);
		jp.setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
		jp.setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
		frame.getContentPane().add(jp);
		frame.pack();
		frame.setVisible(true);
		frame.repaint();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		double endtime = System.currentTimeMillis() / 1000;
		
		//output and frame updates
		System.out.println("Time: " + (endtime - starttime) + " sek");
		System.out.println("Number of fittings: " + (perEcho*width*height));
		int actual = -1;
		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (actual != slide.getValue()) {
				actual = slide.getValue();
				tf.setText("" + actual);
				image.getGraphics().drawImage(
						buffimg.get(actual).getScaledInstance(image.getWidth(),
								image.getHeight(), BufferedImage.SCALE_FAST),
						0, 0, null);
				frame.repaint();
			}
			try {
				int test = Integer.parseInt(tf.getText());
				if (test != actual && test >= 0 && test <= slide.getMaximum()) {
					actual = Integer.parseInt(tf.getText());
					slide.setValue(actual);
					image.getGraphics().drawImage(
							buffimg.get(actual).getScaledInstance(
									image.getWidth(), image.getHeight(),
									BufferedImage.SCALE_FAST), 0, 0, null);
					frame.repaint();
				}
			} catch (NumberFormatException e) {

			}
		}
	}

	public void run() {
		int z = counter++;
		if (counter < perEcho){
			new Thread(this).start();
		}
		// highest fitting problem
		int maxprob = 1;
		int[] arr = new int[1];

		Polyfitter fitter = new Polyfitter();
		fitter.setAlgorithm(new PolynomialLowestSquare(1));
		
		int[] probrasta = new int[width * height];
		
		BufferedImage probimg = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				fitter.removePoints();
				for (int i = 0; i < echo_numbers; i++) {
					fitter.addPoint(new Point2D(i, data.get(i * perEcho + z)
							.getRaster().getPixel(x, y, arr)[0]));
				}

				fitter.fit();
				int prob = (int) fitter.getProblem();

				probrasta[x + width * y] = prob;
				if (prob > maxprob) {
					maxprob = prob;
				}
			}
		}

		WritableRaster r = probimg.getRaster();

		for (int i = 0; i < width * height; i++) {
			probrasta[i] = probrasta[i] * 255 / maxprob;
			if (reversedGrayScaling) {
				probrasta[i] = 255 - probrasta[i];
			}
		}

		r.setPixels(0, 0, width, height, probrasta);

		buffimg.set(z, probimg);
		
		if (++finishc == echo_numbers-1){
			finished = true;
		}
	}
}
