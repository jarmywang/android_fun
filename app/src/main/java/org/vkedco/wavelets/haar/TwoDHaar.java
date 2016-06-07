package org.vkedco.wavelets.haar;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Random;

;

/**
 * ======================================================================
 * 
 * @author Vladimir Kulyukin
 * 
 *         An implementation of 2D Ordered Fast Haar Wavelet Transform, Ordered
 *         Fast Inverse Haar Wavelet Transform, Inplace Fast Haar Wavelet
 *         Transform, and Inplace Fast Inverse Haar Wavelet Transform as
 *         specified in Ch. 02 of "Wavelets Made Easy" by Yves Nievergelt.
 * 
 *         The 2D Haar Transform (2DHT) is applied to a n x n sample, where n is
 *         an integral power of 2. The horizontal change (HC) is the change
 *         between the left half of the sample and the right half of the sample
 *         divided by the size of the sample. The vertical change (VC) is the
 *         change between the upper half of the sample and the lower half of the
 *         sample divided by the size of the sample. The diagonal change (DC) is
 *         the change between the 1st diagonal (top left to bottom right) and
 *         the 2nd diagonal (top right to bottom left) divided by the size of
 *         the sample.
 * 
 *         Let us consider a few examples. Suppose our sample is
 * 
 *         {{1, 0}, {0, 1}}
 * 
 *         Then HC is the sum total of the the left half (1 + 0) minus the sum
 *         total of the right half (0 + 1) divided by 4, i.e., (1 - 1)/4 = 0. In
 *         other words, the sum total of the left column minus the sum total of
 *         the right column divided by the size of the sample. VC is the sum
 *         total of the upper half (1 + 0) minus the sum total of the lower half
 *         (0 + 1) divided by 4, i.e., (1-1)/2 = 0. In other words, the sum
 *         total of the first row minus the sum total of the second row divided
 *         by the size of the sample. DC is the sum total of the 1st diagonal (1
 *         + 1) minus the sum total of the second diagonal (0+0) divided by 4 =
 *         (2-0)/2 = 0.5.
 * 
 *         Suppose the sample is
 * 
 *         {{255, 100}, {{50, 250}}.
 * 
 *         Then HC = ((255 + 50) - (100 + 250))/4 = -11.255 VC = ((255 + 100) -
 *         (150 + 250))/4 = 13.75 DC = ((255 + 250) - (100 + 50))/4 = 88.75
 * 
 *         More documentation at
 *         http://vkedco.blogspot.com/2014/07/1d-and-2d-haar
 *         -wavelet-transforms-in.html.
 * 
 *         Bugs to vladimir dot kulyukin at gmail dot com
 *         ==================================================================
 */

public class TwoDHaar {

	// temporary storage places used in methods.
	static double[][] mTemp4x4Quad = null;
	static double[][] mFastInverse2x2 = null;
	static double[][] mTemp2x2Quad1 = null;
	static double[][] mOrderedInverse2x2 = null;

	static {
		mTemp4x4Quad = TwoDHaar.make2DArray(4, 4);
		mFastInverse2x2 = TwoDHaar.make2DArray(2, 2);
		mTemp2x2Quad1 = TwoDHaar.make2DArray(2, 2);
		mOrderedInverse2x2 = TwoDHaar.make2DArray(2, 2);
	}

	// p. 40 - top left
	static final short AVG_MTRX[][] = { { 1, 1 }, { 1, 1 } };
	// p. 40 - top right
	static final short HOR_MTRX[][] = { { 1, -1 }, { 1, -1 } };
	// p. 40 - bottom left
	static final short VER_MTRX[][] = { { 1, 1 }, { -1, -1 } };
	// p. 40 - bottom right
	static final short DIAG_MTRX[][] = { { 1, -1 }, { -1, 1 } };

	public static void displaySample(double[][] sample, int size, int num_tabs) {
		for (int x = 0; x < size; x++) {
			for (int i = 0; i < num_tabs; i++) {
				System.out.print("\t");
			}
			for (int y = 0; y < size; y++) {

				System.out.print(sample[x][y] + "\t");
			}
			System.out.println();
		}
	}

	public static void displaySample(double[][] sample, int tlx, int tly,
			int size, int num_tabs) {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int i = 0; i < num_tabs; i++) {
					System.out.print("\t");
				}
				System.out.print(sample[tlx + x][tly + y] + "\t");
			}
			System.out.println();
		}
	}

	public static double computeAvrg(double[][] sample, int size) {
		double total = 0.0;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				total += sample[row][col];
			}
		}
		return total / (size * size);
	}

	public static double computeAbsAvrg(double[][] sample, int size) {
		double total = 0.0;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				total += Math.abs(sample[row][col]);
			}
		}
		return total / (size * size);
	}

	public static double getAvrg(double[][] sample, int top_left_x,
			int top_left_y, int avrg_num, int size) {
		switch (avrg_num) {
		case 0:
			return sample[top_left_x][top_left_y];
		case 1:
			return sample[top_left_x][top_left_y + size];
		case 2:
			return sample[top_left_x + size][top_left_y];
		case 3:
			return sample[top_left_x + size][top_left_y + size];
		default:
			return -1;
		}
	}

	public static double getHorWvlt(double[][] sample, int top_left_x,
			int top_left_y, int wavelet_num, int size) {
		switch (wavelet_num) {
		case 0:
			return sample[top_left_x][top_left_y + 1];
		case 1:
			return sample[top_left_x][top_left_y + size + 1];
		case 2:
			return sample[top_left_x + size][top_left_y + 1];
		case 3:
			return sample[top_left_x + size][top_left_y + size + 1];
		default:
			return -1;
		}
	}

	public static double getVerWvlt(double[][] sample, int top_left_x,
			int top_left_y, int wavelet_num, int size) {
		switch (wavelet_num) {
		case 0:
			return sample[top_left_x + 1][top_left_y];
		case 1:
			return sample[top_left_x + 1][top_left_y + size];
		case 2:
			return sample[top_left_x + size + 1][top_left_y];
		case 3:
			return sample[top_left_x + size + 1][top_left_y + size];
		default:
			return -1;
		}
	}

	public static double getDiagWvlt(double[][] sample, int top_left_x,
			int top_left_y, int wavelet_num, int size) {
		switch (wavelet_num) {
		case 0:
			return sample[top_left_x + 1][top_left_y + 1];
		case 1:
			return sample[top_left_x + 1][top_left_y + size + 1];
		case 2:
			return sample[top_left_x + size + 1][top_left_y + 1];
		case 3:
			return sample[top_left_x + size + 1][top_left_y + size + 1];
		default:
			return -1;
		}
	}

	// x is the number of rows
	// y is the number of columns
	public static double[][] make2DArray(int x, int y) {
		return new double[x][y];
	}

	static void copy2DSubArrayInto2DArray(double[][] subarray,
			double[][] array, int tlx, int tly, int size) {
		for (int x = 0; x < size; x++) {
			System.arraycopy(subarray[x], 0, array[tlx + x], tly, size);
		}
	}

	public static boolean compareSamples(double[][] sample_01,
			double[][] sample_02, int size) {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (sample_01[x][y] != sample_02[x][y]) {
					return false;
				}
			}
		}
		return true;
	}

	public static double[][] makeRandom2DArray(int num_rows, int num_cols,
			int from, int upto) {
		double[][] array = make2DArray(num_rows, num_cols);
		Random rnd = new Random();
		int diff = upto - from + 1;
		for (int x = 0; x < num_rows; x++) {
			for (int y = 0; y < num_cols; y++) {
				array[x][y] = rnd.nextInt(diff) + from;
			}
		}
		return array;
	}

	public static double[][] makeRandom2DEvenArray(int num_rows, int num_cols,
			int from, int upto) {
		double[][] array = make2DArray(num_rows, num_cols);
		Random rnd = new Random();
		int diff = upto - from + 1;

		if (diff < 2) {
			return null;
		}

		for (int x = 0; x < num_rows; x++) {
			for (int y = 0; y < num_cols; y++) {
				double num = rnd.nextInt(diff) + from;
				if (num % 2 == 0) {
					array[x][y] = num;
				} else if (num + 1 <= upto) {
					array[x][y] = num + 1;
				} else {
					array[x][y] = num - 1;
				}
			}
		}
		return array;
	}

	public static double[][] getAveragesFromOrderedList(
			ArrayList<double[][]> ordered_ht_list) {
		return ordered_ht_list.get(0);
	}

	public static double[][] getHorizontalsFromOrderedList(
			ArrayList<double[][]> ordered_ht_list) {
		return ordered_ht_list.get(1);
	}

	public static double[][] getVerticalsFromOrderedList(
			ArrayList<double[][]> ordered_ht_list) {
		return ordered_ht_list.get(2);
	}

	public static double[][] getDiagonalsFromOrderedList(
			ArrayList<double[][]> ordered_ht_list) {
		return ordered_ht_list.get(3);
	}

	// @author Sarat Kiran Andhavarapu
	public static double[][] getHorizontalsFromOrderedListFinal(
			ArrayList<double[][]> ordered_ht_list) {
		return ordered_ht_list.get(ordered_ht_list.size() - 1 - 3);
	}

	// @author Sarat Kiran Andhavarapu
	public static double[][] getVerticalsFromOrderedListFinal(
			ArrayList<double[][]> ordered_ht_list) {
		return ordered_ht_list.get(ordered_ht_list.size() - 1 - 2);
	}

	// @author Sarat Kiran Andhavarapu
	public static double[][] getDiagonalsFromOrderedListFinal(
			ArrayList<double[][]> ordered_ht_list) {
		return ordered_ht_list.get(ordered_ht_list.size() - 1 - 1);
	}

	// ArrayList<double[][]> consists of four 2D arrays: horizontals, verticals,
	// and
	// diagonals, averages; n is a power of 2. such that the sample is 2^n by
	// 2^n.
	// num_steps is how many iterations ordered fast haar transform is applied
	// to the sample.
	// For example, suppose sample = {{9, 7}, {5, 3}}. Then
	// orderedFastHaarWaveletTransformForNumIters(sample, 1, 1)
	// returns ArrayList of double[][] where
	// the 1st element is {{1}} - horizontal changes after 1st iteration;
	// the 2nd element is {{2}} - vertical changes after 1st iteration;
	// the 3rd element is {{0}} - diagonal changes after 1st iteration;
	// the 4th element is {{6}} - averages after 1st iteration.
	// Suppose the sample = {{9, 7, 6, 2},
	// {5, 3, 4, 4},
	// {8, 2, 4, 0},
	// {6, 0, 2, 2}}.
	// Then orderedHaarWaveletTransform(sample, 2, 1) returns ArrayList of
	// double[][] where
	// the 1st element is {{1, 1},
	// {3, 1}} - horizontal changes after 1st iteration;
	// the 2nd element is {{2, 0},
	// {1, 0}} - vertical changes after 1st iteration;
	// the 3rd element is {{0, 1},
	// {0, 1}} - diagonal changes after 1st iteration;
	// the 4th element is {{6, 4},
	// {4, 2}} - averages after 1st iterations.
	// If the sample remains the same and orderedHaarWaveletTransform(sample, 2,
	// 2) is called, then
	// the returned ArrayList of double[][] has the following contents:
	// 1st element is {{1, 1},
	// {3, 1}} - horizontal changes after the 1st iteration
	// 2nd element is {{2, 0},
	// {1, 0}} - vertical changes after the 1st iteration,
	// 3rd element is {{0, 1},
	// {0, 1}} - diagonal changes after the 1st iteration,
	// 4th element is {{1}} - horizontal changes after the 2nd iteration,
	// 5th element is {{1}} - vertical changes after the 2nd iteration,
	// 6th element is {{0}} - diagonal changes after the 2nd iteration,
	// 7th element is {{4}} - averages after the 2nd iteration.
	// In general, let H(n), V(n), D(n), and A(n) be the horizontals, verticals,
	// diagonals and
	// averages after the n-th iteration. the contents of the returned ArrayList
	// of double[][] are:
	// H(1), V(1), D(1), H(2), V(2), D(2), H(3), V(3), D(3), ...., H(n), V(n),
	// D(n), A(n).
	public static ArrayList<double[][]> orderedFastHaarWaveletTransformForNumIters(
			double[][] sample, int n, int num_iters) {
		if (num_iters > n)
			return null;
		int size = n;
		ArrayList<double[][]> iteration_rslt = null;
		ArrayList<double[][]> rslt = new ArrayList<double[][]>();
		//int count_delete = 0;
		double[][] averages = sample;
		while (num_iters-- > 0) {
			iteration_rslt = TwoDHaar
					.orderedFastHaarWaveletTransformForNumItersAux(averages,
							size);
			size -= 1;
			averages = TwoDHaar.getAveragesFromOrderedList(iteration_rslt);

			// System.out.println("averages at dim = " + dim);
			// TwoDHaar.displaySample(averages, dim, 0);
			// System.out.println("real average == " +
			// TwoDHaar.computeAvrg(sample, dim));
			// System.out.println("abs  average == " +
			// TwoDHaar.computeAbsAvrg(sample, dim));

			rslt.add(TwoDHaar.getHorizontalsFromOrderedList(iteration_rslt));
			rslt.add(TwoDHaar.getVerticalsFromOrderedList(iteration_rslt));
			rslt.add(TwoDHaar.getDiagonalsFromOrderedList(iteration_rslt));

			// Use this to see the how 2dHWT is acting on the image . Not
			// required normally just for understanding of it.
			// -------------------
			// @author Sarat Kiran Andhavarapu
			/*
			 * if(count_delete == 0) {
			 * 
			 * Mat hor =
			 * getMatFrom2DPixelArrayFinal(TwoDHaar.getHorizontalsFromOrderedList
			 * (iteration_rslt)); Highgui.imwrite("/home/usu/Desktop/images/hor"
			 * + "_" + "_Edges_.JPG", hor); Mat ver =
			 * getMatFrom2DPixelArrayFinal
			 * (TwoDHaar.getVerticalsFromOrderedList(iteration_rslt));
			 * Highgui.imwrite("/home/usu/Desktop/images/ver" + "_" +
			 * "_Edges_.JPG", ver); Mat dia =
			 * getMatFrom2DPixelArrayFinal(TwoDHaar
			 * .getDiagonalsFromOrderedList(iteration_rslt));
			 * Highgui.imwrite("/home/usu/Desktop/images/dia" + "_" +
			 * "_Edges_.JPG", hor); }
			 * 
			 * if(count_delete == 1) {
			 * 
			 * Mat hor =
			 * getMatFrom2DPixelArrayFinal(TwoDHaar.getHorizontalsFromOrderedList
			 * (iteration_rslt));
			 * Highgui.imwrite("/home/usu/Desktop/images/hor1" + "_" +
			 * "_Edges_.JPG", hor); Mat ver =
			 * getMatFrom2DPixelArrayFinal(TwoDHaar
			 * .getVerticalsFromOrderedList(iteration_rslt));
			 * Highgui.imwrite("/home/usu/Desktop/images/ver1" + "_" +
			 * "_Edges_.JPG", ver); Mat dia =
			 * getMatFrom2DPixelArrayFinal(TwoDHaar
			 * .getDiagonalsFromOrderedList(iteration_rslt));
			 * Highgui.imwrite("/home/usu/Desktop/images/dia1" + "_" +
			 * "_Edges_.JPG", hor); }
			 */
			// final int dim = (int)Math.pow(2, size);
			// displayOrderedFastHaarWaveletTransformResults(iteration_rslt,
			// dim);
			//count_delete++;
		}

		rslt.add(averages);
		return rslt;
	}

	private static final Mat getMatFrom2DPixelArrayFinal(double[][] array) {
		// steps is the num_steps_forward
		Mat newMat = new Mat(array.length, array.length, CvType.CV_16S);
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				newMat.put(i, j, array[i][j]);
			}
		}
		return newMat;
	}

	public static void displayOrderedFastHaarWaveletTransformResults(
			ArrayList<double[][]> transform, int dim) {
		double[][] temp = TwoDHaar.getAveragesFromOrderedList(transform);
		double real_mean_mean = 0;
		double abs_mean_mean = 0;
		double real_hor_mean = 0;
		double abs_hor_mean = 0;
		double real_ver_mean = 0;
		double abs_ver_mean = 0;
		double real_dig_mean = 0;
		double abs_dig_mean = 0;
		// compute MEAN/HOR, MEAN/VER, MEAN/DIG
		System.out.println("MEAN at dim = " + dim);
		TwoDHaar.displaySample(temp, dim, 0);
		System.out.println("-----------------------------");
		real_mean_mean = TwoDHaar.computeAvrg(temp, dim);
		abs_mean_mean = TwoDHaar.computeAbsAvrg(temp, dim);
		System.out.println("MEAN real average == " + real_mean_mean);
		System.out.println("MEAN abs  average == " + abs_mean_mean);
		System.out.println("*****************************");

		temp = TwoDHaar.getHorizontalsFromOrderedList(transform);
		System.out.println("horizontals at dim = " + dim);
		TwoDHaar.displaySample(temp, dim, 0);
		System.out.println("-----------------------------");
		real_hor_mean = TwoDHaar.computeAvrg(temp, dim);
		abs_hor_mean = TwoDHaar.computeAbsAvrg(temp, dim);
		System.out.println("HOR real average == " + real_hor_mean);
		System.out.println("HOR abs average  == " + abs_hor_mean);
		System.out.println("*****************************");

		temp = TwoDHaar.getVerticalsFromOrderedList(transform);
		System.out.println("verticals at dim = " + dim);
		TwoDHaar.displaySample(temp, dim, 0);
		System.out.println("-----------------------------");
		real_ver_mean = TwoDHaar.computeAvrg(temp, dim);
		abs_ver_mean = TwoDHaar.computeAbsAvrg(temp, dim);
		System.out.println("VER real average == " + real_ver_mean);
		System.out.println("VER abs average  == " + abs_ver_mean);
		System.out.println("*****************************");

		temp = TwoDHaar.getDiagonalsFromOrderedList(transform);
		System.out.println("diagonals at dim = " + dim);
		TwoDHaar.displaySample(temp, dim, 0);
		System.out.println("-----------------------------");
		real_dig_mean = TwoDHaar.computeAvrg(temp, dim);
		abs_dig_mean = TwoDHaar.computeAbsAvrg(temp, dim);
		System.out.println("DIG real average == " + real_dig_mean);
		System.out.println("DIG abs average  == " + abs_dig_mean);
		System.out.println("*****************************");
		// Display overall stats
		if (abs_hor_mean != 0)
			System.out.println("|MEAN|/|HOR| == " + abs_mean_mean
					/ abs_hor_mean);
		else
			System.out.println("|MEAN|/|HOR| == UNDEF");

		if (real_hor_mean != 0)
			System.out.println("MEAN/HOR     == " + real_mean_mean
					/ real_hor_mean);
		else
			System.out.println("MEAN/HOR     == UNDEF");

		if (abs_ver_mean != 0)
			System.out.println("|MEAN|/|VER| == " + abs_mean_mean
					/ abs_ver_mean);
		else
			System.out.println("|MEAN|/|VER| == UNDEF");

		if (real_ver_mean != 0)
			System.out.println("MEAN/VER     == " + real_mean_mean
					/ real_ver_mean);
		else
			System.out.println("MEAN/VER     == UNDEF");

		if (abs_dig_mean != 0)
			System.out.println("|MEAN|/|DIG| == " + abs_mean_mean
					/ abs_dig_mean);
		else
			System.out.println("|MEAN|/|DIG| == UNDEF");

		if (real_dig_mean != 0)
			System.out.println("MEAN/DIG     == " + real_mean_mean
					/ real_dig_mean);
		else
			System.out.println("MEAN/DIG     == UNDEF");
		System.out.println("*****************************");
	}

	// This always returns Array<double[][]> rslt, where rslt.get(0) is a 2D
	// array of averages,
	// rslt.get(1) is a 2D array of horizontals (changes from left to right),
	// rslt.get(2) is
	// a 2D array of verticals (changes from top to bottom), rslt.get(3) is a 2D
	// array of
	// diagonals (diagonal changes from top left to bottom right and from top
	// right to
	// bottom left).
	public static ArrayList<double[][]> orderedFastHaarWaveletTransformForNumItersAux(
			double[][] sample, int n) {

		ArrayList<double[][]> rslt = new ArrayList<double[][]>();
		if (n <= 0) {
			rslt.add(sample);
			rslt.add(sample);
			rslt.add(sample);
			rslt.add(sample);
			return rslt;
		} else {
			final int size = (int) Math.pow(2, n);
			final int half_size = size / 2;
			// System.out.println("half_size = " + half_size);
			double[][] averages = new double[half_size][half_size];
			double[][] horizontals = new double[half_size][half_size];
			double[][] verticals = new double[half_size][half_size];
			double[][] diagonals = new double[half_size][half_size];
			int i = 0, j = 0;
			double[][] tmpMatrix = new double[2][2];
			for (int tlx = 0; tlx < size; tlx += 2) {
				j = 0;
				for (int tly = 0; tly < size; tly += 2) {
					tmpMatrix[0][0] = sample[tlx][tly];
					tmpMatrix[0][1] = sample[tlx][tly + 1];
					tmpMatrix[1][0] = sample[tlx + 1][tly];
					tmpMatrix[1][1] = sample[tlx + 1][tly + 1];
					TwoDHaar.inPlaceFastHaarWaveletTransform(tmpMatrix, 2);
					// System.out.println("tmpMtrx");
					// TwoDHaar.displaySample(tmpMatrix, 2, 0);
					averages[i][j] = tmpMatrix[0][0];
					horizontals[i][j] = tmpMatrix[0][1];
					verticals[i][j] = tmpMatrix[1][0];
					diagonals[i][j] = tmpMatrix[1][1];
					j += 1;
				}
				i += 1;
			}

			rslt.add(averages);
			rslt.add(horizontals);
			rslt.add(verticals);
			rslt.add(diagonals);

			return rslt;
		}
	}

	// Apply 2D Inverse Fast Haar Wavelet Transform on the ordered haar
	// transfrom for a specific number of iterations.
	public static void orderedFastInverseHaarWaveletTransformForNumIters(
			ArrayList<double[][]> ordered_transform, int num_iters) {
		for (int L = 0; L < num_iters; L++) {
			TwoDHaar.orderedFastInverseHaarWaveletTransformOnce(ordered_transform);
		}
	}

	public static void orderedFastInverseHaarWaveletTransformOnce(
			ArrayList<double[][]> transform) {
		int size = transform.size();
		double[][] averages = transform.get(size - 1);
		double[][] diagonals = transform.get(size - 2);
		double[][] verticals = transform.get(size - 3);
		double[][] horizontals = transform.get(size - 4);
		final int half_n = averages[0].length;
		final int n = 2 * half_n;

		double[][] rslt = new double[n][n];
		int copy_x = 0;
		int copy_y = 0;

		for (int tlx = 0; tlx < half_n; tlx++) {
			copy_y = 0;
			for (int tly = 0; tly < half_n; tly++) {
				TwoDHaar.mOrderedInverse2x2[0][0] = averages[tlx][tly];
				TwoDHaar.mOrderedInverse2x2[0][1] = horizontals[tlx][tly];
				TwoDHaar.mOrderedInverse2x2[1][0] = verticals[tlx][tly];
				TwoDHaar.mOrderedInverse2x2[1][1] = diagonals[tlx][tly];
				TwoDHaar.fastInverseHaarWaveletTransform2x2(
						TwoDHaar.mOrderedInverse2x2, 0, 0);
				TwoDHaar.copy2DSubArrayInto2DArray(TwoDHaar.mOrderedInverse2x2,
						rslt, copy_x, copy_y, 2);
				copy_y += 2;
			}
			copy_x += 2;
		}

		transform.remove(size - 1);
		transform.remove(size - 2);
		transform.remove(size - 3);
		transform.remove(size - 4);
		transform.add(rslt);
	}

	// this is a fast inverst haar transform applied to a 2x2 subsample of the
	// sample whose
	// top left corner in the sample is (tlx, tly).
	private static void fastInverseHaarWaveletTransform2x2(double[][] sample,
			int tlx, int tly) {
		// double rslt_mtrx[][] = new double[2][2];
		final double avrg = sample[tlx][tly];
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				TwoDHaar.mFastInverse2x2[x][y] = avrg * TwoDHaar.AVG_MTRX[x][y];
			}
		}
		final double hor = sample[tlx][tly + 1];
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				TwoDHaar.mFastInverse2x2[x][y] += hor * TwoDHaar.HOR_MTRX[x][y];
			}
		}
		final double ver = sample[tlx + 1][tly];
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				TwoDHaar.mFastInverse2x2[x][y] += ver * TwoDHaar.VER_MTRX[x][y];
			}
		}
		final double diag = sample[tlx + 1][tly + 1];
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				TwoDHaar.mFastInverse2x2[x][y] += diag
						* TwoDHaar.DIAG_MTRX[x][y];
			}
		}

		for (int x = 0; x < 2; x++) {
			System.arraycopy(TwoDHaar.mFastInverse2x2[x], 0, sample[tlx + x],
					tly, 2);
		}
	}

	// Same as orderedFastHaarWaveletTransformForNumIters but does everything in
	// place. the sample is
	// destructively modified. size is the real size of the sample that must be
	// a integral
	// power of 2. The parameter size must be an integral power of 2 and
	// indicates the
	// size of the sample, i.e., the sample is size x size.
	// TwoDHaar.orderedFastHaarWaveletTransformForNumIters(sample_4x4_06, 2, 2)
	// outputs:
	// H(1) = {{0.0, 0.0},
	// {0.0, 0.0}}
	// V(1) = {{0.0, 0.0},
	// {0.0, 0.0}}
	// D(1) = {{127.5, 0.0},
	// {0.0, 127.5}}
	// H(2) = {{0.0}}
	// V(2) = {{0.0}}
	// D(2) = {{63.75}}
	// A(2) = {{63.75}}
	// inPlaceFastHaarWaveletTransform on the sample outputs:
	// {{63.75, 0.0, 0.0, 0.0},
	// {0.0, 127.5, 0.0, 0.0},
	// {0.0, 0.0, 63.75, 0.0},
	// {0.0, 0.0, 0.0, 127.5}}
	// The output of the inPlaceFastHaarWaveletTransform encodes the same
	// information but
	// differently. The entry in the top left corner encodes A(2) = 63.75. The
	// entry
	// in [0, 2] encodes H(2) = 0.0. The entry in [2, 0] encodes V(2) = 0.0. The
	// entry in [2, 2] encodes D(2) = 63.75.
	public static void inPlaceFastHaarWaveletTransform(double[][] sample,
			int size) {
		inPlaceFastHaarWaveletTransformAt(sample, 0, 0, size);
	}

	// A more flexible version of the above method in that it allows the caller
	// to apply
	// the inverse Haar transform to a sub-sample of the sample. The size of the
	// sub-sample
	// is quad_size x quad_size and its top left corner is specified by tlx and
	// tly. quad_size
	// must be an integral power of 2.
	public static void inPlaceFastHaarWaveletTransformAt(double[][] sample,
			int tlx, int tly, int quad_size) {

		if (quad_size < 2) {
			return;
		}

		if (quad_size == 2) {
			double zero_plus = sample[tlx][tly] + sample[tlx][tly + 1];
			double zero_minus = sample[tlx][tly] - sample[tlx][tly + 1];
			double one_plus = sample[tlx + 1][tly] + sample[tlx + 1][tly + 1];
			double one_minus = sample[tlx + 1][tly] - sample[tlx + 1][tly + 1];

			sample[tlx][tly] = zero_plus / 2;
			sample[tlx][tly + 1] = zero_minus / 2;
			sample[tlx + 1][tly] = one_plus / 2;
			sample[tlx + 1][tly + 1] = one_minus / 2;

			zero_plus = sample[tlx][tly] + sample[tlx + 1][tly];
			zero_minus = sample[tlx][tly] - sample[tlx + 1][tly];
			one_plus = sample[tlx][tly + 1] + sample[tlx + 1][tly + 1];
			one_minus = sample[tlx][tly + 1] - sample[tlx + 1][tly + 1];

			sample[tlx][tly] = zero_plus / 2;
			sample[tlx + 1][tly] = zero_minus / 2;
			sample[tlx][tly + 1] = one_plus / 2;
			sample[tlx + 1][tly + 1] = one_minus / 2;
		} else {
			// 1. divide the sample into four quads: Quad0, Quad1, Quad2, Quad3.
			final int half_size = quad_size / 2;
			// top left corner of Quad0
			final int quad_0_tlx = tlx;
			final int quad_0_tly = tly;
			// top left corner of Quad1
			final int quad_1_tlx = tlx;
			final int quad_1_tly = tly + half_size;
			// top left corner of Quad2
			final int quad_2_tlx = tlx + half_size;
			final int quad_2_tly = tly;
			// top left corner of Quad3
			final int quad_3_tlx = tlx + half_size;
			final int quad_3_tly = tly + half_size;

			// 2. Recursively transform each quad in place
			inPlaceFastHaarWaveletTransformAt(sample, quad_0_tlx, quad_0_tly,
					half_size);
			inPlaceFastHaarWaveletTransformAt(sample, quad_1_tlx, quad_1_tly,
					half_size);
			inPlaceFastHaarWaveletTransformAt(sample, quad_2_tlx, quad_2_tly,
					half_size);
			inPlaceFastHaarWaveletTransformAt(sample, quad_3_tlx, quad_3_tly,
					half_size);

			// 3. get the averages from each quad
			double[][] averages = TwoDHaar.make2DArray(2, 2);
			averages[0][0] = TwoDHaar.getAvrg(sample, quad_0_tlx, quad_0_tly,
					0, half_size);
			averages[0][1] = TwoDHaar.getAvrg(sample, quad_0_tlx, quad_0_tly,
					1, half_size);
			averages[1][0] = TwoDHaar.getAvrg(sample, quad_0_tlx, quad_0_tly,
					2, half_size);
			averages[1][1] = TwoDHaar.getAvrg(sample, quad_0_tlx, quad_0_tly,
					3, half_size);

			// 4. do the row transform on the averages
			sample[quad_0_tlx][quad_0_tly] = (averages[0][0] + averages[0][1]) / 2;
			sample[quad_1_tlx][quad_1_tly] = (averages[0][0] - averages[0][1]) / 2;
			sample[quad_2_tlx][quad_2_tly] = (averages[1][0] + averages[1][1]) / 2;
			sample[quad_3_tlx][quad_3_tly] = (averages[1][0] - averages[1][1]) / 2;

			// 5. do the col transform transform on the averages
			final double left_col_plus = (sample[quad_0_tlx][quad_0_tly] + sample[quad_2_tlx][quad_2_tly]) / 2;
			final double left_col_minus = (sample[quad_0_tlx][quad_0_tly] - sample[quad_2_tlx][quad_2_tly]) / 2;
			final double right_col_plus = (sample[quad_1_tlx][quad_1_tly] + sample[quad_3_tlx][quad_3_tly]) / 2;
			final double right_col_minus = (sample[quad_1_tlx][quad_1_tly] - sample[quad_3_tlx][quad_3_tly]) / 2;

			// 6. put the results back into the sample
			sample[quad_0_tlx][quad_0_tly] = left_col_plus;
			sample[quad_2_tlx][quad_2_tly] = left_col_minus;
			sample[quad_1_tlx][quad_1_tly] = right_col_plus;
			sample[quad_3_tlx][quad_3_tly] = right_col_minus;

			averages = null;
		}
	}

	// apply 2D Haar transform to the sub-sample of the sample a pecific number
	// of iterations. the sub-sample's top
	// left corner is (tlx, tly) and its size is quad_size x quad_size.
	public static void inPlaceFastHaarWaveletTransformForNumIters(
			double[][] sample, int tlx, int tly, int quad_size, int num_iters) {

		if (quad_size < 2) {
			return;
		}

		if (num_iters == 0) {
			return;
		}

		if (quad_size == 2) {
			double zero_plus = sample[tlx][tly] + sample[tlx][tly + 1];
			double zero_minus = sample[tlx][tly] - sample[tlx][tly + 1];
			double one_plus = sample[tlx + 1][tly] + sample[tlx + 1][tly + 1];
			double one_minus = sample[tlx + 1][tly] - sample[tlx + 1][tly + 1];

			sample[tlx][tly] = zero_plus / 2;
			sample[tlx][tly + 1] = zero_minus / 2;
			sample[tlx + 1][tly] = one_plus / 2;
			sample[tlx + 1][tly + 1] = one_minus / 2;

			zero_plus = sample[tlx][tly] + sample[tlx + 1][tly];
			zero_minus = sample[tlx][tly] - sample[tlx + 1][tly];
			one_plus = sample[tlx][tly + 1] + sample[tlx + 1][tly + 1];
			one_minus = sample[tlx][tly + 1] - sample[tlx + 1][tly + 1];

			sample[tlx][tly] = zero_plus / 2;
			sample[tlx + 1][tly] = zero_minus / 2;
			sample[tlx][tly + 1] = one_plus / 2;
			sample[tlx + 1][tly + 1] = one_minus / 2;
		} else {
			// 1. divide the sample into four quads: Quad0, Quad1, Quad2, Quad3.
			final int half_size = quad_size / 2;
			// top left corner of Quad0
			final int quad_0_tlx = tlx;
			final int quad_0_tly = tly;
			// top left corner of Quad1
			final int quad_1_tlx = tlx;
			final int quad_1_tly = tly + half_size;
			// top left corner of Quad2
			final int quad_2_tlx = tlx + half_size;
			final int quad_2_tly = tly;
			// top left corner of Quad3
			final int quad_3_tlx = tlx + half_size;
			final int quad_3_tly = tly + half_size;

			// 2. Recursively transform each quad in place
			inPlaceFastHaarWaveletTransformForNumIters(sample, quad_0_tlx,
					quad_0_tly, half_size, num_iters - 1);
			inPlaceFastHaarWaveletTransformForNumIters(sample, quad_1_tlx,
					quad_1_tly, half_size, num_iters - 1);
			inPlaceFastHaarWaveletTransformForNumIters(sample, quad_2_tlx,
					quad_2_tly, half_size, num_iters - 1);
			inPlaceFastHaarWaveletTransformForNumIters(sample, quad_3_tlx,
					quad_3_tly, half_size, num_iters - 1);

			// 3. get the averages from each quad
			double[][] averages = TwoDHaar.make2DArray(2, 2);
			averages[0][0] = TwoDHaar.getAvrg(sample, quad_0_tlx, quad_0_tly,
					0, half_size);
			averages[0][1] = TwoDHaar.getAvrg(sample, quad_0_tlx, quad_0_tly,
					1, half_size);
			averages[1][0] = TwoDHaar.getAvrg(sample, quad_0_tlx, quad_0_tly,
					2, half_size);
			averages[1][1] = TwoDHaar.getAvrg(sample, quad_0_tlx, quad_0_tly,
					3, half_size);

			// 4. do the row transform on the averages
			sample[quad_0_tlx][quad_0_tly] = (averages[0][0] + averages[0][1]) / 2;
			sample[quad_1_tlx][quad_1_tly] = (averages[0][0] - averages[0][1]) / 2;
			sample[quad_2_tlx][quad_2_tly] = (averages[1][0] + averages[1][1]) / 2;
			sample[quad_3_tlx][quad_3_tly] = (averages[1][0] - averages[1][1]) / 2;

			// 5. do the col transform transform on the averages
			final double left_col_plus = (sample[quad_0_tlx][quad_0_tly] + sample[quad_2_tlx][quad_2_tly]) / 2;
			final double left_col_minus = (sample[quad_0_tlx][quad_0_tly] - sample[quad_2_tlx][quad_2_tly]) / 2;
			final double right_col_plus = (sample[quad_1_tlx][quad_1_tly] + sample[quad_3_tlx][quad_3_tly]) / 2;
			final double right_col_minus = (sample[quad_1_tlx][quad_1_tly] - sample[quad_3_tlx][quad_3_tly]) / 2;

			// 6. put the results back into the sample
			sample[quad_0_tlx][quad_0_tly] = left_col_plus;
			sample[quad_2_tlx][quad_2_tly] = left_col_minus;
			sample[quad_1_tlx][quad_1_tly] = right_col_plus;
			sample[quad_3_tlx][quad_3_tly] = right_col_minus;

			averages = null;
		}
	}

	// apply the inverse 2D Haar transform to the size x size sample. size is an
	// integral power of 2.
	public static void inPlaceFastInverseHaarWaveletTransform(
			double[][] sample, int size) {
		inPlaceFastInverseHaarWaveletTransformAt(sample, 0, 0, size);
	}

	// apply the inverse 2D Haar transform to the sub-sample of the sample whose
	// top left corner is (tlx, tly)
	// and whose size is quad_size x quad_size.
	public static void inPlaceFastInverseHaarWaveletTransformAt(
			double[][] sample, int tlx, int tly, int quad_size) {
		if (quad_size < 2)
			return;

		if (quad_size == 2) {
			fastInverseHaarWaveletTransform2x2(sample, tlx, tly);
		} else {
			final int half_size = quad_size / 2;
			// System.out.println("half_size = " + half_size);
			// System.out.println("tly+half_size = " + (tly+half_size));
			TwoDHaar.mTemp2x2Quad1[0][0] = sample[tlx][tly];
			TwoDHaar.mTemp2x2Quad1[0][1] = sample[tlx][tly + half_size];
			TwoDHaar.mTemp2x2Quad1[1][0] = sample[tlx + half_size][tly];
			TwoDHaar.mTemp2x2Quad1[1][1] = sample[tlx + half_size][tly
					+ half_size];

			// TwoDHaar.displaySample(TwoDHaar.mTemp2x2Quad1, 2, 0);
			TwoDHaar.fastInverseHaarWaveletTransform2x2(TwoDHaar.mTemp2x2Quad1,
					0, 0);
			// TwoDHaar.displaySample(TwoDHaar.mTemp2x2Quad1, 2, 0);

			sample[tlx][tly] = TwoDHaar.mTemp2x2Quad1[0][0];
			sample[tlx][tly + half_size] = TwoDHaar.mTemp2x2Quad1[0][1];
			sample[tlx + half_size][tly] = TwoDHaar.mTemp2x2Quad1[1][0];
			sample[tlx + half_size][tly + half_size] = TwoDHaar.mTemp2x2Quad1[1][1];

			TwoDHaar.inPlaceFastInverseHaarWaveletTransformAt(sample, tlx, tly,
					half_size);
			TwoDHaar.inPlaceFastInverseHaarWaveletTransformAt(sample, tlx, tly
					+ half_size, half_size);
			TwoDHaar.inPlaceFastInverseHaarWaveletTransformAt(sample, tlx
					+ half_size, tly, half_size);
			TwoDHaar.inPlaceFastInverseHaarWaveletTransformAt(sample, tlx
					+ half_size, tly + half_size, half_size);
		}
	}

	public static void displayOrderedHaarTransform(
			ArrayList<double[][]> haar_transform) {
		for (double[][] ary : haar_transform) {
			int len = ary[0].length;
			System.out.println("dim == " + len);
			TwoDHaar.displaySample(ary, len, 0);
		}
	}
}
