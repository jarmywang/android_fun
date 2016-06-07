package com.example.user.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.vkedco.opencv.haar.TwoDMatHaar;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * Created by Wang on 2016/4/26.
 */
public class OpencvBlurActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java");
    }

    public static final String ROOT_PATH = "/Download/images/";
    String IMG_BLUR = "b.png";
    String IMG_NO_BLUR = "nob.jpg";
    String IMG = "image_001.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = new TextView(this);
        view.setText("OpencvBlurActivity");
        setContentView(view);

//        Imgproc.cvtColor(mat, mat2, Imgproc.COLOR_BGR2GRAY);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 13; i++) {
                    System.out.println("\n=====================================");
                    String img_path = Environment.getExternalStorageDirectory().getPath() + ROOT_PATH + "image_" + String.format("%03d", i) + ".png";
//                    System.out.println("img_path=" + img_path);

//                    Mat mat = Highgui.imread(img_path);
//                    System.out.println("mat=" + mat);
//                    HaarBlur.estimateImageBlur(mat, 1.5, 0.001); // 可能是这里错了，这个毫无参考性
//                    System.out.println("HaarBlur..BLUR_EXTENT_" + i + " = " + HaarBlur.getBlurExtent());
//                    System.out.println("HaarBlur..IS BLURRED_" + i + " = " + HaarBlur.getIsBlurred());

//                    System.out.println("test_edge_detection=" + test_edge_detection("image_" + String.format("%03d", i) + ".png", 64, 5)); // 较靠谱，第13张为57，其他10以内

                    opencvanswers(img_path); // 模拟器打印到opencvanswers..000就不往下了
                }
            }
        }).start();

    }

    private void opencvanswers(String imgPath){ // http://answers.opencv.org/question/16927/detect-if-image-is-blurry/
        System.out.println("opencvanswers..imgPath=" + imgPath);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inDither = true;
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeFile(imgPath);
        System.out.println("opencvanswers..image.w="+image.getWidth()+",image.h="+image.getHeight());
        int l = CvType.CV_8UC1; //8-bit grey scale image
        Mat matImage = new Mat();
        Utils.bitmapToMat(image, matImage);
//        System.out.println("opencvanswers..000");
        Mat matImageGrey = new Mat();
        Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY);

//        System.out.println("opencvanswers..111");
        Bitmap destImage;
        destImage = Bitmap.createBitmap(image);
        Mat dst2 = new Mat();
        Utils.bitmapToMat(destImage, dst2);
//        System.out.println("opencvanswers..222");
//        System.out.println("opencvanswers..destImage.w=" + destImage.getWidth() + ",destImage.h=" + destImage.getHeight());
        Mat laplacianImage = new Mat();
        dst2.convertTo(laplacianImage, l);
        Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U);
        Mat laplacianImage8bit = new Mat();
        laplacianImage.convertTo(laplacianImage8bit, l);
//        System.out.println("opencvanswers..333");

        Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(),
                laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(laplacianImage8bit, bmp);
//        System.out.println("opencvanswers..bmp.w=" + bmp.getWidth() + ",bmp.h=" + bmp.getHeight());
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(),
                bmp.getHeight());

        int maxLap = -16777216;
//        System.out.println("opencvanswers..pixels.length="+pixels.length); // 即宽*高
        for (int i = 0; i < pixels.length; i++) {
//            System.out.println("opencvanswers..pixels["+i+"]="+pixels[i]); // 打印太多
            if (pixels[i] > maxLap)
                maxLap = pixels[i];
        }

        int soglia = -6118750;
        System.out.println("opencvanswers..maxLap="+maxLap);
        if (maxLap <= soglia) {
            System.out.println("opencvanswers.."+imgPath.split("/")[imgPath.split("/").length-1]+" is blur image");
        }
        System.out.println("==============================================\n");
    }

    public static String test_edge_detection(String img_name, int N, int n) {
        String img_path = Environment.getExternalStorageDirectory().getPath() + ROOT_PATH + img_name;
        System.out.println("img_path=" + img_path);
        Mat mat = Highgui.imread(img_path);
        ArrayList<int[]> edges = TwoDMatHaar.detectEdges(img_path, mat, N, n,
                10.0, 10.0, 10.0);
        ArrayList<Integer> no_elements = new ArrayList<>();
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
            return ans;
        } else {
            ans = img_name + "	Blur Image " + S;
            System.out.println(img_name + "	Blur Image " + S);
            return ans;
        }
    }

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
        return count;
    }

    private static void DFS(int[] e, ArrayList<int[]> edges, int[] edges_flag,
                            ArrayList<Integer> no_elements, int i, int cluster_number) {
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

    public static boolean overlap(int[] e1, int[] e2) {
        ArrayList<int[]> e1point = new ArrayList<>();
        e1point.add(new int[]{e1[1], e1[0]});
        e1point.add(new int[]{e1[1] + 64, e1[0]});
        e1point.add(new int[]{e1[1], e1[0] + 64});
        e1point.add(new int[]{e1[1] + 64, e1[0] + 64});

        ArrayList<int[]> e2point = new ArrayList<>();

        e2point.add(new int[]{e2[1], e2[0]});
        e2point.add(new int[]{e2[1] + 64, e2[0]});
        e2point.add(new int[]{e2[1], e2[0] + 64});
        e2point.add(new int[]{e2[1] + 64, e2[0] + 64});

        for (int[] e1p : e1point) {
            for (int[] e2p : e2point) {
                if (e1p[0] == e2p[0] && e1p[1] == e2p[1]) {
                    return true;
                }
            }
        }

        return false;

    }

}
