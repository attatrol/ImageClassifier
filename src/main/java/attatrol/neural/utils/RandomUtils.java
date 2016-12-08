package attatrol.neural.utils;

import java.util.Random;

/**
 * Utility class that provides different randomization methods.
 * @author attatrol
 *
 */
public final class RandomUtils {

    private static Random sRandom;

    /**
     * Not in use
     */
    private RandomUtils() { }

    public static float[] generateRandomFloats(float min, float max, int size) {
        Random random = getRandom();
        float[] result = new float[size];
        for (int i = 0; i < size; i++) {
            result[i] = random.nextFloat() * (max - min) + min;
        }
        return result;
    }

    public static float[] generateRandomFloats(double min, double max, int size) {
        return generateRandomFloats((float) min, (float) max, size);
    }

    private static Random getRandom() {
        if (sRandom == null) {
            synchronized(RandomUtils.class) {
                if (sRandom == null) {
                    sRandom = new Random();
                }
            }
        }
        return sRandom;
    }

}
