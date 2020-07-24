package TreesBasedCorr;

import java.util.*;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

public class CorrTree implements FitFunction {

    int[] tVar1, tVar2;

    float[][] v1Transform;
    float[][] v2Transform;

    int[] v1SplitAdjustMap;
    int[] v2SplitAdjustMap;

    boolean isVar1Reversed = false;
    boolean isVar2Reversed = false;

    IntegerBasedCorrTreeFNode root;

    public CorrTree(DataObject dObjx, int[] trainSamplesAIndex_a, int[] trainSamplesAIndex_b, int[] fAttributesInd) {
        learnPreProcessTransform(dObjx, trainSamplesAIndex_a, trainSamplesAIndex_b, fAttributesInd);
    }

    public static CorrTree train(Random rand, DataObject dObjx, int[] trainSamplesAIndex_a, int[] trainSamplesAIndex_b,
                                 int[] fAttributesInd, CorrTreeTrainConfig trainConfig, boolean isRandSplit) {
        CorrTree cTree = null;
        cTree = new CorrTree(dObjx, trainSamplesAIndex_a, trainSamplesAIndex_b, fAttributesInd);

        float[][] dataBox = cTree.getAllDataEnclosingBox(trainSamplesAIndex_b);

        int[] sortedInds = QSortAlg.getIndexesSortASCE(cTree.tVar1);

        int[][] trainAIntSample = new int[cTree.tVar1.length][];
        for (int i = 0; i < sortedInds.length; i++) {
            trainAIntSample[i] = new int[]{cTree.tVar1[sortedInds[i]], cTree.tVar2[sortedInds[i]]};
        }
        cTree.tVar1 = null;
        cTree.tVar2 = null;

        IntegerBasedCorrTreeFNode root = new IntegerBasedCorrTreeFNode(
                new float[]{dataBox[0][0], dataBox[0][1]},
                new float[]{dataBox[1][0], dataBox[1][1]},
                trainAIntSample, trainConfig.bWeight,
                -1);


        Comparator<IntegerBasedCorrTreeFNode> candidNodesComaroter = new Comparator<IntegerBasedCorrTreeFNode>() {
            @Override
            public int compare(IntegerBasedCorrTreeFNode o1, IntegerBasedCorrTreeFNode o2) {
                if (o1.getOptimalSplitScore() >= o2.getOptimalSplitScore())
                    return -1;
                else
                    return 1;
            }
        };

        double minErrorReduction = trainConfig.errorReductionCut;

        PriorityQueue<IntegerBasedCorrTreeFNode> candidSplitNodes = new PriorityQueue<IntegerBasedCorrTreeFNode>(100, candidNodesComaroter);
        root.computeOptimalSplitScore(cTree, rand, (int) Math.min(100, 4 + trainConfig.sampleTrialFraction * root.trainASample.length),
                trainConfig.bWeight, trainConfig.minChild, isRandSplit, 0);
        candidSplitNodes.add(root);
        int leafCount = 1;
        double minErrorReduction2 = minErrorReduction / 100;

        while (leafCount < trainConfig.maxSplitCount) {
            if (leafCount > 3) {
                minErrorReduction2 = minErrorReduction / 10;
            }
            if (leafCount > 7) {
                minErrorReduction2 = minErrorReduction;
            }

            IntegerBasedCorrTreeFNode node = candidSplitNodes.poll();
            if (node == null) {
                break;
            }

            IntegerBasedCorrTreeFNode[] kids = node.doSplit(trainConfig.bWeight, trainConfig.minChild);
            leafCount += (kids.length - 1);
            for (IntegerBasedCorrTreeFNode kid : kids) {
                if (kid.trainASample != null && (kid.trainASample.length + kid.bSampleCount * trainConfig.bWeight) > trainConfig.minChild) {
                    int trialCount = Math.min(100, 4 + (int) (trainConfig.sampleTrialFraction * kid.trainASample.length));
                    kid.computeOptimalSplitScore(cTree, rand, trialCount,
                            trainConfig.bWeight, trainConfig.minChild, isRandSplit, minErrorReduction2);
                    Float kidSplitScore = kid.getOptimalSplitScore();
                    if (kidSplitScore != null && kidSplitScore > trainConfig.errorReductionCut)
                        candidSplitNodes.add(kid);
                }
            }
        }

        cTree.root = root;
        cTree.tVar1 = null;
        cTree.tVar2 = null;

        return cTree;
    }

    static int getTransform(float[][] map, float v) {

        if (true)
            return toRankTransform(map, v);

        if (v <= map[0][0])
            return (int) map[0][1];
        else if (v >= map[map.length - 1][0])
            return (int) map[map.length - 1][1];

        int start = 0, end = map.length - 1;

        while (end > start + 1) {
            int mid = (end + start) / 2;

            if (v > map[mid][0])
                start = mid;
            else
                end = mid;

        }

        if (v - map[start][0] < map[end][0] - v)
            return (int) map[start][1];
        else
            return (int) map[end][1];

    }

    static Object[] learnRankTransform(float[] var1, float[] var2) {
        float[][] v1Transform = getRankTransformMap(var1);
        float[][] v2Transform = getRankTransformMap(var2);

        int[] tVar1 = toRankTransform(v1Transform, var1);
        int[] tVar2 = toRankTransform(v2Transform, var2);

        return new Object[]{v1Transform, v2Transform, new int[][]{tVar1, tVar2}};
    }

    public static float[][] getRankTransformMap(float[] vals) {
        float[] vCopy = Util.getArrayCopy(vals);

        Arrays.sort(vCopy);

        List<float[]> map = new ArrayList<float[]>();
        for (int i = 0; i < vCopy.length; ) {
            do {
                i++;
            }
            while (i < vCopy.length && vCopy[i - 1] == vCopy[i]);

            map.add(new float[]{vCopy[i - 1], i});
        }
        float[][] mapArr = map.toArray(new float[map.size()][]);
        return mapArr;
    }

    public static int[] toRankTransform(float[][] map, float[] v) {
        int[] iV = new int[v.length];
        for (int i = 0; i < iV.length; i++)
            iV[i] = toRankTransform(map, v[i]);

        return iV;
    }

    public static int toRankTransform(float[][] map, float v) {

        if (v <= map[0][0])
            return (int) map[0][1];
        else if (v > map[map.length - 2][0])
            return (int) map[map.length - 1][1];

        int start = 1, end = map.length - 2;

        while (end > start + 1) {
            int mid = (end + start) / 2;

            if (v > map[mid][0])
                start = mid + 1;
            else
                end = mid;

        }
        if (false)
            if (v > map[start][0] && v < map[end][0]) {
                if ((v - map[start][0]) < (map[end][0] - v))
                    return (int) map[start][1];
                else
                    return (int) map[end][1];
            }

        if (v <= map[start][0])
            return (int) map[start][1];
        else
            return (int) map[end][1];
    }

    public float getValue(float[] x) {

        int[] xt = preProcessTransform(x);

        return getValue(xt);
    }

    public float getValue(int[] x) {
        float score = this.root.getValue(x[0], x[1])[0];
        return score;
    }

    public float getValue(float[] x, int treeSplitLevel) {

        int[] xt = preProcessTransform(x);

        return getValue(xt, treeSplitLevel);
    }

    public float getValue(int[] x, int treeSplitLevel) {

        float score = this.root.getValue(x[0], x[1], treeSplitLevel)[0];
        return score;
    }

    public float[] getValueAll(float[] x, int treeLevel, float[][][] globalVTransform) {
        int v1 = 0;
        int v2 = 0;

        if (true) {

            int[] xt = preProcessTransform(x);
            v1 = xt[0];
            v2 = xt[1];
        } else if (globalVTransform != null) {
            v1 = getTransform(globalVTransform[0], x[0]);
            v2 = getTransform(globalVTransform[1], x[1]);

        } else {
            v1 = getTransform(v1Transform, x[0]);
            v2 = getTransform(v2Transform, x[1]);

        }

        float[] score = this.root.getValue(v1, v2, treeLevel);
        if (true) {
            return score;
        }
        if (score[0] > 0.5) {
            return new float[]{1, score[1], score[2], score[3], score[4]};
        } else {
            return new float[]{-1, score[1], score[2], score[3], score[4]};
        }

    }

    void learnPreProcessTransform(DataObject dObjx, int[] trainSamplesAIndex_a, int[] trainSamplesAIndex_b, int[] fAttributesInd) {
        float[] var1_b = Util.getItems(dObjx.getRawFData(fAttributesInd[0]), trainSamplesAIndex_b);
        float[] var2_b = Util.getItems(dObjx.getRawFData(fAttributesInd[1]), trainSamplesAIndex_b);

        float[] var1_a = Util.getItems(dObjx.getRawFData(fAttributesInd[0]), trainSamplesAIndex_a);
        float[] var2_a = Util.getItems(dObjx.getRawFData(fAttributesInd[1]), trainSamplesAIndex_a);

        isVar1Reversed = false; //Math.random()>0.5;
        isVar2Reversed = false; //Math.random()>0.5;
        if (isVar1Reversed) {
            for (int i = 0; i < var1_b.length; i++)
                var1_b[i] = -var1_b[i];

            for (int i = 0; i < var1_a.length; i++)
                var1_a[i] = -var1_a[i];
        }

        if (isVar2Reversed) {
            for (int i = 0; i < var2_b.length; i++)
                var2_b[i] = -var2_b[i];

            for (int i = 0; i < var2_a.length; i++)
                var2_a[i] = -var2_a[i];
        }

        Object[] tmp = learnRankTransform(var1_b, var2_b);
        this.v1Transform = (float[][]) tmp[0];
        this.v2Transform = (float[][]) tmp[1];

        this.tVar1 = toRankTransform(this.v1Transform, var1_a);
        this.tVar2 = toRankTransform(this.v2Transform, var2_a);

        v1SplitAdjustMap = new int[trainSamplesAIndex_b.length + 1];
        v2SplitAdjustMap = new int[trainSamplesAIndex_b.length + 1];

        v1SplitAdjustMap[0] = -99999;
        v2SplitAdjustMap[0] = -99999;

        for (int i = v1SplitAdjustMap.length - 1, iInd = this.v1Transform.length - 1; i > 0; i--) {
            while (iInd > 0 && this.v1Transform[iInd - 1][1] >= i)
                iInd--;

            v1SplitAdjustMap[i] = (int) this.v1Transform[iInd][1];
        }

        for (int i = v2SplitAdjustMap.length - 1, iInd = this.v2Transform.length - 1; i > 0; i--) {
            while (iInd > 0 && this.v2Transform[iInd - 1][1] >= i)
                iInd--;

            v2SplitAdjustMap[i] = (int) this.v2Transform[iInd][1];
        }

    }

    public int[] preProcessTransform(float[] v) {
        float v1 = v[0];
        if (isVar1Reversed)
            v1 = -v1;
        float v2 = v[1];
        if (isVar2Reversed)
            v2 = -v2;

        int v1t = toRankTransform(v1Transform, v1);
        int v2t = toRankTransform(v2Transform, v2);
        return new int[]{v1t, v2t};
    }

    public float[][] getAllDataEnclosingBox(int[] trainSamplesAIndex) {
        return new float[][]{{1, trainSamplesAIndex.length}, {1, trainSamplesAIndex.length}};
    }

}
