package org.vkedco.opencv.haar;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.vkedco.wavelets.haar.TwoDHaar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TwoDMatHaar {

	static final void displayMat(Mat mat) {
		// mat.convertTo(mat, CvType.CV_64FC3);
		for (int r = 0; r < mat.rows(); r++) {
			System.out.print("Row " + r + ": ");
			for (int c = 0; c < mat.rows(); c++) {
				System.out.print(mat.get(r, c)[0] + " ");
			}
			System.out.println();
		}
	}

	static double log(int x, int base) {
		return (Math.log(x) / Math.log(base));
	}

	static final boolean isPowOf2(int n) {
		if (n < 1)
			return false;
		else {
			double pOf2 = TwoDMatHaar.log(n, 2);
			return Math.abs(pOf2 - (int) pOf2) == 0;
		}
	}

	static final int largestPowOf2SmallerThanX(int n) {

		int rslt = n - 1;
		while (!TwoDMatHaar.isPowOf2(rslt)) {
			rslt--;
		}
		return rslt;
	}

	static final double[][] get2DPixelArrayFromMat(Mat mat) {
		int num_rows = mat.rows();
		int num_cols = mat.cols();

		if (num_rows == 0 || num_cols == 0) {
			System.out.println("empty mat");
			return null;
		}

		if (!TwoDMatHaar.isPowOf2(num_rows)) {
			System.out.println("check get2DPixelArrayFromMat()");
			num_rows = TwoDMatHaar.largestPowOf2SmallerThanX(num_rows);
			System.out.println("num_rows = " + num_rows);
		}

		if (!TwoDMatHaar.isPowOf2(num_cols)) {
			num_cols = TwoDMatHaar.largestPowOf2SmallerThanX(num_cols);
			System.out.println("num_cols = " + num_cols);
		}

		int dim = Math.min(num_rows, num_cols);
		num_rows = dim;
		num_cols = dim;

		// System.out.println("dim = " + dim);

		double[][] imgArr = new double[dim][dim];

		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				imgArr[i][j] = mat.get(i, j)[0];
			}
		}

		// System.out.println("ImgArr's num_rows = " + imgArr.length);
		// System.out.println("ImgArr's num_cols = " + imgArr[0].length);
		return imgArr;
	}

	static final double[][] get2DPixelArrayFromMatAt(Mat mat, int x, int y,
			int size) {

		double[][] imgArr = new double[size][size];

		int r = 0;
		int c = 0;
		for (int i = x; i < x + size; i++) {
			c = 0;
			for (int j = y; j < y + size; j++) {
				// System.out.println("get2DPixelArrayFromMatAt(): " + i + ", "
				// + j);
				imgArr[r][c++] = mat.get(i, j)[0];
			}
			r++;
		}

		// System.out.println("ImgArr's num_rows = " + imgArr.length);
		// System.out.println("ImgArr's num_cols = " + imgArr[0].length);
		return imgArr;
	}

	static final Mat getMatFrom2DPixelArray(Mat mat, double[][] array) {
		// steps is the num_steps_forward
		Mat newMat = new Mat(mat.rows(), mat.cols(), CvType.CV_16S);
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				newMat.put(i, j, array[i][j]);
			}
		}
		return newMat;
	}

	static final Mat getMatFrom2DPixelArrayFinal(double[][] array) {
		// steps is the num_steps_forward
		Mat newMat = new Mat(array.length, array.length, CvType.CV_16S);
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				newMat.put(i, j, array[i][j]);
			}
		}
		return newMat;
	}

	static final Mat getMatFrom2DPixelArraySimple(double[][] array) {
		// steps is the num_steps_forward
		Mat newMat = new Mat(array.length, array.length, CvType.CV_16S);
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				newMat.put(i, j, array[i][j]);
			}
		}
		return newMat;
	}

	// Mat is of size 2^n x 2^n.
	public static ArrayList<double[][]> orderedFastHaarWaveletTransformForNumIters(
			Mat mat, int n, int num_iters) {
		double[][] pixels = TwoDMatHaar.get2DPixelArrayFromMat(mat);
		ArrayList<double[][]> rslt = TwoDHaar
				.orderedFastHaarWaveletTransformForNumIters(pixels, n,
						num_iters);
		pixels = null;
		return rslt;
	}

	/***************************************************************
	 * 
	 * Given three thresholds: Theta_HAA, Theta_VAA, Theta_DAA There are the
	 * following stats computed for each window N x N, where N = 2^n: HAA, VAA,
	 * DAA. - If HAA >= Theta_HAA and VAA < Theta_VAA and DAA < Theta_DAA, then
	 * it a vertical edge - If VAA >= Theta_VAA and HAA < Theta_HAA and DAA <
	 * Theta_DAA, then it is a horizontal edge - If (HAA >= Theta_HAA and VAA >=
	 * Theta_VAA) or DAA >= Theta_DAA { then it is a diagonal edge; if ( DRA < 0
	 * ) then edge is from NE to SW if ( DRA > 0 ) then edge is from NW to SE }
	 * 
	 * For now, we assume we iterate until the window is 2 x 2.
	 * 
	 *************************************************************** 
	 */

	public static enum EDGE_TYPE {
		HOR, VER, DIG, NONE
	};

	// N = 2^n
	public static EDGE_TYPE detectEdgeType(double[][] pixels, int n,
			double theta_HAA, double theta_VAA, double theta_DAA) {
		ArrayList<double[][]> transform = TwoDHaar
				.orderedFastHaarWaveletTransformForNumIters(pixels, n, n - 1);
		double[][] horizontals = TwoDHaar
				.getHorizontalsFromOrderedList(transform);
		double[][] verticals = TwoDHaar.getVerticalsFromOrderedList(transform);
		double[][] diagonals = TwoDHaar.getDiagonalsFromOrderedList(transform);
		double HAA = TwoDHaar.computeAbsAvrg(horizontals, 2);
		double VAA = TwoDHaar.computeAbsAvrg(verticals, 2);
		double DAA = TwoDHaar.computeAbsAvrg(diagonals, 2);

		horizontals = null;
		verticals = null;
		diagonals = null;

		if (HAA >= theta_HAA && VAA < theta_VAA && DAA < theta_DAA) {
			return EDGE_TYPE.VER;
		} else if (VAA >= theta_VAA && HAA < theta_HAA && DAA < theta_DAA) {
			return EDGE_TYPE.HOR;
		} else if ((HAA >= theta_HAA && VAA >= theta_VAA) || DAA >= theta_DAA) {
			return EDGE_TYPE.DIG;
		} else {
			return EDGE_TYPE.NONE;
		}
	}

	public static EdgeStats detectEdgeStats(double[][] pixels, int n,
			double theta_HAA, double theta_VAA, double theta_DAA) {

		ArrayList<double[][]> transform = TwoDHaar
				.orderedFastHaarWaveletTransformForNumIters(pixels, n, n - 1);
		double[][] horizontals = TwoDHaar
				.getHorizontalsFromOrderedListFinal(transform);
		double[][] verticals = TwoDHaar
				.getVerticalsFromOrderedListFinal(transform);
		double[][] diagonals = TwoDHaar
				.getDiagonalsFromOrderedListFinal(transform);

		/*
		 * double[][] horizontals =
		 * TwoDHaar.getHorizontalsFromOrderedList(transform); double[][]
		 * verticals = TwoDHaar.getVerticalsFromOrderedList(transform);
		 * double[][] diagonals =
		 * TwoDHaar.getDiagonalsFromOrderedList(transform);
		 */

		double HAA = TwoDHaar.computeAbsAvrg(horizontals, 2);
		double HRA = TwoDHaar.computeAvrg(horizontals, 2);
		double VAA = TwoDHaar.computeAbsAvrg(verticals, 2);
		double VRA = TwoDHaar.computeAvrg(verticals, 2);
		double DAA = TwoDHaar.computeAbsAvrg(diagonals, 2);
		double DRA = TwoDHaar.computeAvrg(diagonals, 2);

		horizontals = null;
		verticals = null;
		diagonals = null;

		if (HAA >= theta_HAA && VAA < theta_VAA && DAA < theta_DAA) {
			return new EdgeStats(EDGE_TYPE.VER, HAA, HRA, VAA, VRA, DAA, DRA);
		} else if (VAA >= theta_VAA && HAA < theta_HAA && DAA < theta_DAA) {
			return new EdgeStats(EDGE_TYPE.HOR, HAA, HRA, VAA, VRA, DAA, DRA);
		} else if ((HAA >= theta_HAA && VAA >= theta_VAA) || DAA >= theta_DAA) {
			return new EdgeStats(EDGE_TYPE.DIG, HAA, HRA, VAA, VRA, DAA, DRA);
		} else {
			return new EdgeStats(EDGE_TYPE.NONE, HAA, HRA, VAA, VRA, DAA, DRA);
		}
	}

	// N = 2^{n}. N = 64, n = 5
	// Test this!!!
	public static ArrayList<int[]> detectEdges(String img_path, Mat img, int N,
			int n, double theta_HAA, double theta_VAA, double theta_DAA) {
		final int num_rows = img.rows();
		final int num_cols = img.cols();
		// System.out.println("num_rows = " + num_rows + " num_cols = " +
		// num_cols);
		ArrayList<int[]> edges = new ArrayList<int[]>();
		Rect roi = null;
		// -------------
		ArrayList<Integer> ListA = new ArrayList<Integer>();
		Map<Integer, Integer> temp = new HashMap<Integer, Integer>();
		Map<Integer, Integer> final_array = new HashMap<Integer, Integer>();
		Map<Integer, ArrayList<Integer>> multiMap = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<cluster_list> ListB = new ArrayList<cluster_list>();

		// variables used for line to line detection. -------------------
		// @author Sarat Kiran Andhavarapu
		boolean before = false;
		boolean before_checker = true;
		int row_number_temp = 0;
		int col_number = 1;
		int id = 1;
		int last = row_number_temp;
		int last_id = row_number_temp;
		cluster_list old;
		int row_total;
		int col_total;
		if (num_rows % N == 0)
			row_total = num_rows / N - 1;
		else
			row_total = num_rows / N;
		if (num_cols % N == 0)
			col_total = num_cols / N - 1;
		else
			col_total = num_cols / N;
		// System.out.println("asdjlhasd:");
		// System.out.println(num_rows/N);
		// System.out.println(num_cols/N);
		boolean out_bound_left = true;
		boolean out_bound_right = true;
		// -------------------------------------------------------------------
		
		// System.out.println(num_rows);
		for (int row = 0; row <= num_rows - N; row += N) {
			col_number++;
			// System.out.println("new column");
			for (int col = 0; col <= num_cols - N; col += N) {
				out_bound_left = out_bound_right = before_checker = true;
				if (col == 0) {
					out_bound_left = false;
				} else if (col == (col_total - 1) * N) {
					out_bound_right = false;
				}

				row_number_temp++;
				// System.out.println(row_number_temp);
				// System.out.println("row = " + row + " col = " + col);
				roi = new Rect(col, row, N, N);
				// System.out.print("roi.x + roi.width = " + (roi.x +
				// roi.width));
				// System.out.println(" m.cols = " + img.cols());
				// System.out.print("roi.y + roi.height = " + (roi.y +
				// roi.height));
				// System.out.println(" m.rows = " + img.rows());

				Mat mask = img.submat(roi);

				EdgeStats estats = TwoDMatHaar.detectEdgeStats(
						TwoDMatHaar.get2DPixelArrayFromMat(mask), n, theta_HAA,
						theta_VAA, theta_DAA);
				if (EDGE_TYPE.NONE != estats.mEdgeType) {
					int[] edge = new int[] { row, col };

					temp.put(row_number_temp, 1);
					
					
					// --------------------------- FINDING IT LINE BY LINE
					// ------------------------
					// @author Sarat Kiran Andhavarapu
					// /System.out.println(row_number_temp++);
					// System.out.println(row_number_temp);
					/*
					 * if(before){ before_checker = false; for (Integer key :
					 * multiMap.keySet()) { ArrayList<Integer> templ =
					 * multiMap.get(key); if
					 * (templ.contains(row_number_temp-1)){
					 * templ.add(row_number_temp); break; } }
					 * 
					 * } if(temp.size() == 0) { ArrayList<Integer> t = new
					 * ArrayList<Integer>(); t.add(row_number_temp);
					 * multiMap.put(id,t); id++;
					 * 
					 * } else{ boolean checker = false; if(out_bound_left &&
					 * temp.containsKey(row_number_temp-col_total-1)){
					 * 
					 * if (before_checker) { for (Integer key :
					 * multiMap.keySet()) { ArrayList<Integer> templ =
					 * multiMap.get(key); if (templ.contains(row_number_temp -
					 * col_total - 1)) { templ.add(row_number_temp); break; } }
					 * }
					 * 
					 * if(out_bound_right &&
					 * temp.containsKey(row_number_temp-col_total+1)) { int
					 * left_key=0; for (Integer key : multiMap.keySet()) {
					 * ArrayList<Integer> templ = multiMap.get(key); if
					 * (templ.contains(row_number_temp - col_total - 1)) {
					 * left_key = key; break; } } int right_key = 0;
					 * ArrayList<Integer> right_key_al = new
					 * ArrayList<Integer>(); for (Integer key :
					 * multiMap.keySet()) { ArrayList<Integer> templ =
					 * multiMap.get(key); if (templ.contains(row_number_temp -
					 * col_total + 1)) { right_key = key; right_key_al = templ;
					 * break; } } if (left_key != right_key) {
					 * multiMap.get(left_key).addAll(right_key_al);
					 * multiMap.remove(right_key); } }
					 * 
					 * 
					 * 
					 * 
					 * }
					 * 
					 * else if(temp.containsKey(row_number_temp-col_total)){
					 * if(before_checker) for (Integer key : multiMap.keySet())
					 * { ArrayList<Integer> templ = multiMap.get(key); if
					 * (templ.contains(row_number_temp-col_total)){
					 * templ.add(row_number_temp);
					 * 
					 * } }
					 * 
					 * } else if(out_bound_right &&
					 * temp.containsKey(row_number_temp-col_total+1) &&
					 * !checker) { if (before) { int last_key=0; for (Integer
					 * key : multiMap.keySet()) { ArrayList<Integer> templ =
					 * multiMap.get(key); if
					 * (templ.contains(row_number_temp-col_total+1)){ last_key =
					 * key; break; } } int remove_key=0; for (Integer key :
					 * multiMap.keySet()) { ArrayList<Integer> templ =
					 * multiMap.get(key); if (templ.contains(row_number_temp)){
					 * multiMap.get(last_key).addAll(templ); remove_key = key; }
					 * } multiMap.remove(remove_key); } else { for (Integer key
					 * : multiMap.keySet()) { ArrayList<Integer> templ =
					 * multiMap.get(key); if
					 * (templ.contains(row_number_temp-col_total+1)){
					 * templ.add(row_number_temp);
					 * 
					 * } } }
					 * 
					 * 
					 * 
					 * 
					 * } else { if (before_checker){
					 * 
					 * ArrayList<Integer> t = new ArrayList<Integer>();
					 * t.add(row_number_temp); multiMap.put(id,t); id++; } }
					 * 
					 * }
					 */
					// ----------------------------------------------------------------
					edges.add(edge);
					Scalar sc = new Scalar(255, 0, 0);
					Point top_left = new Point(col, row);
					Point top_right = new Point(col + roi.width - 1, row);
					Point bot_left = new Point(col, row + roi.height - 1);
					Point bot_right = new Point(roi.x + roi.width - 1, roi.y
							+ roi.width - 1);
					Core.line(img, top_left, top_right, sc);
					Core.line(img, top_right, bot_right, sc);
					Core.line(img, bot_right, bot_left, sc);
					Core.line(img, bot_left, top_left, sc);

					writeEdgeStats(img, estats, top_left, top_right, bot_left,
							bot_right);
					before = true;
					last = row_number_temp;
					// Core.putText(img, , org, fontFace, fontScale, color);
				} else {
					before = false;
				}
			}
			// System.out.println("New line");
		}
		// /System.out.println(col_number);
		// System.out.println("List");
		// for(int i =0;i< ListA.size();i++)\
		// System.out.println(multiMap);
		// System.out.println(temp);
		// System.out.println(final_array);
		// Highgui.imwrite(img_path + "_" + N + "_Edges_.JPG", img);
		return edges;
	}

	static void writeEdgeStats(Mat img, EdgeStats estats, Point top_left,
			Point top_right, Point bot_left, Point bot_right) {
		final String edge_type = estats.mEdgeType.toString();
		DecimalFormat df = new DecimalFormat("#0.0");
		final String haa = String.format(df.format(estats.mHAA) + "|"
				+ String.format(df.format(estats.mHRA)));
		final String vaa = String.format(df.format(estats.mVAA) + "|"
				+ String.format(df.format(estats.mVRA)));
		final String daa = String.format(df.format(estats.mDAA) + "|"
				+ String.format(df.format(estats.mDRA)));
		final Scalar color = new Scalar(255, 255, 255);
		Core.putText(img, edge_type, new Point(top_left.x, top_left.y + 10),
				Core.FONT_HERSHEY_PLAIN, 0.8, color);
		Core.putText(img, haa, new Point(top_left.x, top_left.y + 20),
				Core.FONT_HERSHEY_PLAIN, 0.8, color);
		Core.putText(img, vaa, new Point(top_left.x, top_left.y + 30),
				Core.FONT_HERSHEY_PLAIN, 0.8, color);
		Core.putText(img, daa, new Point(top_left.x, top_left.y + 40),
				Core.FONT_HERSHEY_PLAIN, 0.8, color);

	}
}
