package TreesBasedCorr;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

import java.util.*;

public class UCorrRandomForrest implements FitFunction {

    static int minTreeCountPerExample = 20;
    public Vector<CorrTree> roots;
    public List<int[]> testIndsList = new ArrayList<int[]>();
    public int bestTreeSize = 20;
    float leaveOutErrorAll = 0;
    float[] aTestScores;
    float[] aSampleTestCount;
    float[] aTestLastTreeInd;
    float[] bTestLastTreeInd;
    int[][] bgTestPairs;
    float[] bTestScores;
    float[] bSampleTestCount;

    public UCorrRandomForrest(final Random rand0, final DataObject dObj, final int[] samplesIndex, final int[] fAttributesInd,
                              final float sampleRate, final int forestSize, final CorrTreeTrainConfig trainConfig) {

        createPermutatedSampleTestExamples(/*samplesIndex.length*3*/ 10000, rand0, dObj, samplesIndex);

        aTestScores = new float[samplesIndex.length];
        aSampleTestCount = new float[samplesIndex.length];

        aTestLastTreeInd = new float[aTestScores.length];
        bTestLastTreeInd = new float[bTestScores.length];

        roots = new Vector<CorrTree>();

        final RandomTrainTestSampler sampler = new RandomTrainTestSampler(rand0, samplesIndex, 1 - sampleRate, forestSize);
        final int[] treesTrained = new int[]{0};

        for (int i = 0; i < forestSize; i++) {
            dObj.addNoise();

            // Sample next set of examples for training the next tree.

            int[][] subSampleInds_a = trainConfig.sampleTrainDataWithReplacement ? sampler.nextTrainTestWithReplacement_fast(trainConfig.testDataFrac)
                    : sampler.nextTrainTest_noReplacement_fast(trainConfig.testDataFrac);

            trainConfig.bWeight = 1f / subSampleInds_a[0].length;

            int[] subFeatInds = fAttributesInd;

            boolean[] sampleIsTest_a = new boolean[samplesIndex.length];
            for (int z = 0; z < subSampleInds_a[1].length; z++)
                sampleIsTest_a[subSampleInds_a[1][z]] = true;

            CorrTree root = CorrTree.train(rand0, dObj, subSampleInds_a[0], subSampleInds_a[0], subFeatInds,
                    trainConfig, rand0.nextDouble() < trainConfig.randTreesPercent);

            testIndsList.add(subSampleInds_a[1]);

            synchronized (roots) {
                roots.add(root);
                computeAllTestScores(root, dObj, subSampleInds_a[1],
                        roots.size() - 1, null, sampleIsTest_a, trainConfig);
            }
            treesTrained[0]++;
        }

        leaveOutErrorAll = getAllTestError_simple();
    }

    public int[] getTreesSizes() {
        int[] s = new int[roots.size()];

        for (int i = 0; i < roots.size(); i++) {
            List<IntegerBasedCorrTreeFNode> leafNodes = new ArrayList<IntegerBasedCorrTreeFNode>();
            roots.get(i).root.getAllLeafNodes(leafNodes);
            s[i] = leafNodes.size();
        }
        return s;
    }

    public float getAllTestError_simple() {

        Vector<Double> aScores = new Vector<Double>();
        double[] aCnt = new double[aTestScores.length];
        double[] bCnt = new double[bTestScores.length];

        for (int i = 0; i < aTestScores.length; i++) {
            aCnt[i] = aSampleTestCount[i];

            if (this.aSampleTestCount[i] > 0) {
                double ai_avg = aTestScores[i] / aSampleTestCount[i];
                aScores.add(ai_avg);
            }
        }

        Vector<Double> bScores = new Vector<Double>();

        for (int i = 0; i < bSampleTestCount.length; i++) {
            bCnt[i] = bSampleTestCount[i];

            if (this.bSampleTestCount[i] > 0) {
                double bi_avg = bTestScores[i] / bSampleTestCount[i];
                bScores.add(bi_avg);
            }
        }

        double[] aS = Util.toDoubleArray(aScores);
        double[] bS = Util.toDoubleArray(bScores);

        float mu = (float) Statistics.getMannWhitneyU(aS, bS);
        return mu;
    }

    public float smoothScore(float score0) {
        // no smoothness applied
        return score0;
    }

    public void createPermutatedSampleTestExamples(int maxSize, final Random rand0, final DataObject dObj,
                                                   final int[] samplesIndex) {
        int bgTestPairsSize = Math.min(maxSize, (samplesIndex.length - 1) * (samplesIndex.length - 1));
        bgTestPairs = new int[bgTestPairsSize][];
        int ind = 0;

        Hashtable<String, String> hash = new Hashtable<String, String>();

        while (ind < bgTestPairs.length) {
            int ind1 = rand0.nextInt(samplesIndex.length);
            int ind2 = rand0.nextInt(samplesIndex.length);

            float[] x1 = dObj.getRawFData_bySample(samplesIndex[ind1]);
            float[] x2 = dObj.getRawFData_bySample(samplesIndex[ind2]);

            // Skip permutated examples that are exact match to observed examples. Should not be common anyway.
            if (Math.abs(x1[0] - x2[0]) <= 0 || Math.abs(x1[1] - x2[1]) <= 0)
                continue;

            String k = samplesIndex[ind1] + "," + samplesIndex[ind2];
            if (hash.containsKey(k))
                continue;

            hash.put(k, k);
            bgTestPairs[ind] = new int[]{samplesIndex[ind1], samplesIndex[ind2]};
            ind++;
        }

        this.bTestScores = new float[bgTestPairs.length];
        this.bSampleTestCount = new float[bgTestPairs.length];
    }

    public float getValue(float[] x) {
        float avg = 0;
        for (CorrTree tn : roots) {
            float f = 0;
            if (bestTreeSize > -2)
                f = tn.getValue(x, bestTreeSize);
            else
                f = tn.getValue(x);

            avg += f;

        }
        avg = avg / roots.size();

        return avg;
    }

    public synchronized void computeAllTestScores(CorrTree newRoot, DataObject dObj, int[] testSampleInds_a,
                                                  int lastTrainedNodeIndex, float[][][] globalVTransform,
                                                  boolean[] sampleIsTest_a, CorrTreeTrainConfig trainConfig) {

        if (lastTrainedNodeIndex > trainConfig.treeSizeSelectionIterationCount || trainConfig.candidTreeSize.length == 1) {
            if (trainConfig.candidTreeSize.length == 1)
                this.bestTreeSize = trainConfig.candidTreeSize[0];

            float[] bestModel = new float[]{this.bestTreeSize, 0, 0, 0, 0, 0, 0, 0, 0};
            bestTreeSize = trainConfig.candidTreeSize[0];
            computeSelectTestScores(newRoot, bestModel, dObj, testSampleInds_a, globalVTransform, sampleIsTest_a);
        }
    }

    public synchronized float[] computeSelectTestScores(CorrTree newRoot, float[] bestModel, DataObject dObj,
                                                        int[] testSampleInds_a, float[][][] globalVTransform,
                                                        boolean[] sampleIsTest_a) {

        int a_r_ind = 0;
        int bg_r_ind = 0;

        for (int i = 0; i < bgTestPairs.length; i++) {

            boolean bRand = Math.random() > 0.5;
            if ((!sampleIsTest_a[bgTestPairs[i][0]] && bRand) || (!sampleIsTest_a[bgTestPairs[i][1]] && !bRand))
                continue;

            if (this.bSampleTestCount[i] > minTreeCountPerExample)
                continue;


            float[] x1 = dObj.getRawFData_bySample(bgTestPairs[i][0]);
            float[] x2 = dObj.getRawFData_bySample(bgTestPairs[i][1]);

            float[] x = new float[]{x1[0], x2[1]};
            float[] result = newRoot.getValueAll(x, (int) bestModel[0], globalVTransform);
            this.bTestScores[i] += smoothScore(result[bg_r_ind]);
            this.bSampleTestCount[i] += 1;
        }


        for (int i = 0, iMax = testSampleInds_a.length; i < iMax; i++) {
            int iInd = testSampleInds_a[i];

            if (this.aSampleTestCount[iInd] > minTreeCountPerExample)
                continue;

            float[] iS = dObj.getRawFData_bySample(iInd);
            float[] result = newRoot.getValueAll(iS, (int) bestModel[0], globalVTransform);
            aTestScores[iInd] += smoothScore(result[a_r_ind]);
            aSampleTestCount[iInd] += 1;
        }

        return new float[]{0, 1};
    }

    // MannWhitney U statistics based error function.
    private float getTrainLeaveOutError() {
        return this.leaveOutErrorAll;
    }

    public float getNullDistSTD()
    {
        float n = this.aTestScores.length;
        float m = this.bTestScores.length;
        return (float)Math.sqrt((1 + n + m * 1.5) / (3 * n * m));
    }

    private float getUCorr() {
        return getTrainLeaveOutError();
    }

    private float getApproximatePValue() {
        return (float)Statistics.getNormalCDFLeftTailedPValue(getUCorr(), getNullDistSTD());
    }

    /* Compute uCorr based on given params setting. */
    public static UCorrResult uCorr(double[][] data, int treesNum, int minLeafWidth, float randTreesFrac, int tree_min_splits) {
        float[] allTag = new float[data.length];
        DataObject dObj = new DataObject(Util.getTranspose(Util.double2Float(data)), allTag);

        float sampleRate = 1.0f;

        float bWeight = 1f / (dObj.getSampleIndex().length * sampleRate);
        int[] minWidths = new int[]{minLeafWidth};

        UCorrResult u_corr_result = uCorr(data,
                1, treesNum, bWeight,
                0.0f, minWidths, new int[]{tree_min_splits}, new float[]{0.1f}, randTreesFrac, true);

        return u_corr_result;
    }

    /* Compute uCorr based on default params setting. */
    public static UCorrResult uCorr(double[][] data) {
        int tree_min_splits = (int)Math.sqrt(data.length);

        if (tree_min_splits < 10)
            tree_min_splits = 10;
        else if (tree_min_splits > 100)
            tree_min_splits = 100;

        return uCorr(data, 100, (int)Math.max(6, 0.03 * data.length), 0.5f, tree_min_splits);
    }

    /* Compute uCorr starting with a simple grid search, then compute a final uCorr based on bes setting. */
    public static UCorrResult uCorr(double[][] data,
                               float sampleRate, int forestSize, float bWeight, float bTag,
                               int[] minWidth, int[] minSplits, float[] errorReductionMinCut,
                               float randTreesFrac, boolean withReplacement) {

        Random randi = new Random();
        float[] allTag = new float[data.length];
        DataObject dObj = new DataObject(Util.getTranspose(Util.double2Float(data)), allTag);

        double maxPerf = -9999;
        float[] parm = new float[3];

        parm[0] = minWidth[0];
        parm[1] = minSplits[0];
        parm[2] = errorReductionMinCut[0];

        if (minWidth.length != 1 || minSplits.length != 1 || errorReductionMinCut.length != 1)
            for (int i = 0; i < minWidth.length; i++) {
                for (int j = 0; j < minSplits.length; j++) {
                    for (int h = 0; h < errorReductionMinCut.length; h++) {
                        UCorrRandomForrest root = new UCorrRandomForrest(randi, dObj, dObj.getSampleIndex(),
                                new int[]{0, 1}, sampleRate, forestSize,
                                new CorrTreeTrainConfig(minSplits[j], 0.1f, errorReductionMinCut[h],
                                        minWidth[i], 1, bTag, bWeight, new int[]{minSplits[j]},
                                        0, randTreesFrac, 0.3f, withReplacement));

                        double perf_ij = root.getTrainLeaveOutError();
                        if (perf_ij > maxPerf * 1.001) {
                            maxPerf = perf_ij;
                            parm[0] = minWidth[i];
                            parm[1] = minSplits[j];
                            parm[2] = errorReductionMinCut[h];
                        }
                    }
                }
            }

        UCorrRandomForrest root = new UCorrRandomForrest(randi, dObj, dObj.getSampleIndex(),
                new int[]{0, 1}, sampleRate, forestSize,
                new CorrTreeTrainConfig((int) parm[1], 0.1f, parm[2], (int) parm[0], 1, bTag, bWeight,
                        new int[]{(int) parm[1]}, 0, randTreesFrac, 0.3f, withReplacement));

        return new UCorrResult(
                root.getUCorr(),
                root.getNullDistSTD(),
                root.getApproximatePValue()
        );
    }
}
