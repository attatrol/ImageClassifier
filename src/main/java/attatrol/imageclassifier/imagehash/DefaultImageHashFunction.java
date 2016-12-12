package attatrol.imageclassifier.imagehash;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import attatrol.imageclassifier.ImageClassifierException;
import attatrol.imageclassifier.i18n.ImageClassifierI18nProvider;

public class DefaultImageHashFunction implements ImageHashFunction {

    /**
     * 
     */
    private static final long serialVersionUID = -1555439835548762822L;

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
        int[] count = new int[width * height * 4];
        final int imageHeight = bi.getHeight();
        final int imageWidth = bi.getWidth();
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                final int cellIndex = j * width / imageWidth + width * (i * height / imageHeight);
                int rgbPoint = raster[i][j];
                int blackPoint = rgbPoint & 0xff;
                result[4 * cellIndex] += blackPoint;
                count[4 * cellIndex]++;
                rgbPoint >>>= 8;
                blackPoint += rgbPoint & 0xff;
                result[4 * cellIndex + 1] += rgbPoint & 0xff;
                count[4 * cellIndex + 1]++;
                rgbPoint >>>= 8;
                blackPoint += rgbPoint & 0xff;
                result[4 * cellIndex + 2] += rgbPoint & 0xff;
                count[4 * cellIndex + 2]++;
                result[4 * cellIndex + 3] += ((double) blackPoint) / 3;
                count[4 * cellIndex + 3]++;
            }
        }
        for (int i = 0; i < result.length; i++) {
            result[i] /= (count[i] * 0xff);
            if (result[i] > 1.) {
                //System.out.println("Great problem!" + i + " : " + result[i]);
                result[i] = 1.;
            }
            //System.out.println("Index: " + i + "  Size: " + count[i] + "   Value: " + result[i]);
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
        final int pixelDepth = image.getColorModel().getPixelSize();
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
        }
         else if (pixelDepth == 8) {
             final int pixelLength = 1;
             for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel] & 0xff) << 8); // green
                argb += (((int) pixels[pixel] & 0xff) << 16); // red
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
