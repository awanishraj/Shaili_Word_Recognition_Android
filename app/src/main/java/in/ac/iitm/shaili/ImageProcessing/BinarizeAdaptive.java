package in.ac.iitm.shaili.ImageProcessing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by Awanish Raj on 24/06/15.
 */
public class BinarizeAdaptive {

    private static final String LOG_TAG = "BinarizeAdaptive";

    private static final int WINDOW_FACTOR = 8;
    private static final float THRESH_DARKBG = 1.55f;
    private static final float THRESH_LIGHTBG = 0.85f;

    /**
     * Image binarization using Bradley's algorithm
     *
     * @param original
     * @return
     */
    public static Bitmap thresh(Bitmap original) {
        final int width = original.getWidth();
        final int height = original.getHeight();

        int integralImg[] = new int[width * height];
//        int grayMatrix[][] = new int[width][height];

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int sum, index, count;


//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                int grayVal = gray(original.getPixel(i, j));
//                grayMatrix[i][j] = grayVal;
//                if (maxGray < grayVal) maxGray = grayVal;
//                if (minGray > grayVal) minGray = grayVal;
//            }
//        }

        int minGray = 255;
        int maxGray = 0;

        /**
         * First pass for computing the integral image.
         */
        for (int i = 0; i < width; i++) {
            sum = 0;
            for (int j = 0; j < height; j++) {
//                int grayPixel = (grayMatrix[i][j] - minGray) * 255 / (maxGray - minGray);
                int grayPixel = gray(original.getPixel(i, j));
//                grayMatrix[i][j] = grayPixel;

                if (maxGray < grayPixel) maxGray = grayPixel;
                if (minGray > grayPixel) minGray = grayPixel;

                sum += grayPixel;
                index = j * width + i;
                integralImg[index] = sum + ((i != 0) ? integralImg[index - 1] : 0);
            }
        }

        Log.e(LOG_TAG, "Gray Max: " + maxGray + " Min: " + minGray);

        float bgAverage = (integralImg[width * height - 1] / (height * width));
        Log.e(LOG_TAG, "Background average: " + bgAverage);

        float scaledAverage = (bgAverage - minGray) * 255 / (maxGray - minGray);
        Log.e(LOG_TAG, "Scaled average: " + scaledAverage);

        float bgLightness = scaledAverage / 255f;

        Log.e(LOG_TAG, "Binarize lightness: " + bgLightness);

        float thresholdFactor = (bgLightness > 0.5f) ? THRESH_LIGHTBG : THRESH_DARKBG;
        /**
         * For high value of bgLightness, we need <1f
         * For bgLightness = 0.5f, we need 1f;
         * For lower value of bgLightness, we need >1f
         */


        int x1, y1, x2, y2;
        int s2 = (width / WINDOW_FACTOR) / 2;

        /**
         * Second pass to perform thresholding
         */
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                /**
                 * Calculating corner positions for S X S box
                 */
                x1 = i - s2;
                x2 = i + s2;
                y1 = j - s2;
                y2 = j + s2;

                /**
                 * Truncating border values
                 */
                if (x1 < 0) x1 = 0;
                if (x2 >= width) x2 = width - 1;
                if (y1 < 0) y1 = 0;
                if (y2 >= height) y2 = height - 1;

                /**
                 * Total number of pixels in the box after truncation
                 */
                count = (x2 - x1) * (y2 - y1);

                /**
                 * Calculating sum over the box using integral image
                 */
                sum = integralImg[y2 * width + x2]
                        - integralImg[y1 * width + x2]
                        - integralImg[y2 * width + x1]
                        + integralImg[y1 * width + x1];


                x1 = i - 30;
                x2 = i + 30;
                y1 = j - 30;
                y2 = j + 30;

//                /**
//                 * Truncating border values
//                 */
//                if (x1 < 0) x1 = 0;
//                if (x2 >= width) x2 = width - 1;
//                if (y1 < 0) y1 = 0;
//                if (y2 >= height) y2 = height - 1;
//
//                /**
//                 * Total number of pixels in the box after truncation
//                 */
//                int biggerCount = (x2 - x1) * (y2 - y1);
//
//                /**
//                 * Calculating sum over the box using integral image
//                 */
//                int biggerSum = integralImg[y2 * width + x2]
//                        - integralImg[y1 * width + x2]
//                        - integralImg[y2 * width + x1]
//                        + integralImg[y1 * width + x1];


                /**
                 * Binarizing the image based on threshold for the box
                 */

//                int gray = grayMatrix[i][j];
//                output.setPixel(i, j, Color.rgb(gray, gray, gray));
//                if ((grayMatrix[i][j] * count) <= (long) (sum * thresholdFactor)) {
//                float localLightness = (biggerSum / (255f * biggerCount));
//                long mainThresh = (long) ((localLightness > 0.5f) ? sum * THRESH_LIGHTBG : sum * THRESH_DARKBG);
//                if ((grayMatrix[i][j] * count) <= mainThresh) {
                int scaledPixel = (gray(original.getPixel(i, j)) - minGray) * 255 / (maxGray - minGray);
                int scaledAverage2 = (sum / count - minGray) * 255 / (maxGray - minGray);
//                if ((grayMatrix[i][j] * count) <= (long) (sum * thresholdFactor)) {
                if ((scaledPixel) <= (long) (scaledAverage2 * thresholdFactor)) {
//                if ((gray(original.getPixel(i, j)) * count) <= (long) (sum * 1.0f+bgLightness + 1.0f)) {
                    output.setPixel(i, j, (bgLightness > 0.5f) ? Color.BLACK : Color.WHITE);
//                    output.setPixel(i, j, (localLightness > 0.5f) ? Color.BLACK : Color.WHITE);
//                    output.setPixel(i, j, Color.BLACK);
                } else {
                    output.setPixel(i, j, (bgLightness > 0.5f) ? Color.WHITE : Color.BLACK);
//                    output.setPixel(i, j, (localLightness > 0.5f) ? Color.WHITE : Color.BLACK);
//                    output.setPixel(i, j, Color.WHITE);
                }
            }
        }

        return output;
    }

    /**
     * Method to obtain gray pixel
     *
     * @param pixel
     * @return
     */
    private static int gray(int pixel) {

        /**
         * RGB average
         */
//        return ((Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3);

        /**
         * Standard luminance calculation
         */
        return (int) (0.2126 * Color.red(pixel) + 0.7152 * Color.green(pixel) + 0.0722 * Color.blue(pixel));

        /**
         * Perceived luminance calculation
         */
//        return (int) (0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) + 0.114 * Color.blue(pixel));

    }

}
