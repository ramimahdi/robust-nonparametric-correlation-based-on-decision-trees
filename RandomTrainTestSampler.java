package TreesBasedCorr;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

import java.util.ArrayList;
import java.util.Random;

public class RandomTrainTestSampler {
    int[] allSampleInds;
    float[] allSampleTestTimes;
    int maxSamplingCountPerSample;
    float testDataFraction;
    private Random rand;

    public RandomTrainTestSampler(Random rand,int[] allSampleInds, float testDataFraction, int samplingCount){
        this.rand = rand;
        this.allSampleInds = allSampleInds;
        this.testDataFraction = testDataFraction;
        this.allSampleTestTimes = new float[allSampleInds.length];


        this.maxSamplingCountPerSample = (int)( 2 + samplingCount * testDataFraction );
    }

    public synchronized int[][] nextTrainTestWithReplacement_fast(float testDataFrac){
        int n = allSampleInds.length;

        ArrayList<Integer> trainList = new ArrayList<Integer>();
        ArrayList<Integer> testList = new ArrayList<Integer>();

        int[] rInds = Util.getRandomIndex(rand, n);
        int test_n = (int)(n * testDataFrac);
        for(int i=0;i<n;i++){
            int ind = rInds[i];
            if(i < test_n)
                testList.add(allSampleInds[ind]);
            else
                trainList.add(allSampleInds[ind]);
        }
        int n0=trainList.size();
        while(trainList.size()<n){
            int ind = rand.nextInt(n0);
            trainList.add(trainList.get(ind).intValue()); //make a copy
        }
        return new int[][]{Util.toIntArray(trainList), Util.toIntArray(testList)};
    }

    public synchronized int[][] nextTrainTest_noReplacement_fast(float testFraction){

        ArrayList<Integer> trainList = new ArrayList<Integer>();
        ArrayList<Integer> testList = new ArrayList<Integer>();

        for(int i=0, iMax=allSampleInds.length;i<iMax;i++)
            trainList.add(i);

        int testSizeTmp = (int)(testFraction * allSampleInds.length);
        while(testList.size() < testSizeTmp){
            int ind = rand.nextInt(trainList.size());
            testList.add(trainList.remove(ind));
        }
        return new int[][]{Util.toIntArray(trainList), Util.toIntArray(testList)};
    }
}
