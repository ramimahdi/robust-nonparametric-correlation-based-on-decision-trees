package TreesBasedCorr;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Rami Mahdi on 01/01/2019.
 */

public class TestCaseVisualizer {

    public static void main(String[] str) {
        Random rand0 = new Random(3);

         double[][] dataD = SimulatedDataGenerator.getQuadraticRelation(rand0, 300, new GaussRandR(rand0, 0.001f));
        // double[][] dataD = SimulatedDataGenerator.getQuadraticRelation(rand0, 300, new GaussRandR(rand0, 0.05f));
        // double[][] dataD = SimulatedDataGenerator.getSinusoidalRelation(rand0, 600, new GaussRandR(rand0, 0.01f));
        // double[][] dataD = SimulatedDataGenerator.getTwosLinesV2(rand0, 300, new GaussRandR(rand0, 0.001f));
        //double[][] dataD = SimulatedDataGenerator.getSigmoidRelation(rand0, 300, new GaussRandR(rand0, 0.1f));

        //double[][] dataD = SimulatedDataGenerator.getLinearRelation(rand0, 300, new GaussRandR(rand0, 0.001f));

        // permutate for testing
        //dataD = Util.getRandomized(rand0, dataD);

        float[][] allData = Util.double2Float(dataD);
        float[] allTag = new float[allData.length];
        float[][] allData2 = Util.getArrayCopy(allData);

        DataObject dObj = new DataObject(Util.getTranspose(allData), allTag);

        float sampleRate = 1f;
        float bWeight = 1f * sampleRate / (dObj.getSampleIndex().length - 1);
        float bTag = -1;

        UCorrRandomForrest root = new UCorrRandomForrest(rand0, dObj, dObj.getSampleIndex(),
                new int[]{0, 1}, sampleRate, 100,
                new CorrTreeTrainConfig(200, 0.15f, 1f, 5, 1,
                        bTag, bWeight, new int[]{1000}, 0, 0.1f, 0.3f, true)
        );

        System.out.println("root.getTreesSizes(): " + Arrays.toString(root.getTreesSizes()));
        Plot(allData2, allTag, new FitFunction[]{root}, new float[]{0.1f, 0.1f}, new float[]{500, 500});
    }

    public static void Plot(float[][] allData, float[] allTag, FitFunction[] funcs, float[] xyMarginWidth, float[] xyScale) {
        float xMax = Statistics.maximum(Util.getColumn(allData, 0));
        float xMin = Statistics.minimum(Util.getColumn(allData, 0));
        float yMax = Statistics.maximum(Util.getColumn(allData, 1));
        float yMin = Statistics.minimum(Util.getColumn(allData, 1));

        float xRange = xMax - xMin;
        xMax += xRange * xyMarginWidth[0];
        xMin -= xRange * xyMarginWidth[0];

        float yRange = yMax - yMin;
        yMax += yRange * xyMarginWidth[1];
        yMin -= yRange * xyMarginWidth[1];

        ArrayList<float[]> list = new ArrayList<float[]>();

        float minF = 10, maxF = -10;
        float xStep = (xMax - xMin) / 200;
        float yStep = (yMax - yMin) / 200;

        for (float v1 = xMin; v1 < xMax; v1 += xStep) {
            for (float v2 = yMin; v2 < yMax; v2 += yStep) {
                float[] x = new float[]{v1, v2};
                float fv = 0;
                for (FitFunction func : funcs)
                    fv += func.getValue(x);

                fv /= funcs.length;

                if (fv > 2)
                    fv = 2;
                if (fv < -2)
                    fv = -2;
                if (fv > maxF) maxF = fv;
                if (fv < minF) minF = fv;
                list.add(new float[]{x[0], x[1], fv});
            }
        }
        float cx = xyScale[0];
        float cy = xyScale[1];

        for (float[] v : allData) {
            float[] v2 = v;

            v2[0] = (v[0] - xMin) / (xMax - xMin);
            v2[1] = (v[1] - yMin) / (yMax - yMin);

            v[0] = cx * v2[0] + 10;
            v[1] = cy * v2[1] + 10;
        }

        for (float[] v : list) {
            float[] v2 = v;
            v2[0] = (v[0] - xMin) / (xMax - xMin);
            v2[1] = (v[1] - yMin) / (yMax - yMin);

            v[0] = cx * v2[0] + 10;
            v[1] = cy * v2[1] + 10;
            v[2] = (v[2] - minF) / (maxF - minF);

            v[2] = (v[2] + 1f) / 2;
        }
        Visulaizer vs = Visulaizer.plot(allData, allTag, new float[]{1, 1}, new float[]{0, 0});
        vs.addBackGround(list);
    }
}

class Visulaizer extends JPanel {
    ArrayList<double[]> list_1;
    ArrayList<double[]> list_2;
    ArrayList<double[]> bgBlocks;
    double[] bgStart, bgEnd;
    ArrayList<int[]> lines;

    public Visulaizer(ArrayList<double[]> cls_1_points, ArrayList<double[]> cls_2_points, ArrayList<int[]> lines) {
        list_1 = cls_1_points;
        list_2 = cls_2_points;
        this.lines = lines;
    }

    public static Visulaizer plot(float[][] all_points, float[] tags, float[] scale, float[] offSet) {
        ArrayList<double[]> cls1 = new ArrayList<double[]>();
        ArrayList<double[]> cls2 = new ArrayList<double[]>();
        int ind = 0;
        for (float[] v : all_points) {
            double[] vD = new double[]{v[0] * scale[0] + offSet[0], v[1] * scale[1] + offSet[1]};
            if (tags[ind] > 0)
                cls1.add(vD);
            else
                cls2.add(vD);

            ind++;
        }
        return plot(cls1, cls2, null);
    }

    public static Visulaizer plot(ArrayList<double[]> cls_1_points, ArrayList<double[]> cls_2_points, ArrayList<int[]> lines) {
        Visulaizer panel = new Visulaizer(cls_1_points, cls_2_points, lines);
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(panel);
        frame.setVisible(true);
        System.out.println("********************************");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return panel;
    }

    public void addBackGround(ArrayList<float[]> list) {

        bgStart = new double[]{list.get(0)[0], list.get(0)[1]};
        bgEnd = new double[]{list.get(0)[0], list.get(0)[1]};
        bgBlocks = new ArrayList<double[]>();
        for (float[] v : list) {
            double[] temp = {v[0], v[1], v[2]};
            if (v[0] < bgStart[0])
                bgStart[0] = v[0];
            else if (v[0] > bgEnd[0])
                bgEnd[0] = v[0];

            if (v[1] < bgStart[1])
                bgStart[1] = v[1];
            else if (v[1] > bgEnd[1])
                bgEnd[1] = v[1];

            bgBlocks.add(temp);
        }
    }

    public void paintBG(Graphics g) {
        if (bgBlocks != null) {

            Graphics2D g2 = (Graphics2D) g;
            for (int i = 0; i < bgBlocks.size(); i++) {
                double[] v = bgBlocks.get(i);
                g2.setColor(new Color(0.1f + (float) (0.9 * v[2]), 0.1f + (float) (0.9 * v[2]), 0.1f + (float) (0.9 * v[2])));
                g2.fillRect((int) v[0] - 2, (int) v[1] - 2, 4, 4);
            }
        }
    }

    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.gray);
        g2.fillRect(0, 0, 900, 900);
        g2.setColor(Color.white);
        g2.fillRect(10, 10, 880, 880);

        paintBG(g2);

        if (list_1 != null) {
            g2.setColor(Color.BLUE);
            for (double[] v : list_1) {
                g2.fillRect((int) (v[0] - 2), (int) (v[1] - 2), 2, 2);
            }
        }
        if (list_2 != null) {
            g2.setColor(Color.RED);
            for (double[] v : list_2)
                g2.fillOval((int) (v[0] - 2), (int) (v[1] - 1), 2, 2);
        }

        if (lines != null) {
            g2.setColor(Color.GREEN);
            for (int i = 0; i < lines.size(); i++) {
                int[] line = lines.get(i);
                g2.drawLine(line[0], line[1], line[2], line[3]);
            }
        }
        g2.setColor(Color.black);
        g2.drawRect((int) bgStart[0] - 1, (int) bgStart[1] - 1, (int) (bgEnd[0] - bgStart[0]) + 2, (int) (bgEnd[1] - bgStart[1]) + 2);
    }
}

