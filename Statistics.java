package TreesBasedCorr;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

public final class Statistics {

    public static float average(float[] array, int start, int end) {
        return sum(array, start, end) / (end - start + 1);
    }

    public static int sum(int[] array, int start, int end) {
        int sum = 0;

        for (int i = start; i <= end; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static float sum(float[] array, int start, int end) {
        float sum = 0;

        for (int i = start; i <= end; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static int sum(int[] array, int length) {
        return sum(array, 0, length - 1);
    }

    public static int sum(int[] array) {
        return sum(array, array.length);
    }

    public static float maximum(float[] array) {
        return array[maximumInd(array)];
    }

    public static int maximumInd(float[] array) {
        int length = array.length;
        int ind = 0;
        if (length > 1) {
            for (int i = 1; i < length; i++) {
                if (array[i] > array[ind]) {
                    ind = i;
                }
            }
        }
        return ind;
    }

    public static int minimumInd(float[] array) {
        int length = array.length;
        int ind = 0;
        if (length > 1) {
            for (int i = 1; i < length; i++) {
                if (array[i] < array[ind]) {
                    ind = i;
                }
            }
        }

        return ind;
    }

    public static float minimum(float[] array) {
        return array[minimumInd(array)];
    }

    public static double getMannWhitneyU(double[] largeSample, double[] smallerSample) {
        double u1 = getMannWhitneyU_simple(largeSample, smallerSample);
        return u1;
    }

    public static double getMannWhitneyU_simple(double[] largeSample, double[] smallerSample) {
        double[] arr = new double[smallerSample.length + largeSample.length];
        for (int i = 0; i < smallerSample.length; i++)
            arr[i] = smallerSample[i];

        for (int i = 0; i < largeSample.length; i++)
            arr[i + smallerSample.length] = largeSample[i];

        int[] sortedInds = SortAlg2.getSortedInds(arr, false, true);

        double mwu = 0;
        double mwuB = 0;
        int sCnt = 0, lCnt = 0, i = -1;
        while (i < arr.length - 1) {
            int curLsCnt = 0, curSCnt = 0;
            do {
                i++;
                if (sortedInds[i] < smallerSample.length)
                    curSCnt++;
                else
                    curLsCnt++;
            } while (i < arr.length - 1 && arr[sortedInds[i]] == arr[sortedInds[i + 1]]);
            mwu += curLsCnt * sCnt;
            mwuB += curSCnt * lCnt;
            sCnt += curSCnt;
            lCnt += curLsCnt;
        }

        return (mwu - mwuB) / ((double) largeSample.length * smallerSample.length);
    }

    // normCDFTable for range(-4 to 4, delta = 0.1)
    static double[] normCDFTable = {
            0.00003, 0.00005, 0.00007, 0.00011, 0.00016, 0.00023, 0.00034, 0.00048,
            0.00069, 0.00097, 0.00135, 0.00187, 0.00256, 0.00347, 0.00466, 0.00621,
            0.00820, 0.01072, 0.01390, 0.01786, 0.02275, 0.02872, 0.03593, 0.04457,
            0.05480, 0.06681, 0.08076, 0.09680, 0.11507, 0.13567, 0.15866, 0.18406,
            0.21186, 0.24196, 0.27425, 0.30854, 0.34458, 0.38209, 0.42074, 0.46017,
            0.50000, 0.53983, 0.57926, 0.61791, 0.65542, 0.69146, 0.72575, 0.75804,
            0.78814, 0.81594, 0.84134, 0.86433, 0.88493, 0.90320, 0.91924, 0.93319,
            0.94520, 0.95543, 0.96407, 0.97128, 0.97725, 0.98214, 0.98610, 0.98928,
            0.99180, 0.99379, 0.99534, 0.99653, 0.99744, 0.99813, 0.99865, 0.99903,
            0.99931, 0.99952, 0.99966, 0.99977, 0.99984, 0.99989, 0.99993, 0.99995,
            0.99997
    };

    public static double normalCDF(double x) {
        if(x < -4) return 0;
        else if(x >= 4) return 1.0;
        else {
            double tmp = (x + 4) * 10;
            int x_pre_ind = (int)Math.floor(tmp);
            int x_post_ind = x_pre_ind + 1;

            return (normCDFTable[x_pre_ind] * (x_post_ind - tmp) + normCDFTable[x_post_ind]* (tmp - x_pre_ind) );
        }
    }

    public static double getNormalCDFLeftTailedPValue(double x, double std) {
        return 1.0 - normalCDF(x / std);
    }
}
