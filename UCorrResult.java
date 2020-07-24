package TreesBasedCorr;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

public class UCorrResult {
    float corrCoefficient;
    float correspondingNullDistSTD;
    float pValue;

    public UCorrResult(float corrCoefficient, float correspondingNullDistSTD, float pValue) {
        this.corrCoefficient = corrCoefficient;
        this.correspondingNullDistSTD = correspondingNullDistSTD;
        this.pValue = pValue;
    }
}
