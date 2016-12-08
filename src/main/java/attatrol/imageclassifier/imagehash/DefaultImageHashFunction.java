package attatrol.imageclassifier.imagehash;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import attatrol.imageclassifier.ImageClassifierException;
import attatrol.imageclassifier.i18n.ImageClassifierI18nProvider;

public class DefaultImageHashFunction implements ImageHashFunction {

    private int width;

    private int height;

    public DefaultImageHashFunction(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double[] hash(File image) throws ImageClassifierException {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(image);
        } catch (IOException e) {
            throw new ImageClassifierException(ImageClassifierI18nProvider.getText("message.filecannotbeopened")
                    + e.getLocalizedMessage());
        }
        int[][] raster = convertTo2DWithoutUsingGetRGB(bi);
        double[] result = new double[width * height * 4];
        final int imageHeight = bi.getHeight();
        final int imageWidth = bi.getWidth();
        Map<Integer, Integer> count = new TreeMap<>();
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                final int cellIndex = j * width / imageWidth + width * i * (height - 1) / imageHeight;
                if (count.containsKey(cellIndex)) {
                    int old = count.get(cellIndex);
                    count.put(cellIndex,  old + 1);
                }
                else {
                    count.put(cellIndex, 1);
                }
                int rgbPoint = raster[i][j];
                int blackPoint = rgbPoint & 0xff;
                result[4 * cellIndex] += blackPoint;
                rgbPoint >>>= 8;
                blackPoint += rgbPoint & 0xff;
                result[4 * cellIndex + 1] += rgbPoint & 0xff;
                rgbPoint >>>= 8;
                blackPoint += rgbPoint & 0xff;
                result[4 * cellIndex + 2] += rgbPoint & 0xff;
                result[4 * cellIndex + 3] += ((double) blackPoint) / 3;
            }
        }
        for (int i = 0; i < result.length; i++) {
            result[i] /= (width * height * 0xff);
            if (result[i] > 1.) {
                System.out.println(i + " : " + result[i]);
                result[i] = 1.;
            }
        }
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            System.out.println("[" + entry.getKey() +"] = " + entry.getValue());
        }
        return result;
    }

    @Override
    public int getResultSize() {
        return width * height * 4;
    }

    private static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
           final int pixelLength = 4;
           for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
              int argb = 0;
              argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
              argb += ((int) pixels[pixel + 1] & 0xff); // blue
              argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
              argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
              result[row][col] = argb;
              col++;
              if (col == width) {
                 col = 0;
                 row++;
              }
           }
        } else {
           final int pixelLength = 3;
           for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
              int argb = 0;
              argb += -16777216; // 255 alpha
              argb += ((int) pixels[pixel] & 0xff); // blue
              argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
              argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
              result[row][col] = argb;
              col++;
              if (col == width) {
                 col = 0;
                 row++;
              }
           }
        }

        return result;
     }

}
