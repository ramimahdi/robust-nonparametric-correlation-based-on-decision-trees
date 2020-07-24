package TreesBasedCorr;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */


public class CorrTreeTrainConfig implements  Cloneable{
    public int maxSplitCount;
    public float sampleTrialFraction;
    public float errorReductionCut;
    public int minChild;
    public float aTag=1;
    public float bTag=-1;
    public float bWeight=1;
    public float randTreesPercent=0;

    public int[] candidTreeSize=new int[]{20};
    public int treeSizeSelectionIterationCount=20;

    public float testDataFrac = 0.3f;
    public boolean sampleTrainDataWithReplacement = true;

    public CorrTreeTrainConfig(int maxSplitCount, float sampleTrialFraction, float errorReductionCut, int minChild,
                               float aTag, float bTag, float bWeight,
                               int[] candidTreeSize, int treeSizeSelectionIterationCount, float randTreesPercent,
                               float testDataFrac, boolean sampleTrainDataWithReplacement){
        this.maxSplitCount=maxSplitCount;
        this.sampleTrialFraction=sampleTrialFraction;
        this.errorReductionCut=errorReductionCut;
        this.minChild=minChild;
        this.aTag = aTag;
        this.bTag = bTag;
        this.bWeight = bWeight;
        this.treeSizeSelectionIterationCount=treeSizeSelectionIterationCount;
        this.candidTreeSize=candidTreeSize;
        this.randTreesPercent=randTreesPercent;
        this.testDataFrac = testDataFrac;
        this.sampleTrainDataWithReplacement = sampleTrainDataWithReplacement;
    }

    @Override
   protected Object clone() throws CloneNotSupportedException {
       return super.clone();
   }
}
