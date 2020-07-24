package TreesBasedCorr;

import java.util.Random;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

public class SimulatedDataGenerator {

    static double[][] getSinusoidalRelation(Random rand, int n, NoiseF noiseF){
        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            //tmp[i][0] = 0.11 + rand.nextDouble();
            //tmp[i][1] = Math.sin(4*Math.PI*tmp[i][0] ) ;

            tmp[i][0] = rand.nextDouble();
            tmp[i][1] = Math.sin(5*Math.PI*tmp[i][0] ) ;
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    static double[][] getCrossRelation(Random rand, int n, NoiseF noiseF){
        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            tmp[i][0] = rand.nextDouble();
            if(rand.nextBoolean())
                tmp[i][1] = tmp[i][0] -0.5;
            else
                tmp[i][1] = 0.5 - tmp[i][0];
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    static double[][] getBrokenCrossRelation(Random rand, int n, NoiseF noiseF){
        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            tmp[i][0] = rand.nextDouble();
            if(rand.nextBoolean())
                tmp[i][1] = tmp[i][0] -0.5;
            else
                tmp[i][1] = 0.5 - tmp[i][0];

            if (tmp[i][0] > 0.5)
                tmp[i][0] += 0.25;

            if (tmp[i][1] > 0.0)
                tmp[i][1] += 0.25;

            tmp[i][1] /= 1.25;
            tmp[i][0] /= 1.25;
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    static double[][] getLinedQuadrableRelation(Random rand, int n, NoiseF noiseF){
        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            tmp[i][0] = rand.nextDouble();
            if(tmp[i][0] < 0.5) {
                tmp[i][1] = 2 * tmp[i][0];
            } else {
                tmp[i][1] = 1 - 2 * (tmp[i][0] - 0.5);
            }
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    public static double[][] getRandomRelation_noRedunduncy(Random rand, int n, NoiseF noiseF){

        double[] v1=getUniqRandVals(rand, n);
        double[] v2=getUniqRandVals(rand, n);

        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            tmp[i][0] = v1[i];
            tmp[i][1] = v2[i];
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    public static double[] getUniqRandVals(Random rand, int len){
        double[] vals=new double[len];
        int ind=0;
        while(ind<len){
            double randV=rand.nextDouble();
            boolean isUsed=false;
            for(int i=0;i<ind;i++)
                if(randV==vals[i]){
                    isUsed=true;
                    break;
                }
            if(!isUsed)
                vals[ind++]=randV;
        }
        return vals;
    }

    public static double[][] getLinearRelation(Random rand, int n, NoiseF noiseF){
        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            tmp[i][0] = rand.nextDouble();
            tmp[i][1] = -0.5 * tmp[i][0] ;
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    public static double[][] getTwosLines(Random rand, int n, NoiseF noiseF){
        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            tmp[i][0] = rand.nextDouble();
            if (tmp[i][0] < 0.5) {
                tmp[i][1] = 2 * tmp[i][0];
            } else {
                tmp[i][1] = 2 * tmp[i][0] -1;
            }
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    public static double[][] getTwosLinesV2(Random rand, int n, NoiseF noiseF){
        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            tmp[i][0] = rand.nextDouble();
            if (tmp[i][0] < 0.5) {
                tmp[i][1] = 1 - 2 * tmp[i][0];
            } else {
                tmp[i][1] = 2 * tmp[i][0] -1;
                tmp[i][0] = -1 + 4 * tmp[i][0];
            }
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    public static double[][] getAssymitric(Random rand, int n, NoiseF noiseF){
        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            tmp[i][0] = rand.nextDouble();
            if (tmp[i][0] < 0.45) {
                tmp[i][1] = tmp[i][0]*tmp[i][0];
            } else {
                tmp[i][1] = 0.45*0.45 - (tmp[i][0] - 0.45)*(tmp[i][0] - 0.45)/2;
            }
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    public static double[][] getDiscrete(Random rand, int n, NoiseF noiseF){
        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            tmp[i][0] = rand.nextDouble();
            if (tmp[i][0] < 0.5) {
                tmp[i][1] = 0;
            } else {
                tmp[i][1] = 1;
            }
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    public static double[][] getAbsSqrt(Random rand, int n, NoiseF noiseF){
        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            tmp[i][0] = rand.nextDouble();
            if (tmp[i][0] < 0.5) {
                tmp[i][1] = Math.sqrt(0.5 - tmp[i][0]);
            } else {
                tmp[i][1] = Math.sqrt(tmp[i][0] - 0.5);
            }
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    static double[][] getEmptyCircileRelation(Random rand, int n, NoiseF noiseF) {
        double[][] tmp = new double[n][2];
        for (int i = 0; i < n; i++) {
            tmp[i][0]= rand.nextDouble();
            tmp[i][1] = Math.sqrt(0.25 - (tmp[i][0]-0.5)*(tmp[i][0]-0.5));

            if(rand.nextBoolean()){
                tmp[i][1] = tmp[i][1];
            }else{
                tmp[i][1] = -tmp[i][1];
            }
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    static double[][] getEmptyDiamondRelation(Random rand, int n, NoiseF noiseF) {
        double[][] tmp = new double[n][2];
        for (int i = 0; i < n; i++) {
            tmp[i][0] = rand.nextDouble() - 0.5;
            if(tmp[i][0]<0){
                if(rand.nextBoolean())
                    tmp[i][1] = 0.5 + tmp[i][0];
                else
                    tmp[i][1] = -0.5 - tmp[i][0];
            }else{
                if(rand.nextBoolean())
                    tmp[i][1] = 0.5 - tmp[i][0];
                else
                    tmp[i][1] = -0.5 + tmp[i][0];
            }
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    static double[][] getSigmoidRelation(Random rand, int n, NoiseF noiseF) {
        double[][] tmp = new double[n][2];
        for (int i = 0; i < n; i++) {
            tmp[i][0] = (rand.nextDouble() -0.5) * 12;
            tmp[i][1] = 1.0 / (1.0 + Math.exp(-tmp[i][0]));
        }
        noiseF.addNoise(tmp);
        return tmp;
    }

    static double[][] getQuadraticRelation(Random rand, int n, NoiseF noiseF){
        double[][] tmp = new double[n][2];
        for(int i=0;i<n;i++){
            tmp[i][0] =  (rand.nextDouble()-0.5);
            tmp[i][1] = 2 * (tmp[i][0]*tmp[i][0]);
        }
        noiseF.addNoise(tmp);
        return tmp;
    }
}
