package TreesBasedCorr;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */


public class DataObject {

    float[] tags;
    private float[][] rawFData;  // rows should features and columns are examples
    private float[][] rawFData_T; // rows are examples, columns are attributes
    byte[][] fProcessedData; // rows should features and columns are examples
    byte[] fEffectiveBinCount;
    float[][][] fBins;

    int[] sampleIndex;

    public void addNoise(){
        for(int i = 0; i <rawFData.length;i++){
            for(int j = 0; j <rawFData[i].length;j++) {
                float noise = (float)Math.random() * 1e-9F;
                rawFData[i][j] += noise;
            }
        }

        for(int i = 0; i <rawFData_T.length;i++){
            for(int j = 0; j <rawFData_T[i].length;j++) {
                float noise = (float)Math.random() * 1e-9F;
                rawFData_T[i][j] += noise;
            }
        }
    }

    public DataObject(float[][] fData){
        this.rawFData=fData;
        this.rawFData_T=Util.getTranspose(fData);

        this.fProcessedData=new byte[fData.length][];
        fEffectiveBinCount = new byte[fData.length];
        fBins = new float[fData.length][][];
        for(int i=0,iMax=fData.length;i<iMax;i++){
            fBins[i] = FDataTransformer.quantization_simple(fData[i], 90);
            this.fProcessedData[i] = FDataTransformer.toBytes(fData[i],fBins[i][0]);
            fEffectiveBinCount[i] = (byte)fBins[i][0].length;
        }

        sampleIndex = new int[rawFData[0].length];
        for(int i=0;i<sampleIndex.length;i++)
            sampleIndex[i]=i;
    }

    public DataObject(float[][] fData, float[] tags){
        this(fData);

        this.tags=tags;

    }

    public int[] getSampleIndex(){
            return sampleIndex;
    }

    public float[] getRawFData_bySample(int sampleIndex){
        return rawFData_T[sampleIndex];
    }

    // return one feature/dimenssion
    public float[] getRawFData(int featureInd){
        return rawFData[featureInd];
    }
}
