package TreesBasedCorr;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

public class IntegerBasedCorrTreeFNode {
    /*0 __ __   1__ __   2__ __   3____   4__ __   5__ __   6__ __   7__ __   8__ __
     * | 0| 1|  |__|  |  |  |__|  |____|  |__ __|  |__|__|  |__|__|  |    /|  |\    |
     * |_2|_3|  |__|__|  |__|__|  |____|  |__|__|  |__ __|  |__|__|  |  /  |  |  \  |
     *                                                               |/____|  |____\|
     * horizontal is dim1 , vertical is dim2. The origin (0,0) is at the left bottom corner
     * 7 & 8 are not tested
     * */
    static int[][][] allSplitTopplogies = new int[][][]{
            new int[][]{{0, 2}, {1, 3}}, new int[][]{{0}, {2}, {1, 3}},
            new int[][]{{0, 2}, {1}, {3}}, new int[][]{{0, 1}, {2, 3}},
            new int[][]{{0, 1}, {2}, {3}}, new int[][]{{0}, {1}, {2, 3}}, new int[][]{{0}, {1}, {2}, {3}}
    };
    float[] var1Bounds;
    float[] var2Bounds;
    int[][] trainASample;
    int[][] trainASample_T;
    float bSampleCount;
    float aSampleCount;
    float trainABError;
    float[] tag;
    boolean isLeaf = true;
    IntegerBasedCorrTreeFNode[] kids;
    int[] subBox2KidMap;
    float[] bestSplitSetting;
    float[] exactSplitPoint = null;
    int treeSplitIndex;

    public IntegerBasedCorrTreeFNode(float[] var1Bounds, float[] var2Bounds, int treeSplitIndex) {
        if (Float.isNaN(var1Bounds[0]) || Float.isNaN(var1Bounds[1]) || Float.isNaN(var2Bounds[0]) || Float.isNaN(var2Bounds[1]))
            var2Bounds[1] += 0;
        this.var1Bounds = var1Bounds;
        this.var2Bounds = var2Bounds;
        this.treeSplitIndex = treeSplitIndex;
    }

    public IntegerBasedCorrTreeFNode(float[] var1Bounds, float[] var2Bounds, int[][] trainASample,
                                     float bWeight, int treeSplitIndex) {
        this.var1Bounds = var1Bounds;
        this.var2Bounds = var2Bounds;
        this.treeSplitIndex = treeSplitIndex;
        setTrainASample(trainASample, bWeight);
    }

    private static float[] computeTagX(float aCnt, float bCnt, float bWeight) {
        float propA = aCnt / (aCnt + bCnt * bWeight);

        return new float[]{propA, aCnt, bCnt};
    }

    static int getContainingSubBox(float[] exactSplitPoint, int v1, int v2) {
        if (v1 <= exactSplitPoint[0]) {
            if (v2 > exactSplitPoint[1])
                return 0;
            else
                return 2;

        } else {
            if (v2 > exactSplitPoint[1])
                return 1;
            else
                return 3;
        }
    }

    public void setTrainASample(int[][] trainASample, float bWeight) {
        aSampleCount = 0;
        if (trainASample != null && trainASample.length > 0) {
            this.trainASample = trainASample;
            aSampleCount = this.trainASample.length;
            this.trainASample_T = Util.getTranspose(trainASample);
        }
        this.bSampleCount = (this.var1Bounds[1] - this.var1Bounds[0] + 1) * (this.var2Bounds[1] - this.var2Bounds[0] + 1);
        tag = computeTagX(aSampleCount, this.bSampleCount, bWeight);

        trainABError = getError(tag, aSampleCount, this.bSampleCount, bWeight);

        if (Float.isNaN(this.bSampleCount))
            this.bSampleCount += 0;

    }

    float getError(float[] tag, float aCnt, float bCnt, float bWeight) {

        float bCntW = bCnt * bWeight;
        float gini = 1 - tag[0] * tag[0] - (1 - tag[0]) * (1 - tag[0]);
        gini *= (bCntW + aCnt);

        return gini;
    }

    boolean isInBound(float v1, float v2) {
        return v1 >= var1Bounds[0] && v1 <= var1Bounds[1] &&
                v2 >= var2Bounds[0] && v2 <= var2Bounds[1];

    }

    Float getOptimalSplitScore() {
        if (this.bestSplitSetting != null)
            return this.bestSplitSetting[4];
        return null;
    }

    void computeOptimalSplitScore(CorrTree tree, Random rand, int trialCount, float bWeight,
                                  int mindChild, boolean isRandSplit, double minErrorReduction) {
        if (this.bestSplitSetting != null)
            return;

        float initialError = trainABError;
        if (isRandSplit)
            trialCount = trialCount < 5 ? trialCount : 5;

        this.bestSplitSetting = null;

        float[] bestSplitB = getOptimalSplit_BSampleBased(tree, rand, initialError, trialCount, bWeight, mindChild, isRandSplit, minErrorReduction);
        if (bestSplitB != null && (this.bestSplitSetting == null || bestSplitB[4] > this.bestSplitSetting[4])) {
            this.bestSplitSetting = bestSplitB;
        }

    }

    private float[] getOptimalSplit_BSampleBased(CorrTree tree, Random rand, float initialError, int trialCount,
                                                 float bWeight, int mindChild,
                                                 boolean isRandSplit, double minErrorReduction) {

        if ((var1Bounds[1] < var1Bounds[0] + 3) || (var2Bounds[1] < var2Bounds[0] + 3))
            return null;

        float[] bestSplit = null;
        int failCnt = 0;
        for (int i = 0; i < trialCount; i++) {

            int v1 = (int) this.var1Bounds[0] + 1 + rand.nextInt((int) (var1Bounds[1] - var1Bounds[0] - 1));
            int v2 = (int) this.var2Bounds[0] + 1 + rand.nextInt((int) (var2Bounds[1] - var2Bounds[0] - 1));

            int v1x = tree.v1SplitAdjustMap[v1];
            int v2x = tree.v2SplitAdjustMap[v2];

            if (v1x >= var1Bounds[1] || v1x <= var1Bounds[0] || v2x >= var2Bounds[1] || v2x <= var2Bounds[0]) {
                failCnt += 1;
                if (failCnt < 10)
                    i--;

                continue;
            }

            if (Float.isNaN(this.bSampleCount)) {
                this.bSampleCount += 0;
            }

            float[] splitSetting = getBestSplit(rand, initialError, v1x, v2x, bWeight,
                    mindChild, isRandSplit, minErrorReduction);

            if (splitSetting.length == 0)
                continue;

            if (bestSplit == null || splitSetting[1] > bestSplit[4]) {
                bestSplit = new float[]{-1, v1x, v2x, splitSetting[0], splitSetting[1]};
            }
        }
        return bestSplit;
    }

    float[][] getSubBoxBounds(int v1, int v2, int subBoxIndex) {
        float[] v1Bound = null;
        float[] v2Bound = null;
        if (subBoxIndex == 0 || subBoxIndex == 2)
            v1Bound = new float[]{var1Bounds[0], v1};
        else
            v1Bound = new float[]{v1 + 1, var1Bounds[1]};

        if (subBoxIndex == 2 || subBoxIndex == 3)
            v2Bound = new float[]{var2Bounds[0], v2};
        else
            v2Bound = new float[]{v2 + 1, var2Bounds[1]};

        return new float[][]{v1Bound, v2Bound};
    }

    float[][] getCombinedSubBoxBounds(int v1, int v2, int subBox1Index, int subBox2Index) {
        float[][] subBox1Bounds = getSubBoxBounds(v1, v2, subBox1Index);
        float[][] subBox2Bounds = getSubBoxBounds(v1, v2, subBox2Index);
        float[] v1Bound = new float[]{
                Math.min(subBox1Bounds[0][0], subBox2Bounds[0][0]),
                Math.max(subBox1Bounds[0][1], subBox2Bounds[0][1])
        };

        float[] v2Bound = new float[]{
                Math.min(subBox1Bounds[1][0], subBox2Bounds[1][0]),
                Math.max(subBox1Bounds[1][1], subBox2Bounds[1][1])
        };
        return new float[][]{v1Bound, v2Bound};
    }

    float[][] get4BoxStats(float v1, float v2) {
        float[] boxSampleACount = new float[4];
        float[] boxSampleBCount = new float[4];

        float dim1L1 = (v1 - var1Bounds[0] + 1);
        float dim1L2 = (var1Bounds[1] - v1);

        float dim2L1 = (v2 - var2Bounds[0] + 1);
        float dim2L2 = (var2Bounds[1] - v2);

        boxSampleBCount[0] = dim1L1 * dim2L2;
        boxSampleBCount[1] = dim1L2 * dim2L2;
        boxSampleBCount[2] = dim1L1 * dim2L1;
        boxSampleBCount[3] = dim1L2 * dim2L1;

        //Assuming trainASample are sorted according to dim1 in ASCE ordered
        boxSampleACount[2] = 0;
        boxSampleACount[0] = 0;
        int[] splitPsudoInd = Util.getPseudoIndex(v1, this.trainASample_T[0]);
        int splitInd = splitPsudoInd[0] + splitPsudoInd[1] - 1;

        for (int i = splitInd; i > -1; i--)
            if (trainASample_T[1][i] > v2)
                boxSampleACount[0]++;
            else
                boxSampleACount[2]++;

        for (int i = splitInd + 1; i < trainASample.length; i++)
            if (trainASample_T[1][i] > v2)
                boxSampleACount[1]++;
            else
                boxSampleACount[3]++;

        float[] bTrueFracs = new float[]{1, 1, 1, 1};

        return new float[][]{boxSampleACount, boxSampleBCount,
                new float[]{dim1L1, dim1L2, dim2L1, dim2L2}, bTrueFracs};
    }

    float[] getBestSplit(Random rand, float initialError, float v1, float v2,
                         float bWeight, int minChild, boolean isRandSplit, double minErrorReduction) {
        float[] splitError = new float[allSplitTopplogies.length];
        float[] splitMinWidth = new float[allSplitTopplogies.length];

        float[][] boxStats = get4BoxStats(v1, v2);

        float[] splitErrorReductionScore = new float[allSplitTopplogies.length];

        double minBoxSampleCnt = 9999;
        double minBoxSampleACnt = 9999;
        for (int i = 0; i < allSplitTopplogies.length; i++) {

            for (int j = 0; j < allSplitTopplogies[i].length; j++) {
                float aSampleCnt = 0, bSampleCnt = 0;
                for (int z = 0; z < allSplitTopplogies[i][j].length; z++) {
                    aSampleCnt += boxStats[0][allSplitTopplogies[i][j][z]];

                    bSampleCnt += boxStats[1][allSplitTopplogies[i][j][z]];
                }
                float[] tag = computeTagX(aSampleCnt, bSampleCnt, bWeight);
                float E = this.getError(tag, aSampleCnt, bSampleCnt, bWeight);

                splitError[i] += E;

                minBoxSampleCnt = Math.min(minBoxSampleCnt, aSampleCnt + bSampleCnt * bWeight);
                minBoxSampleACnt = Math.min(minBoxSampleACnt, aSampleCnt);
            }

            splitErrorReductionScore[i] = 2 * (initialError - splitError[i]) / allSplitTopplogies[i].length;

            if (isRandSplit)
                splitErrorReductionScore[i] *= 2;

            if (splitErrorReductionScore[i] < minErrorReduction) {
                splitErrorReductionScore[i] = -1;
            } else {
                if (isRandSplit) {

                    splitErrorReductionScore[i] = (float) ((0.1 * rand.nextDouble()) * Math.sqrt(this.bSampleCount + 1));
                }

                if (i == 0)
                    splitMinWidth[i] = Math.min(boxStats[2][0], boxStats[2][1]);
                else if (i == 3)
                    splitMinWidth[i] = Math.min(boxStats[2][2], boxStats[2][3]);
                else
                    splitMinWidth[i] = Statistics.minimum(boxStats[2]);

                splitErrorReductionScore[i] *= ((splitMinWidth[i] - minChild + 1));
                double subBoxMinEdgeLen = Math.max(splitMinWidth[i], Math.sqrt(minBoxSampleACnt));

                if (subBoxMinEdgeLen < minChild) {
                    splitErrorReductionScore[i] = -1 / ((float) subBoxMinEdgeLen + 1);
                }
            }
        }

        int bestSplitInd = Statistics.maximumInd(splitErrorReductionScore);
        if (splitErrorReductionScore[bestSplitInd] <= 0) {
            return new float[]{};
        }
        return new float[]{bestSplitInd, splitErrorReductionScore[bestSplitInd]};
    }

    float[] setExactSplitPoint(int splitSampleIndex) {
        if (splitSampleIndex > -1)
            exactSplitPoint = new float[]{trainASample[splitSampleIndex][0], trainASample[splitSampleIndex][1]};

        else
            exactSplitPoint = new float[]{this.bestSplitSetting[1], this.bestSplitSetting[2]};

        return exactSplitPoint;

    }

    public IntegerBasedCorrTreeFNode[] doSplit(float bWeight, int treeSplitIndex) {
        if (bestSplitSetting == null) {
            return new IntegerBasedCorrTreeFNode[]{};
        }
        setExactSplitPoint((int) bestSplitSetting[0]);
        subBox2KidMap = new int[4];
        int[][] splitToppolgy = allSplitTopplogies[(int) bestSplitSetting[3]];
        for (int i = 0; i < splitToppolgy.length; i++)
            for (int j = 0; j < splitToppolgy[i].length; j++)
                subBox2KidMap[splitToppolgy[i][j]] = i;

        float[][][] subBoxesBounds = new float[splitToppolgy.length][][];
        this.kids = new IntegerBasedCorrTreeFNode[subBoxesBounds.length];
        int[] kidSampleCnt = new int[splitToppolgy.length];
        for (int i = 0; i < splitToppolgy.length; i++) {
            if (splitToppolgy[i].length == 1) {
                subBoxesBounds[i] = getSubBoxBounds((int) bestSplitSetting[1], (int) bestSplitSetting[2], splitToppolgy[i][0]);
            } else {
                subBoxesBounds[i] = getCombinedSubBoxBounds((int) bestSplitSetting[1], (int) bestSplitSetting[2], splitToppolgy[i][0], splitToppolgy[i][1]);
            }

            this.kids[i] = new IntegerBasedCorrTreeFNode(subBoxesBounds[i][0], subBoxesBounds[i][1], treeSplitIndex);
            List<int[]> kidASample = new ArrayList<int[]>();
            for (int j = 0; j < trainASample.length; j++)
                if (this.kids[i].isInBound(trainASample[j][0], trainASample[j][1]))
                    kidASample.add(trainASample[j]);

            this.kids[i].setTrainASample(kidASample.toArray(new int[kidASample.size()][]), bWeight);
            kidSampleCnt[i] = kidASample.size();
        }

        if (Statistics.sum(kidSampleCnt) != trainASample.length)
            kidSampleCnt[0] += 0;

        this.isLeaf = false;
        return this.kids;
    }

    float[] getValue(int v1, int v2) {

        if (isLeaf)
            return tag;

        int ind = getContainingSubBox(this.exactSplitPoint, v1, v2);
        return kids[subBox2KidMap[ind]].getValue(v1, v2);
    }

    float[] getValue(int v1, int v2, int treeSplitLevel) {
        if (isLeaf || this.treeSplitIndex >= treeSplitLevel)
            return tag;

        int ind = getContainingSubBox(this.exactSplitPoint, v1, v2);
        IntegerBasedCorrTreeFNode childNode = kids[subBox2KidMap[ind]];

        if (childNode.treeSplitIndex <= treeSplitLevel)
            return childNode.getValue(v1, v2, treeSplitLevel);

        else
            return tag;
    }

    public void getAllLeafNodes(List<IntegerBasedCorrTreeFNode> leafNodes) {
        if (this.isLeaf) {
            leafNodes.add(this);
        } else {
            for (IntegerBasedCorrTreeFNode kid : kids)
                kid.getAllLeafNodes(leafNodes);
        }
    }

}
