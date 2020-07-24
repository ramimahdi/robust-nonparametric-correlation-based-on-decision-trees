package TreesBasedCorr;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

public class SortAlg2 {

    static int[] getSortedInds(double[] vals, boolean isReversed, boolean isShuffle){

        double[] vals2 = vals;
        Integer[] idx0 =  makeIndex(vals.length);
        if(isShuffle){
            vals2 = Util.getArrayCopy(vals);
            Random r=new Random();
            for(int i=0;i<vals2.length;i++){
                int i2 = i +r.nextInt(vals2.length - i);
                double vi = vals2[i];
                vals2[i] = vals2[i2];
                vals2[i2] = vi;
                Integer indi = idx0[i];
                idx0[i]=idx0[i2];
                idx0[i2] = indi;
            }
        }

        final Integer[] idx = makeIndex(vals.length) ;
        final double[] data = vals2;

        for(int i=0;i<vals2.length;i++){
            double x = idx[i] - data[i];
            if(x!=0)
                x+=0;
        }

        if(isReversed)
            Arrays.sort(idx, new Comparator<Integer>() {
                @Override public int compare(final Integer o1, final Integer o2) {
                    return Double.compare(data[o2], data[o1]);
                }
            });
        else
            Arrays.sort(idx, new Comparator<Integer>() {
                @Override public int compare(final Integer o1, final Integer o2) {
                    return Double.compare(data[o1], data[o2]);
                }
            });

        int[] inds = new int[idx.length];
        for(int i=0;i<inds.length;i++)
            inds[i] = idx0[idx[i]];

        return inds;
    }
    static Integer[] makeIndex(int len){
        Integer[] tmp=new Integer[len];
        for(int i=0;i<len;i++)
            tmp[i]=i;

        return tmp;
    }

}
