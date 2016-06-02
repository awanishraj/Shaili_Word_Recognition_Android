package in.ac.iitm.shaili.ImageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import in.ac.iitm.shaili.Objects.RectLocation;

/**
 * Created by Awanish Raj on 20/06/15.
 */
public class BitmapProcessor {

    private static final String LOG_TAG = "BitmapProcessor";

    /**
     * For comparing different images.
     */
    public static final String TYPE_NONE = "NONE";
    public static final String TYPE_OTSU = "OTSU";
    public static final String TYPE_ADAPTIVE = "ADAP";

    /**
     * Method that binarizes and crops the input image
     *
     * @param toTransform
     * @param cropLocation
     * @return
     */
    public static Bitmap process(Bitmap toTransform, RectLocation cropLocation, String type) {

        /**
         * Getting the dimensions of the rectangle
         */
        float width = cropLocation.getAbsWidth();
        float height = cropLocation.getAbsHeight();
        float x = cropLocation.getSmallestX();
        float y = cropLocation.getSmallestY();

        /**
         * Scaling to actual bitmap dimensions
         */
        x = x * toTransform.getWidth();
        width = width * toTransform.getWidth();
        y = y * toTransform.getHeight();
        height = height * toTransform.getHeight();

        /**
         * Truncating to the bitmap dimensions
         */
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > toTransform.getWidth()) width = toTransform.getWidth() - x;
        if (y + height > toTransform.getHeight()) height = toTransform.getHeight() - y;

        Log.e(LOG_TAG, "Transformed dims: " + width + "x" + height);
        /**
         * Cropping the image based on the scaled dimensions
         */
        Bitmap myTransformedBitmap = null;
        try {
            myTransformedBitmap = Bitmap.createBitmap(toTransform,
                    (int) x,
                    (int) y,
                    (int) width,
                    (int) height);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        float ar = myTransformedBitmap.getWidth() * 1.0f / myTransformedBitmap.getHeight();

//        myTransformedBitmap = Bitmap.createScaledBitmap(myTransformedBitmap, (int) (160 * ar), 160, true);

        /**
         * Binarizing the image based on input type
         */
        long startTime = System.currentTimeMillis();
        switch (type) {
            case TYPE_NONE:
                break;
            case TYPE_OTSU:
                myTransformedBitmap = BinarizeOtsu.thresh(myTransformedBitmap);
                break;
            case TYPE_ADAPTIVE:
                myTransformedBitmap = BinarizeAdaptive.thresh(myTransformedBitmap);
                break;
        }
        Log.e(LOG_TAG, "Time taken for " + type + " binarizartion = " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");

        return myTransformedBitmap;
    }
}
