package org.vkedco.opencv.haar;

/***************************************************************
 *
 * Given three thresholds: Theta_HAA, Theta_VAA, Theta_DAA
 * There are the following stats computed for each window N x N,
 * where N = 2^n: HAA, VAA, DAA.
 * - If HAA >= Theta_HAA and VAA < Theta_VAA and DAA < Theta_DAA,
 *   then it a vertical edge
 * - If VAA >= Theta_VAA and HAA < Theta_HAA and DAA < Theta_DAA,
 *   then it is a horizontal edge
 * - If (HAA >= Theta_HAA and VAA >= Theta_VAA) or DAA >= Theta_DAA {
 *     then it is a diagonal edge;
 *     if ( DRA < 0 ) then edge is from NE to SW
 *     if ( DRA > 0 ) then edge is from NW to SE  
 * }
 *
 * For now, we assume we iterate until the window is 2 x 2.
 *
 ***************************************************************
 */

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.vkedco.wavelets.haar.TwoDHaar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class TwoDMatHaarTests {
	static String HOME = "";
	static String s = "blur";
	static final String IMG_PATH = "/home/usu/ocr/" + s + "/";
	static final String IMG_PATH2 = "/home/usu/ocr/" + s + "/";

	static {
		HOME = System.getProperty("user.home");
		System.load(HOME + "\\Documents\\Tencent Files\\460697251\\FileRecv\\OpenCV-2.4.9-android-sdk\\sdk\\native\\libs\\x86\\libopencv_java.so");
	}

	public static void test_ordered_haar(Mat mat, int n, int num_steps_forward,
										 int num_steps_backward) {
		double[][] pixels = TwoDMatHaar.get2DPixelArrayFromMat(mat);
		TwoDHaar.displaySample(pixels, pixels[0].length, 0);
		pixels = null;
		ArrayList<double[][]> transform = TwoDMatHaar
				.orderedFastHaarWaveletTransformForNumIters(mat, n,
						num_steps_forward);
		for (double[][] ary : transform) {
			int len = ary[0].length;
			System.out.println("dim == " + len);
			TwoDHaar.displaySample(ary, len, 0);
		}
	}

	public static void test_01(String img_name) {
		Mat mat = Highgui.imread(IMG_PATH + img_name);
		HaarBlur.estimateImageBlur(mat, 1.5, 0.001);
		System.out.println("BLUR_EXTENT = " + HaarBlur.getBlurExtent());
		System.out.println("IS BLURRED  = " + HaarBlur.getIsBlurred());
		// test_ordered_haar(mat, 4, 2, 2);
	}

	public static void test_ordered_image_haar(String img_name, int x, int y,
											   int size, int num_steps_forward) {
		System.out.println("test_ordered_image_haar");
		Mat mat = Highgui.imread(IMG_PATH + img_name);
		System.out.println("mat.size = " + mat.rows() + ", " + mat.cols());
		double[][] pixels = TwoDMatHaar.get2DPixelArrayFromMatAt(mat, x, y,
				size);
		System.out.println("test_ordered_image_haar check 01");
		System.out.println("pixels' num_rows = " + pixels.length);
		System.out.println("pixels' num_cols = " + pixels[0].length);

		TwoDHaar.displaySample(pixels, pixels[0].length, 0);
		int n = (int) (Math.log(pixels[0].length) / Math.log(2.0));
		ArrayList<double[][]> transform = TwoDHaar
				.orderedFastHaarWaveletTransformForNumIters(pixels, n,
						num_steps_forward);
		transform = null;
	}

	public static String test_edge_detection(String img_name, int N, int n) {
		String img_path = IMG_PATH2 + img_name;
		Mat mat = Highgui.imread(img_path);
		Mat mat2 = Highgui.imread(img_path);
		// System.out.println("mat.size = " + mat.rows() + ", " + mat.cols());
		ArrayList<int[]> edges = TwoDMatHaar.detectEdges(img_path, mat, N, n,
				10.0, 10.0, 10.0);
		// System.out.println("Edges start here : ");
		// for(int[] e: edges) {
		// System.out.println("Edge in " + e[0] + ", " + e[1] + " of size " +
		// 64);
		// }

		// Finding clusters and total area of clusters----------------
		// @author Sarat Kiran Andhavarapu
		ArrayList<Integer> no_elements = new ArrayList<Integer>();
		int no_clusters = 0, max_count_clusters = 0;
		int cluster_count = cluster_count(edges, img_name, N, no_elements);

		for (int j = 0; j < no_elements.size(); j++) {
			if (no_elements.get(j) - 1 > max_count_clusters) {
				max_count_clusters = no_elements.get(j) - 1;
			}
			no_clusters += no_elements.get(j) - 1;
		}
		System.out.println("No of clusters : " + cluster_count);
		int mat_size = mat.rows() * mat.cols();
		int cluster_size = no_clusters * N * N;

		System.out.println("Matrix size : " + mat_size + " Cluster size : "
				+ cluster_size);
		float sharp_percentage = (float) cluster_size / mat_size;
		String S;
		S = "Percentage of sharp image : " + sharp_percentage * 100.0
				+ " Max number of squares in the cluster : "
				+ max_count_clusters;
		System.out.println(S);
		String ans;
		if (sharp_percentage * 100.0 > 19.00 && max_count_clusters >= 15) {
			ans = img_name + "	Sharp Image " + S;
			System.out.println(img_name + "	Sharp Image " + S);

			// String img_path_sharp =
			// "/home/usu/Desktop/images/sharp_images_final/" + img_name;
			// Highgui.imwrite(img_path_sharp, mat2);
			return ans;
		} else {
			ans = img_name + "	Blur Image " + S;
			System.out.println(img_name + "	Blur Image " + S);
			return ans;
		}
	}
	//--------------------------------------------------------------------

	//Find the number of clusters in a image
	// @author Sarat Kiran Andhavarapu
	public static int cluster_count(ArrayList<int[]> edges, String img_name,
									int N, ArrayList<Integer> no_elements) {
		int[] edges_flag = new int[edges.size()];
		Arrays.fill(edges_flag, 0);
		int count = 0;
		int i = -1;

		int cluster_number = 1;
		for (int[] e : edges) {
			int index;
			index = edges.indexOf(e);
			if (edges_flag[index] == 0) {
				no_elements.add(1);
				i++;
				DFS(e, edges, edges_flag, no_elements, i, cluster_number);
				cluster_number++;
				count++;
			}

		}
		// ---------------------------- FINDING CENTROIDS---------------------------
		// @author Sarat Kiran Andhavarapu
		/*
		 * for (int j =0; j< no_elements.size(); j++){
		 * System.out.println(no_elements.get(j)-1); } ArrayList<double[]>
		 * centroids = new ArrayList<double[]>(); int k = 1; while ( k <= count)
		 * { double centroidX = 0, centroidY = 0; int cluster_count = 0; for
		 * (int [] e_centroid: edges) { int index_c = edges.indexOf(e_centroid);
		 * if (edges_flag[index_c] == k) { cluster_count++; centroidX +=
		 * e_centroid[1]+32; centroidY += e_centroid[0]+32; } } double[]
		 * new_point = new double[2]; new_point[0] = centroidY/cluster_count;
		 * new_point[1] = centroidX/cluster_count; centroids.add(new_point);
		 * k++; } String img_path = IMG_PATH2 + img_name + "_" + N +
		 * "_Edges_.JPG"; Mat mat = Highgui.imread(img_path); Scalar sc = new
		 * Scalar(0, 0, 255); //Point bot_right = new Point(10,10); //Point
		 * bot_left = new Point (10,200);
		 * 
		 * 
		 * for (double[] c:centroids) { Point bot_right = new
		 * Point(c[1]-5,c[0]); Point bot_left = new Point (c[1]+5,c[0]); Point
		 * bot_up = new Point (c[1],c[0]-5); Point bot_down = new
		 * Point(c[1],c[0]+5); // System.out.println("X AXIS :" + c[1] +
		 * " Y AXIS :" + c[0]); Core.circle(mat, new Point(c[1],c[0]), 2, sc,2);
		 * Core.line(mat, bot_right, bot_left, sc); Core.line(mat, bot_up,
		 * bot_down, sc); }
		 */
		// Highgui.imwrite(img_path + "_"+ "Centroid.JPG", mat);
		// --------------------------------------------------------------------------
		// Mat mat_test = Highgui.imread(img_path);
		// Mat mat_test_img = new Mat(mat_test.size(),CvType.CV_8UC3);
		// Mat mat_test_x = new Mat(mat_test.size(),)

		return count;
	}

	//Finding connected tiles in a cluster using Depth First Search
	// @author Sarat Kiran Andhavarapu
	private static void DFS(int[] e, ArrayList<int[]> edges, int[] edges_flag,
							ArrayList<Integer> no_elements, int i, int cluster_number) {
		// TODO Auto-generated method stub
		int index_dfs = edges.indexOf(e);
		edges_flag[index_dfs] = cluster_number;
		int present_value = no_elements.get(i);
		no_elements.set(i, present_value + 1);
		for (int[] e_dfs : edges) {
			int index_dfs_inside = edges.indexOf(e_dfs);
			if (overlap(e, e_dfs) && edges_flag[index_dfs_inside] == 0) {
				DFS(e_dfs, edges, edges_flag, no_elements, i, cluster_number);

			}
		}

	}

	// Finding if the tiles overlap with each other
	// @author Sarat Kiran Andhavarapu
	public static boolean overlap(int[] e1, int[] e2) {
		ArrayList<int[]> e1point = new ArrayList<int[]>();
		e1point.add(new int[] { e1[1], e1[0] });
		e1point.add(new int[] { e1[1] + 64, e1[0] });
		e1point.add(new int[] { e1[1], e1[0] + 64 });
		e1point.add(new int[] { e1[1] + 64, e1[0] + 64 });

		ArrayList<int[]> e2point = new ArrayList<int[]>();

		e2point.add(new int[] { e2[1], e2[0] });
		e2point.add(new int[] { e2[1] + 64, e2[0] });
		e2point.add(new int[] { e2[1], e2[0] + 64 });
		e2point.add(new int[] { e2[1] + 64, e2[0] + 64 });

		for (int[] e1p : e1point) {
			for (int[] e2p : e2point) {
				if (e1p[0] == e2p[0] && e1p[1] == e2p[1]) {
					return true;
				}
			}
		}

		return false;

	}

	public static void main(String[] args) throws IOException {
		// test_ordered_image_haar("blur_edge_0001.jpg", 0, 0, 32, 2);
		// test_ordered_image_haar("blur_edge_0006.jpg", 0, 10, 16, 3);
		// test_ordered_image_haar("non_blur_edge_0002.jpg", 4, 4, 16, 3);
		// test_ordered_image_haar("blur_edge_0001.jpg", 10, 10, 16, 3);
		// test_ordered_image_haar("non_blur_edge_0003.jpg", 5, 5, 16, 3);
		// test_ordered_image_haar("no_edge_0001.jpg", 0, 0, 16, 3);
		// test_ordered_image_haar("no_edge_0002.jpg", 0, 0, 16, 3);
		// test_ordered_image_haar("non_blur_edge_0004.jpg", 5, 5, 16, 3);
		// test_ordered_image_haar("non_blur_edge_0007.jpg", 5, 5, 16, 3);
		// test_ordered_image_haar("blur_edge_0002.jpg", 5, 5, 16, 3);
		// test_ordered_image_haar("blur_edge_0004.jpg", 0, 0, 16, 3);
		// test_ordered_image_haar("blur_edge_0008.jpg", 15, 15, 16, 3);
		// test_ordered_image_haar("blur_edge_0009.jpg", 10, 10, 16, 3);

		// -------------- TESTING SINGLE IMAGE ---------------
		// System.out.println(test_edge_detection("11.jpg", 1024, 10));
		// ---------------------------------------------------

		// ---------------- TESTING ALL THE IMAGES IN A FOLDER ---------------------
		//@author Sarat Kiran Andhavarapu

		FileWriter filer = new FileWriter("/home/usu/Desktop/images/"
				+ "ocr_detection.txt");
		PrintWriter out = new PrintWriter(filer);

		File folder = new File("/home/usu/ocr/" + s + "/");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {
				out.println(test_edge_detection(file.getName(), 64, 5));

			}
		}
		out.close();

		// ------------------------------------------------------------------------------

		// test_edge_detection("00099.JPG.JPG", 32, 4);
		// test_edge_detection("00099.JPG.JPG", 128, 6);
		// test_ordered_image_haar("no_edge_0001.jpg", 0, 0, 16, 3);
		// test_01("Blur.jpg");
		// HaarBlur.displayImageStats();
		// HaarBlur.displayEmaxes();
	}

}
