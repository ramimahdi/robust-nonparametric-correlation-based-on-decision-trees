package TreesBasedCorr;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

import java.util.*;

public class Util {

    public static int[][] getTranspose(int[][] arr){
        int[][] arr_t=new int[arr[0].length][arr.length];
        for(int i=0;i<arr.length;i++)
            for(int j=0;j<arr[0].length;j++)
                arr_t[j][i]=arr[i][j];

        return arr_t;
    }

    public static float[][] getTranspose(float[][] arr){
        float[][] arr_t=new float[arr[0].length][arr.length];
        for(int i=0;i<arr.length;i++)
            for(int j=0;j<arr[0].length;j++)
                arr_t[j][i]=arr[i][j];

        return arr_t;
    }

    public static float[] getColumn(float [][] array, int columnInd){
        float[] row = new float[array.length];
        for(int i=0;i<array.length;i++)
            row[i] = array[i][columnInd];

        return row;
    }

    public static double[] toDoubleArray(Vector<Double> al){
        double[] a=new double[al.size()];
        for(int i=0;i<al.size();i++){
            a[i] = al.get(i);
        }
        return a;
    }

    public static int[] toIntArray(List<Integer> al){
        int[] a=new int[al.size()];
        for(int i=0;i<al.size();i++){
            a[i] = al.get(i);
        }
        return a;
    }

    public static float[][] getArrayCopy(float[][] arr){
        float[][] newArr = new float[arr.length][];
        for(int i=0;i<arr.length;i++){
            newArr[i]=new float[arr[i].length];
            for(int j=0;j<arr[i].length;j++)
                newArr[i][j]=arr[i][j];
        }
        return newArr;
    }

    public static float[] getArrayCopy(float[] src){
        float[] dest=new float[src.length];
        for(int i=0;i<src.length;i++){
            dest[i] = src[i];
        }
        return dest;
    }

    public static double[] getArrayCopy(double[] src){
        double[] dest=new double[src.length];
        for(int i=0;i<src.length;i++){
            dest[i] = src[i];
        }
        return dest;
    }

    public static float[][] double2Float(double[][] data){
        float[][] newData = new float[data.length][];
        for(int i=0;i<data.length;i++){
        	newData[i]=new float[data[i].length];
        	for(int j=0;j<data[i].length;j++)
                newData[i][j] = (float)data[i][j];
        }
        return newData;
    }

    public static int[] getRandomIndex(Random rand, int len){
        int[] randInds = new int[len];
        for(int i=0;i<len;i++){
            randInds[i]=i;
        }
        for(int i=0;i<len;i++){
            int rInd  = i + rand.nextInt(len -i);
            int tmp = randInds[i];
            randInds[i] = randInds[rInd];
            randInds[rInd] = tmp;
        }
        return randInds;
    }

    public static float[] getItems(float[] arr, int[] itemIndices){
        float[] tmp= new float[itemIndices.length];
        for(int i=0;i<itemIndices.length;i++)
            tmp[i]=arr[itemIndices[i]];

        return tmp;
    }

    public static int[] getPseudoIndex(float key, int [] valsSortedAsce){
        int lessThanCount=0;
        if(valsSortedAsce.length <7){
            while(lessThanCount < valsSortedAsce.length && valsSortedAsce[lessThanCount] < key)
                lessThanCount++;
        }

        else{
            if(valsSortedAsce[0]>=key)
                lessThanCount=0;

            else if(valsSortedAsce[valsSortedAsce.length-1]<key)
                return new int[]{valsSortedAsce.length,0,0};

            else{
                int start=0, end=valsSortedAsce.length-2;
                while(end > start+1){
                    int mid=(start+end)/2;
                    if(valsSortedAsce[mid]<key)
                        start=mid;
                    else
                        end=mid-1;
                }
                if(valsSortedAsce[end]<key)
                    lessThanCount = end+1;
                else
                    lessThanCount = start+1;
            }
         }
        int equalCount=0;
        int greaterThanCount=0;
        for(int j=Math.max(0,lessThanCount-1);j<valsSortedAsce.length;j++) {
            if (valsSortedAsce[j] == key)
                equalCount++;
            else if (valsSortedAsce[j] > key) {
                greaterThanCount = valsSortedAsce.length - j;
                break;
            }
        }

        return new int[]{lessThanCount, equalCount, greaterThanCount};
    }

}