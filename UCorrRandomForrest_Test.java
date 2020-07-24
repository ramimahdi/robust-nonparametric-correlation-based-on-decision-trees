package TreesBasedCorr;

import java.util.Random;

public class UCorrRandomForrest_Test {

    public static void main(String []args) {

        Random rand0 = new Random();
        float[] noise_scale = {0f, 0.001f, 0.0025f, 0.005f, 0.01f, 0.025f, 0.05f, 0.1f, 0.25f, 0.5f, 1f};


        System.out.println("Noise\t uCorr\t pValue(0-1)");
        for (int i = 0; i < noise_scale.length; i++) {
            double[][] dataD = SimulatedDataGenerator.getQuadraticRelation(rand0, 300, new GaussRandR(rand0, noise_scale[i]));
            UCorrResult ucorrResult = UCorrRandomForrest.uCorr(dataD);
            System.out.println(noise_scale[i] + "\t" + ucorrResult.corrCoefficient + "\t" + ucorrResult.pValue);
        }
    }
}
