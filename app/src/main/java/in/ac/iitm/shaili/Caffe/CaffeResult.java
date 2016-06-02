package in.ac.iitm.shaili.Caffe;


import android.graphics.Bitmap;

/**
 * Created by Awanish Raj on 09/05/16.
 */
public class CaffeResult {

    private static final String LOG_TAG = "CaffeResult";

    private int index;
    private String className;
    private float confidence;
    private String path;

    public CaffeResult(int index, String className, float confidence, String filePath) {
        this.index = index;
        this.className = className;
        this.confidence = confidence;
        this.path = filePath;
    }

    public String getClassName() {
        return className;
    }

    public String getColoredString() {
        String output = "<font color = \"" + getColor() + "\">" + className + "</font>";
        return output;
    }

    public String getColor() {
        float confColor = confidence > 0.5 ? confidence : 0.5f;
        confColor = (confColor - 0.5f) * 2;
        float H = (float) (confColor * 0.4); // Hue (note 0.4 = Green, see huge chart below)
        float S = (float) 0.9; // Saturation
        float B = (float) 0.9; // Brightness
//        int color = Color.HSVToColor(new float[]{(float) H, (float) S, (float) B});
//        return String.format("#%08X", color);
        return "#" + hsvToRgb(H, S, B);
    }

    public float getConfidence() {
        return confidence;
    }

    public static String hsvToRgb(float hue, float saturation, float value) {

        int h = (int) (hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0:
                return rgbToString(value, t, p);
            case 1:
                return rgbToString(q, value, p);
            case 2:
                return rgbToString(p, value, t);
            case 3:
                return rgbToString(p, q, value);
            case 4:
                return rgbToString(t, p, value);
            case 5:
                return rgbToString(value, p, q);
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
    }

    public static String rgbToString(float r, float g, float b) {
        String rs = Integer.toHexString((int) (r * 256));
        String gs = Integer.toHexString((int) (g * 256));
        String bs = Integer.toHexString((int) (b * 256));
        return rs + gs + bs;
    }

    @Override
    public String toString() {
        return className;
    }

    public String getImagePath() {
        return path;
    }
}
