package org.vkedco.opencv.haar;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.vkedco.wavelets.haar.TwoDHaar;

import java.util.ArrayList;

public class ImageManipulator {

	/**
	 * @param args
	 */

	static final Scalar COLOR_GREEN = new Scalar(0, 255, 0);
	static final Scalar COLOR_RED = new Scalar(0, 0, 255);
	static final Scalar COLOR_BLACK = new Scalar(0, 0, 0);
	static String HOME = "";
	static final String IMG_PATH = "C:\\资料备份\\图片\\";

	static {
		HOME = System.getProperty("user.home");
		HOME = "C:\\git\\DianmiAndroidFun\\app\\src\\main";
		System.load(HOME + "/jniLibs/armeabi/libopencv_java.so");
	}

	public static void main(String[] args) {
		
		int rectSize = 1;
		Mat mat = Highgui.imread(IMG_PATH + "background-cover.jpg");
		int centerX = 0;
		int centerY = 0;
		//TwoDMatHaar.displayMat2(mat);
		double[][] pixels = TwoDMatHaar.get2DPixelArrayFromMat(mat);
		TwoDHaar.displaySample(pixels, pixels[0].length, 0);
		//Mat checkMat = TwoDMatHaar.getMatFrom2DPixelArray(mat, pixels);
		//Highgui.imwrite(IMG_PATH + "img65_test.jpg", checkMat);
		//TwoDHaar.displaySample(pixels, pixels[0].length, 0);
		ArrayList<double[][]> haar_transform = TwoDMatHaar.orderedFastHaarWaveletTransformForNumIters(mat, 5, 4);
		TwoDHaar.displayOrderedHaarTransform(haar_transform);
			
		/*
		mat.convertTo(mat, CvType.CV_64FC3);
		System.out.println("Test");
		int size = (int) (mat.total() * mat.channels());
		double[] temp = new double[size];
		mat.get(0, 0, temp);
		int i = 0;
		for(int r = 0; r < mat.rows(); r++) {
			System.out.print("Row " + r + ": ");
			for(int c = 0; c < mat.rows(); c++) {
				System.out.print(temp[i++]+" ");
			}
			System.out.println();
		}
		*/
		
		
	}



}
