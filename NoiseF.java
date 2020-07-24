package TreesBasedCorr;

import java.util.Random;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

public abstract class NoiseF {
    Random rand;
    float scale;
    NoiseF(Random rand, float scale){
        this.rand = rand;
        this.scale=scale;
    }

    void addNoise(double[][] data) {
        for(int i=0; i<data.length; i++) {
            addNoise(data[i]);
        }
    }

    public void addNoise(double[] data) {
        data[0] += this.nextNoiseVal();
        data[1] += this.nextNoiseVal();
    }

    abstract public float nextNoiseVal();

    public static void main(String[] args) {
        ChiSqure r = new ChiSqure(new Random(), 1);
        for(int i=0;i<1000;i++) {
            System.out.println(r.nextNoiseVal());
        }
    }
}

class UniformRandR extends NoiseF{
    UniformRandR(Random rand, float scale){
        super( rand, scale);
    }
    public float nextNoiseVal(){
        return rand.nextFloat()*scale;
    }

}

class GaussRandR extends NoiseF{
    GaussRandR(Random rand, float scale){
        super( rand, scale);
    }
    public float nextNoiseVal(){
        return (float)rand.nextGaussian()*scale;
    }

}

class ChiSqure extends NoiseF{
    GaussRandR gRand;
    int k = 2;
    ChiSqure(Random rand, float scale){
        super( rand, scale);
        gRand = new GaussRandR( rand, scale);
    }
    public float nextNoiseVal(){
        float tmp = 0f;
        for ( int i=0; i<k; i++) {
            float d = gRand.nextNoiseVal();
            tmp += d*d;
        }
        return tmp;
    }
}

class PremutateNoise extends NoiseF{
    float permRate;
    PremutateNoise(float permRate){
        super(null, 0);
        this.permRate = permRate;
    }
    public float nextNoiseVal(){
        return 0;
    }

    void addNoise(double[][] data) {
        for(int i=0;i<data.length ; i++) {
            if(Math.random() < permRate) {
                int j = (int)((Math.random()*data.length));
                swap(data[i], data[j], Math.random() > 0.5 ? 0: 1);
            }
        }
    }
    private void swap(double[] v1, double[] v2, int ind) {
        double tmp = v1[ind];
        v1[ind] = v2[ind];
        v2[ind] = tmp;

    }
}