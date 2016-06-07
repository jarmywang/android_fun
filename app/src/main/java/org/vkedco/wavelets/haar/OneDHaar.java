package org.vkedco.wavelets.haar;


/**
* ============================================================================
* @author Vladimir Kulyukin
*
* An implementation of 1D Ordered Fast Haar Wavelet Transform, Ordered Fast Inverse
* Haar Wavelet Transform, Inplace Fast Haar Wavelet Transform, and Inplace Fast Inverse
* Haar Wavelet Transform as specified in Ch. 01 of "Wavelets Made Easy" by Yves Nievergelt.
*
* More documentation at http://vkedco.blogspot.com/2014/07/1d-and-2d-haar-wavelet-transforms-in.html.
*
* Bugs to vladimir dot kulyukin at gmail dot com
* ============================================================================
*/

public class OneDHaar {

    public static void displaySample(double[] sample) {
        System.out.print("Sample: ");
        for (int i = 0; i < sample.length; i++) {
            System.out.print(sample[i] + " ");
        }
        System.out.println();
    }
    
    public static boolean isPowerOf2(int n) {
        if ( n < 1 )
            return false;
        else {
            double p_of_2 = (Math.log(n)/Math.log(2));
            return Math.abs(p_of_2 - (int)p_of_2) == 0;
        }
    }

    // compute in-place fast haar wavelet transform.
    public static void inPlaceFastHaarWaveletTransform(double[] sample) {
        if ( sample.length == 0 || sample.length == 1 ) return;
        if ( OneDHaar.isPowerOf2(sample.length) == false) return;
        final int num_sweeps = (int)(Math.log(sample.length)/Math.log(2));
        inPlaceFastHaarWaveletTransformForNumIters(sample, num_sweeps);
    }
    
    // apply in-place fast haar wavelet transform for num_sweeps sweeps.
    public static void inPlaceFastHaarWaveletTransformForNumIters(double[] sample, int num_iters) {
        if ( sample.length == 0 || sample.length == 1 ) return;
        if ( OneDHaar.isPowerOf2(sample.length) == false) return;
        int I = 1; // index increment
        int GAP_SIZE = 2; // number of elements b/w averages
        int NUM_SAMPLE_VALS = sample.length; // number of values in the sample
        final int n = (int) (Math.log(NUM_SAMPLE_VALS) / Math.log(2));
        if (num_iters < 1 || num_iters > n) return;
        double a = 0;
        double c = 0;
        for (int ITER_NUM = 1; ITER_NUM <= num_iters; ITER_NUM++) {
            NUM_SAMPLE_VALS /= 2;
            for (int K = 0; K < NUM_SAMPLE_VALS; K++) {
                a = (sample[GAP_SIZE * K] + sample[GAP_SIZE * K + I]) / 2;
                c = (sample[GAP_SIZE * K] - sample[GAP_SIZE * K + I]) / 2;
                sample[GAP_SIZE * K] = a;
                sample[GAP_SIZE * K + I] = c;
            }
            I = GAP_SIZE;
            GAP_SIZE *= 2;
        }
    }

    // do the n-th sweep of In-Place Fast Haar Wavelet Transform
    public static void doNthSweepOfInPlaceFastHaarWaveletTransform(double[] sample, int sweep_number) {
        if (sample.length % 2 != 0 || sample.length == 0) return;
        int I = (int)(Math.pow(2.0, sweep_number-1));
        int GAP_SIZE = (int) (Math.pow(2.0, sweep_number));
        int NUM_SAMPLE_VALS = sample.length;
        final int n = (int) (Math.log(NUM_SAMPLE_VALS) / Math.log(2));
        if (sweep_number < 1 || sweep_number > n) return;
        double a = 0;
        double c = 0;
        NUM_SAMPLE_VALS /= (int)(Math.pow(2.0, sweep_number));
        for (int K = 0; K < NUM_SAMPLE_VALS; K++) {
            a = (sample[GAP_SIZE * K] + sample[GAP_SIZE * K + I]) / 2;
            c = (sample[GAP_SIZE * K] - sample[GAP_SIZE * K + I]) / 2;
            sample[GAP_SIZE * K] = a;
            sample[GAP_SIZE * K + I] = c;
        }
    }

     public static void orderedFastHaarWaveletTransform(double[] sample) {
        final int n = sample.length;
        // if n is not an integral power of 2, then return
        if (n % 2 != 0) return;
        // compute the number of sweeps; e.g., if n = 8, then NUM_SWEEPS is 3.
        final int NUM_SWEEPS = (int) (Math.log(n) / Math.log(2.0));
        double acoeff, ccoeff;
        if (NUM_SWEEPS == 1) {
            acoeff = (sample[0] + sample[1]) / 2.0;
            ccoeff = (sample[0] - sample[1]) / 2.0;
            sample[0] = acoeff;
            sample[1] = ccoeff;
            return;
        }
        double[] acoeffs;
        double[] ccoeffs;
        for (int SWEEP_NUM = 1; SWEEP_NUM < NUM_SWEEPS; SWEEP_NUM++) {
            // size is the number of a-coefficients and c-coefficients at
            // sweep SWEEP_NUM. for example, if the sample has 8 elements;
            // at sweep 1, we have 4 a-coefficients and 4 c-coefficients;
            // at sweep 2, we have 2 a-coefficients and 2 c-coefficients;
            // at sweep 3, we have 1 a-coefficient and 1 c-coefficient.
            int size = (int) Math.pow(2.0, (double) (NUM_SWEEPS - SWEEP_NUM));
            acoeffs = new double[size]; // where we place a-coefficients
            ccoeffs = new double[size]; // where we place c-coefficients
            int ai = 0; // index over acoeffs
            int ci = 0; // index over ccoeffs
            // end is the index of the last a-coefficient in sample[] at
            // sweep SWEEP_NUM. For example, if NUM_SWEEPS = 3, then
            // at sweep 1, end = 2^{3-1+1} - 1 = 7
            // at sweep 2, end = 2^{3-2+1} - 1 = 3
            int end = ((int) Math.pow(2.0, (double) (NUM_SWEEPS - SWEEP_NUM + 1))) - 1;
            for (int i = 0; i <= end; i += 2) {
                acoeffs[ai++] = (sample[i] + sample[i + 1]) / 2.0;
                ccoeffs[ci++] = (sample[i] - sample[i + 1]) / 2.0;
            }
           
            // the following for-loop places the a-coeffs into the left half of the array
            // and c-coeffs into the right half of the array
            // for example, assume that the length of sample is 3 and NUM_SWEEPS = 3,
            // then at sweep 1, size = 4. Thus,
            // sample[0] = a^{2}_{0}, sample[0+4] = c^{2}_{0}
            // sample[1] = a^{2}_{1}, sample[1+4] = c^{2}_{1},
            // sample[2] = a^{2}_{2}, sample[2+4] = c^{2}_{c},
            // sample[3] = a^{2}_{3}, sample[3+4] = c^{2}_{3}
            // in other words,
            // sample[0], sample[1], sample[2], sample[3] are the 4 a-coefficients and
            // sample[4], sample[5], sample[6], sample[7] are the 4 c-coefficients
            // at sweep 2, size = 2. Thus,
            // sample[0] = a^{1}_{0}, sample[0+2] = c^{1}_{0}
            // sample[1] = a^{1}_{1}, sample[1+2] = c^{1}_{1}
            for (int i = 0; i < size; i++) {
                sample[i] = acoeffs[i];
                sample[i + size] = ccoeffs[i];
            }
        }
        // now we compute a^{0}_{0} and c^{0}_{0} at store them
        // in sample[0] and sample[1]
        acoeff = (sample[0] + sample[1]) / 2.0;
        ccoeff = (sample[0] - sample[1]) / 2.0;
        sample[0] = acoeff;
        sample[1] = ccoeff;
    }
    
    public static void orderedFastInverseHaarWaveletTransform(double[] sample) {
        int n = sample.length;
        if (n < 2 || n % 2 != 0) return;
        n = (int) (Math.log(n) / Math.log(2.0));
        double a0 = 0; double a1 = 0; double[] vals = null;
        int GAP = 0; int j = 0;
        for (int L = 1; L <= n; L++) {
            GAP = (int)(Math.pow(2.0, L-1));
            j = 0;
            vals = null;
            vals = new double[2*GAP];
            for(int i = 0; i < GAP; i++) {
                a0 = sample[i] + sample[GAP+i];
                a1 = sample[i] - sample[GAP+i];
                vals[j] = a0;
                vals[j+1] = a1;
                j += 2;
            }
            System.arraycopy(vals, 0, sample, 0, 2*GAP);
        }
    }
    
    public static void inPlaceFastInverseHaarWaveletTransform(double[] sample) {
        int n = sample.length;
        n = (int) (Math.log(n) / Math.log(2.0));
        int GAP_SIZE = (int) (Math.pow(2.0, n - 1));
        int JUMP = 2 * GAP_SIZE;
        int NUM_FREQS = 1;
        for (int SWEEP_NUM = n; SWEEP_NUM >= 1; SWEEP_NUM--) {
            for (int K = 0; K < NUM_FREQS; K++) {
                double aPlus = sample[JUMP * K] + sample[JUMP * K + GAP_SIZE];
                double aMinus = sample[JUMP * K] - sample[JUMP * K + GAP_SIZE];
                sample[JUMP * K] = aPlus;
                sample[JUMP * K + GAP_SIZE] = aMinus;
            }
            JUMP = GAP_SIZE;
            GAP_SIZE /= 2;
            NUM_FREQS *= 2;
        }
    }

    // num_iters should be log(n)/log(2) if in-place fast haar transform is to
    // be computed completely.
    public static void inPlaceFastInverseHaarWaveletTransformForNumIters(double[] sample, int num_iters) {
        int n = sample.length;
        if (n % 2 != 0 || n == 0) {
            return;
        }
        n = (int) (Math.log(n) / Math.log(2.0));
        if (num_iters < 1 || num_iters > n) {
            return;
        }
        //int GAP_SIZE = (int) (Math.pow(2.0, num_sweeps - 1));
        //int JUMP = 2 * GAP_SIZE;
        //int NUM_FREQS = 1;
        final int lower_bound = n - num_iters + 1;
        int GAP_SIZE = 0; int JUMP = 0; int NUM_FREQS = 0;
        for (int ITER_NUM = lower_bound; ITER_NUM <= n; ITER_NUM++) {
            GAP_SIZE = (int) (Math.pow(2.0, n - ITER_NUM));
            JUMP = 2 * GAP_SIZE;
            NUM_FREQS = (int)(Math.pow(2.0, ITER_NUM-1));
            for (int K = 0; K < NUM_FREQS; K++) {
                double aPlus = sample[JUMP * K] + sample[JUMP * K + GAP_SIZE];
                double aMinus = sample[JUMP * K] - sample[JUMP * K + GAP_SIZE];
                sample[JUMP * K] = aPlus;
                sample[JUMP * K + GAP_SIZE] = aMinus;
            }
        }
    }

    public static void doNthIterOfInPlaceFastInverseHaarWaveletTransform(double[] sample, int iter_number) {
        int n = sample.length;
        if (n % 2 != 0 || n == 0) {
            return;
        }
        n = (int) (Math.log(n) / Math.log(2.0));
        if (iter_number < 1 || iter_number > n) {
            return;
        }
        int GAP_SIZE = (int) (Math.pow(2.0, n - iter_number));
        int JUMP = 2 * GAP_SIZE;
        int NUM_FREQS = (int)(Math.pow(2.0, iter_number-1));
        for (int K = 0; K < NUM_FREQS; K++) {
            double aPlus = sample[JUMP * K] + sample[JUMP * K + GAP_SIZE];
            double aMinus = sample[JUMP * K] - sample[JUMP * K + GAP_SIZE];
            sample[JUMP * K] = aPlus;
            sample[JUMP * K + GAP_SIZE] = aMinus;
        }
    }
    
    // haar_transformed_sample is a sample to which the inplace haar wavelet transform
    // has been applied num_sweeps times. this method reconstructs the original sample in place
    // by applying fast inverse haar transform a given number of iterations.
    public static void reconstructSampleTransformedInPlaceForNumIters(double[] haar_transformed_sample, int num_iters) {
        int n = haar_transformed_sample.length;
        if (n % 2 != 0 || n == 0) {
            return;
        }
        n = (int) (Math.log(n) / Math.log(2.0));
        if (num_iters < 1 || num_iters > n) {
            return;
        }
        int GAP_SIZE = (int) (Math.pow(2.0, num_iters-1));
        int JUMP = 2 * GAP_SIZE;
        int NUM_FREQS = (int)(Math.pow(2.0, n - num_iters));
        for (int ITER_NUM = 1; ITER_NUM <= num_iters; ITER_NUM++) {
            for (int K = 0; K < NUM_FREQS; K++) {
                double aPlus = haar_transformed_sample[JUMP * K] + haar_transformed_sample[JUMP * K + GAP_SIZE];
                double aMinus = haar_transformed_sample[JUMP * K] - haar_transformed_sample[JUMP * K + GAP_SIZE];
                haar_transformed_sample[JUMP * K] = aPlus;
                haar_transformed_sample[JUMP * K + GAP_SIZE] = aMinus;
            }
            JUMP = GAP_SIZE;
            GAP_SIZE /= 2;
            NUM_FREQS *= 2;
        }
    }
    
    // Same as the method reconstructSampleTransformedInPlaceForNumIters but with console output messages
    // Each message displays the partially reconstructed array after each reoncstructive
    // sweep.
    public static void reconstructSampleTransformedInPlaceForNumItersWithOutput(double[] haar_transformed_sample, int num_iters) {
        int n = haar_transformed_sample.length;
        if (n % 2 != 0 || n == 0) {
            return;
        }
        n = (int) (Math.log(n) / Math.log(2.0));
        if (num_iters < 1 || num_iters > n) {
            return;
        }
        int GAP_SIZE = (int) (Math.pow(2.0, num_iters-1));
        int JUMP = 2 * GAP_SIZE;
        int NUM_FREQS = (int)(Math.pow(2.0, n - num_iters));
        System.out.print("Reconstruction Sweep 0: "); OneDHaar.displaySample(haar_transformed_sample);
        for (int ITER_NUM = 1; ITER_NUM <= num_iters; ITER_NUM++) {
            for (int K = 0; K < NUM_FREQS; K++) {
                double aPlus = haar_transformed_sample[JUMP * K] + haar_transformed_sample[JUMP * K + GAP_SIZE];
                double aMinus = haar_transformed_sample[JUMP * K] - haar_transformed_sample[JUMP * K + GAP_SIZE];
                haar_transformed_sample[JUMP * K] = aPlus;
                haar_transformed_sample[JUMP * K + GAP_SIZE] = aMinus;
            }
            System.out.print("Reconstruction Sweep " + ITER_NUM + ": "); OneDHaar.displaySample(haar_transformed_sample);
            JUMP = GAP_SIZE;
            GAP_SIZE /= 2;
            NUM_FREQS *= 2;
        }
    }
    
    // display ordered frequencies from lowest to highest in the ordered_sample
    // that has been obtained from the original by the ordered haar wavelet
    // transform.
    public static void displayOrderedFreqsFromOrderedHaar(double[] ordered_sample) {
        int n = ordered_sample.length;
        if ( n == 0 || n % 2 != 0 ) return;
        n = (int)(Math.log(n)/Math.log(2));
        System.out.println(ordered_sample[0]);
        int start = 1;
        int NUM_FREQS = 0;
        for(int sweep_num = 1; sweep_num <= n; sweep_num++) {
            NUM_FREQS = (int)(Math.pow(2.0, sweep_num-1));
            for(int i = start; i < start + NUM_FREQS; i++) {
                System.out.print(ordered_sample[i] + "\t");
            }
            start += NUM_FREQS;
            System.out.println();
        }
    }
    
    // display ordered frequences from lowest to highest in the sample
    // that has been obtained from the original by the in-place fast
    // haar wavelet transform
    public static void displayOrderedFreqsFromInPlaceHaar(double[] in_place_sample) {
        int n = in_place_sample.length;
        //System.out.println("N = " + n);
        if (n % 2 != 0 || n == 0) { return; }
        if ( n == 2 ) {
            System.out.println(in_place_sample[0]);
            System.out.println(in_place_sample[1]);
            return;
        }
        System.out.println(in_place_sample[0]);
        System.out.println(in_place_sample[n/2]);
        int START_INDEX = n/4;
        int NUM_FREQS = 2;
        while ( START_INDEX > 1 ) {
                int ODD = 1;
                for(int K = 0; K < NUM_FREQS; K++) {
                    System.out.print(in_place_sample[START_INDEX*ODD]+"\t");
                    ODD += 2;
                }
                System.out.println();
            START_INDEX /= 2;
            NUM_FREQS *= 2;
        }
        // START_INDEX must be one for the next loop to run
        assert(START_INDEX == 1);
        for(int i = 1; i < n; i += 2) {
            System.out.print(in_place_sample[i]+"\t");
        }
        System.out.println();
    }
    
    public static double reconstructSingleValueFromOrderedHaarWaveletTransform(double[] sample, int n, int k) {
        String binstr = Integer.toBinaryString(k);
        
        if ( binstr.length() < n ) {
            final int diff = n - binstr.length();
            for(int i = 0; i < diff; i++) {
                binstr = "0" + binstr;
            }
        }
        
        binstr = "0" + binstr;
        
        char[] binary = binstr.toCharArray();
        
        double s_k = sample[0];
        int I = (int) Math.pow(2.0, n-2);
        int J = (int) Math.pow(2.0, n-1);
       
        for(int L = 1; L <= n; L++) {
            if ( binary[L] == '0' ) {
                s_k = s_k + sample[J];
                J = J - I;
            }
            else if ( binary[L] == '1' ) {
                s_k = s_k - sample[J];
                J = J + I;
            }
            if ( L < n ) {
                I = I / 2;
            }
        }
        return s_k;
        
    }
}
