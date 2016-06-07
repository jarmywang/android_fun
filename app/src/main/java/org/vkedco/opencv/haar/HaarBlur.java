package org.vkedco.opencv.haar;

import org.opencv.core.Mat;
import org.vkedco.wavelets.haar.TwoDHaar;

import java.util.ArrayList;

public class HaarBlur {
	
	static double[][] mEMAP1 = null;
	static double[][] mEMAP2 = null;
	static double[][] mEMAP3 = null;
	
	static double[][] mEMAX1 = null;
	static double[][] mEMAX2 = null;
	static double[][] mEMAX3 = null;
	
	static int mNUM_EDGES = 0;
	static int mNUM_DIRAC_ASTEP = 0;
	static int mNUM_ROOF_GSTEP = 0;
	static int mNUM_BLURRED_ROOF_GSTEP = 0;
	
	static double mBLUR_EXTENT = 0;
	static boolean mIsBlurred = false;
	
	static void computeEmaps(Mat mat) {
		double[][] pixels = TwoDMatHaar.get2DPixelArrayFromMat(mat);
		int n = pixels[0].length;
		ArrayList<double[][]> orderedRslts = 
				TwoDHaar.orderedFastHaarWaveletTransformForNumIters(pixels, (int)TwoDMatHaar.log(n, 2), 1);
		pixels = null;
		double[][] AVR1 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
		double[][] HOR1 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
		double[][] VER1 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
		double[][] DIG1 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
	
		n = AVR1[0].length;
		orderedRslts = TwoDHaar.orderedFastHaarWaveletTransformForNumIters(AVR1, (int)TwoDMatHaar.log(n, 2), 1);
		
		double[][] AVR2 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
		double[][] HOR2 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
		double[][] VER2 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
		double[][] DIG2 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
		
		n = AVR2[0].length;
		orderedRslts = TwoDHaar.orderedFastHaarWaveletTransformForNumIters(AVR2, (int)TwoDMatHaar.log(n, 2), 1);
		
		//double[][] AVR3 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
		double[][] HOR3 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
		double[][] VER3 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
		double[][] DIG3 = TwoDHaar.getAveragesFromOrderedList(orderedRslts);
		
		HaarBlur.sqrt2DArray(HOR1);
		HaarBlur.sqrt2DArray(VER1);
		HaarBlur.sqrt2DArray(DIG1);
		double[][] tmp = HaarBlur.add2DArrays(HOR1, VER1);
		mEMAP3 = HaarBlur.add2DArrays(tmp, DIG1);
		HaarBlur.sqrt2DArray(mEMAP3);
		
		HaarBlur.sqrt2DArray(HOR2);
		HaarBlur.sqrt2DArray(VER2);
		HaarBlur.sqrt2DArray(DIG2);
		tmp = HaarBlur.add2DArrays(HOR2, VER2);
		mEMAP2 = HaarBlur.add2DArrays(tmp, DIG2);
		HaarBlur.sqrt2DArray(mEMAP2);
		
		HaarBlur.sqrt2DArray(HOR3);
		HaarBlur.sqrt2DArray(VER3);
		HaarBlur.sqrt2DArray(DIG3);
		tmp = HaarBlur.add2DArrays(HOR3, VER3);
		mEMAP1 = HaarBlur.add2DArrays(tmp, DIG3);
		HaarBlur.sqrt2DArray(mEMAP1);
		System.out.println("mEMAP1's size: " + "dimx = " + mEMAP1.length + " dimy = " + mEMAP1[0].length);
		System.out.println("mEMAP2's size: " + "dimx = " + mEMAP2.length + " dimy = " + mEMAP2[0].length);
		System.out.println("mEMAP3's size: " + "dimx = " + mEMAP3.length + " dimy = " + mEMAP3[0].length);
	}
	
	static void computeEmax(double[][] emap, int emax_num, int n) {
		final int dimx = emap.length;
		final int dimy = emap[0].length;
		System.out.println("computeEmax: dimx = " + dimx + " dimy = " + dimy + " emax_num = " + emax_num);
		double[][] emax = new double[dimx/n][dimy/n];
		int curr_row = 0;
		int curr_col = 0;
		double maxval = 0;
		for(int row = 0; row < dimx - n; row += n) {
			curr_col = 0;
			for(int col = 0; col < dimy - n; col += n) {
				maxval = 0;
				for(int max_row = row; max_row < row + n; max_row++) {
					for(int max_col = col; max_col < col + n; max_col++) {
						if ( emap[max_row][max_col] > maxval )
							maxval = emap[max_row][max_col];
					}
				}
				emax[curr_row][curr_col] = maxval;
				curr_col += 1;
			}
			curr_row += 1;
		}
		
		switch ( emax_num ) {
		case 1: mEMAX1 = emax; return;
		case 2: mEMAX2 = emax; return;
		case 3: mEMAX3 = emax; return;
		}
	}
	
	static void computeEmaxes() {
		computeEmax(mEMAP1, 1, 2);
		computeEmax(mEMAP2, 2, 4);
		computeEmax(mEMAP3, 3, 8);
	}
	
	public static void estimateImageBlur(Mat mat, double haar_thresh, double min_zero_thresh) {
		computeEmaps(mat);
		computeEmaxes();
		int dimx = mEMAX1.length; int dimy = mEMAX1[0].length;
		System.out.println("estimateImageBlur: dimx1 = " + dimx + " dimy = " + dimy);
		System.out.println("estimateImageBlur: dimx2 = " + mEMAX2.length + " dimy = " + mEMAX2[0].length);
		System.out.println("estimateImageBlur: dimx3 = " + mEMAX3.length + " dimy = " + mEMAX3[0].length);
		computeEdgeStats(dimx, dimy, haar_thresh);
		double PER = (mNUM_DIRAC_ASTEP*1.0)/mNUM_EDGES;
		mBLUR_EXTENT = 0;
		if ( mNUM_ROOF_GSTEP > 0 ) {
			mBLUR_EXTENT = (mNUM_BLURRED_ROOF_GSTEP*1.0)/mNUM_ROOF_GSTEP;
		}
		mIsBlurred = true;
		if ( PER > min_zero_thresh ) {
			mIsBlurred = false;
		}
	}
	
	static double[] computeEdgeStats(int dimx, int dimy, double thresh) {
		mNUM_EDGES = 0;
		mNUM_DIRAC_ASTEP = 0;
		mNUM_ROOF_GSTEP = 0;
		mNUM_BLURRED_ROOF_GSTEP = 0;
		boolean ROOF_GSTEP_FLAG = false;
		
		for(int row = 0; row < dimx; row++) {
			for(int col = 0; col < dimy; col++) {
				if ( ruleOne(row, col, thresh) ) {
					mNUM_EDGES += 1;
					if ( ruleTwo(row, col, thresh) ) {
						mNUM_DIRAC_ASTEP += 1;
					}
					else if ( ruleThree(row, col, thresh) ) {
						ROOF_GSTEP_FLAG = true;
						mNUM_ROOF_GSTEP += 1;
					}
					else if ( ruleFour(row, col, thresh) ) {
						ROOF_GSTEP_FLAG = true;
						mNUM_ROOF_GSTEP += 1;
					}
					
					if ( ruleFive(row, col, thresh, ROOF_GSTEP_FLAG) ) {
						mNUM_BLURRED_ROOF_GSTEP += 1;
					}
				}
			}
		}
		
		return null;
	}
	
	// Is (row, col) an edge?
	static boolean ruleOne(int row, int col, double thresh) {
		return mEMAX1[row][col] > thresh || mEMAX2[row][col] > thresh || mEMAX3[row][col] > thresh;
	}
	
	// Is (row, col) a Dirac or AStep?
	static boolean ruleTwo(int row, int col, double thresh) {
		return mEMAX1[row][col] > mEMAX2[row][col] && mEMAX2[row][col] > mEMAX3[row][col];
	}
	
	// Is (row, col) a Roof or GStep?
	static boolean ruleThree(int row, int col, double thresh) {
		return mEMAX1[row][col] < mEMAX2[row][col] && mEMAX2[row][col] < mEMAX3[row][col];
	}
	
	// Is (row, col) a Roof?
	static boolean ruleFour(int row, int col, double thresh) {
		return mEMAX2[row][col] > mEMAX1[row][col] && mEMAX2[row][col] > mEMAX3[row][col];
	}
	
	// Is (row, col) in a blurred image?
	static boolean ruleFive(int row, int col, double thresh, boolean roof_gstep_flag) {
		return roof_gstep_flag == true && mEMAX1[row][col] < thresh;
	}
	static void square2DArray(double[][] ary) {
		final int rows = ary.length;
		final int cols = ary[0].length;
		double tmp;
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				tmp = ary[r][c];
				ary[r][c] = tmp * tmp;
			}
		}
	}
	
	static void sqrt2DArray(double[][] ary) {
		final int rows = ary.length;
		final int cols = ary[0].length;
		double tmp;
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				tmp = ary[r][c];
				ary[r][c] = Math.sqrt(tmp);
			}
		}
	}
	
	static void display2DArray(double[][] ary) {
		final int rows = ary.length;
		final int cols = ary[0].length;
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < cols; c++) {
				System.out.print(ary[r][c] + " ");
			}
			System.out.println();
		}
	}
	
	static double[][] add2DArrays(double[][] ary1, double[][] ary2) {
		final int rows1 = ary1.length;
		final int cols1 = ary1[0].length;
		final int rows2 = ary2.length;
		final int cols2 = ary2[0].length;
		if ( rows1 != rows2 || cols1 != cols2 ) return null;
		double[][] rslt = new double[rows1][cols1];
		//System.out.println("rows1 = " + rows1 + " " + "cols1 = " + cols1);
		for(int r = 0; r < rows1; r++) {
			for(int c = 0; c < cols1; c++) {
				rslt[r][c] = ary1[r][c] + ary2[r][c];
			}
		}
		return rslt;
	}
	
	public static boolean getIsBlurred() { return mIsBlurred; }
	public static double getBlurExtent() { return mBLUR_EXTENT; }
	
	static void displayImageStats() {
		System.out.println("NUM_EDGES = " + mNUM_EDGES);
		System.out.println("NUM_DIRAC_ASTEP = " + mNUM_DIRAC_ASTEP);
		System.out.println("NUM_ROOF_GSTEP = " + mNUM_ROOF_GSTEP);
		System.out.println("NUM_BLURRED_ROOF_GSTEP = " + mNUM_BLURRED_ROOF_GSTEP);
	}
	
	static void displayEmaxes() {
		System.out.println("EMAX1 dim = " + mEMAX1.length);
		TwoDHaar.displaySample(mEMAX1, mEMAX1.length, 0);
		System.out.println("EMAX2 dim = " + mEMAX2.length);
		TwoDHaar.displaySample(mEMAX2, mEMAX2.length, 0);
		System.out.println("EMAX3 dim = " + mEMAX3.length);
		TwoDHaar.displaySample(mEMAX3, mEMAX3.length, 0);
	}

}
