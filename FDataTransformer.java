package TreesBasedCorr;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

public class FDataTransformer {

    public FDataTransformer(){}

    public static byte[] toBytes(float [] data, float []binThresholds){
        byte[] bData=new byte[data.length];
        for(int i=0,iMax=data.length;i<iMax;i++){
            int start=0,end=binThresholds.length-1;
            if(data[i]>binThresholds[end-1])
                bData[i]=(byte)end;
            else if (data[i] <= binThresholds[start])
                bData[i]=(byte)start;
            else{
                while(start<end-1){
                    int mid=(end+start)/2;
                    if(data[i]>binThresholds[mid])
                        start=mid;
                    else
                        end=mid;
                }
                bData[i]=(byte)end;
            }
        }
        return bData;
    }

    public static float[][] quantization_simple(float [] data, int binCount){
        float[] dataSorted=Util.getArrayCopy(data);
        QSortAlg.sortASCE(dataSorted);
        float width=(data.length-1) / (binCount);

        float[][] bins = new float[3][binCount+1];
        for(int i=0;i<binCount;i++) {
            int thresholdInd = (int) (width * (1 + i));
            int binStart = Math.max(0, (int) (width * ( i-1)));
            bins[0][i] = dataSorted[thresholdInd]; // threshold & right edge
            bins[1][i] = Statistics.average(dataSorted, binStart, thresholdInd); // average values
            bins[2][i] =  thresholdInd - binStart; // length
        }

        bins[0][binCount] = Float.MAX_VALUE;
        bins[1][binCount] =Float.MAX_VALUE;;
        bins[2][binCount] = 0;

        return bins;
    }
}
