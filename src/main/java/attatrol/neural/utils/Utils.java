package attatrol.neural.utils;

import java.util.Arrays;

/**
 * Utilities for misc data operation
 * @author attatrol
 *
 */
public final class Utils {

    private Utils() { }

    public static float[][] getDeepCopy(float[][] array) {
        float[][] copy = new float[array.length][];
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                copy[i] = Arrays.copyOf(array[i], array[i].length);
            }
        }
        return copy;
    }

    public static int[][] getDeepCopy(int[][] array) {
        int[][] copy = new int[array.length][];
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                copy[i] = Arrays.copyOf(array[i], array[i].length);
            }
        }
        return copy;
    }

    public static float[] getCopy(float[] array) {
        return Arrays.copyOf(array, array.length);
    }

    public static int[] getCopy(int[] array) {
        return Arrays.copyOf(array, array.length);
    }

    public static double[] getCopy(double[] array) {
        return Arrays.copyOf(array, array.length);
    }

    public static double measureManhattanDistanceBetweenVectors(double[] v1, double[] v2) {
        double accumulator  = 0.;
        for (int i = 0; i < v1.length; i++) {
            accumulator += Math.abs(v1[i] - v2[i]);
        }
        return accumulator;
    }

    public static int getIndexOfMaxElement(double[] array) {
        int index = 0;
        double max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (max < array[i]) {
                max = array[i];
                index = i;
            }
        }
        return index;
    }
}
