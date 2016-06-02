package in.ac.iitm.shaili.Objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceHolder;

import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Math.abs;

/**
 * Created by Awanish Raj on 20/06/15.
 */
public class RectLocation {

    private static final String LOG_TAG = "RectLocation";
    public static final int MIN_DIM = 30;

    private float left_top_x;
    private float left_top_y;
    private float right_bottom_x;
    private float right_bottom_y;

    private static final String LEFT_TOP_X = "left-top-x";
    private static final String LEFT_TOP_Y = "left-top-y";
    private static final String RIGHT_BOTTOM_X = "right-bottom-x";
    private static final String RIGHT_BOTTOM_Y = "right-bottom-y";

    /**
     * Getters and setters for the various parameters
     */

    public float getLeft_top_x() {
        return left_top_x;
    }

    public float getLeft_top_y() {
        return left_top_y;
    }

    public float getRight_bottom_x() {
        return right_bottom_x;
    }

    public float getRight_bottom_y() {
        return right_bottom_y;
    }

    public void setLeft_top_x(float left_top_x) {
        this.left_top_x = left_top_x;
    }

    public void setLeft_top_y(float left_top_y) {
        this.left_top_y = left_top_y;
    }

    public void setRight_bottom_x(float right_bottom_x) {
        this.right_bottom_x = right_bottom_x;
    }

    public void setRight_bottom_y(float right_bottom_y) {
        this.right_bottom_y = right_bottom_y;
    }

    /**
     * Get a JsonObject for the rectangle parameters
     *
     * @return
     * @throws JSONException
     */
    public JSONObject getJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(LEFT_TOP_X, this.left_top_x);
        jsonObject.put(LEFT_TOP_Y, this.left_top_y);
        jsonObject.put(RIGHT_BOTTOM_X, this.right_bottom_x);
        jsonObject.put(RIGHT_BOTTOM_Y, this.right_bottom_y);
        return jsonObject;
    }

    /**
     * Parsing the JSONobject into the rectangle object
     *
     * @param jsonObject
     * @throws JSONException
     */
    public void parseJson(JSONObject jsonObject) throws JSONException {
        this.left_top_x = (float) jsonObject.getDouble(LEFT_TOP_X);
        this.left_top_y = (float) jsonObject.getDouble(LEFT_TOP_Y);
        this.right_bottom_x = (float) jsonObject.getDouble(RIGHT_BOTTOM_X);
        this.right_bottom_y = (float) jsonObject.getDouble(RIGHT_BOTTOM_Y);
    }


    /**
     * String getters and setters
     */

    public String getString() throws JSONException {
        return getJson().toString();
    }

    public void parseString(String source) throws JSONException {
        this.parseJson(new JSONObject(source));
    }

    /**
     * Method to normalize the rectangle dimensions into percentage
     *
     * @param width
     * @param height
     * @return
     */
    public RectLocation normalize(float width, float height) {
        RectLocation rectLocation = new RectLocation();
        rectLocation.left_top_x = this.left_top_x / width;
        rectLocation.right_bottom_x = this.right_bottom_x / width;
        rectLocation.left_top_y = this.left_top_y / height;
        rectLocation.right_bottom_y = this.right_bottom_y / height;
        return rectLocation;
    }

    /**
     * Getting the absolute width of the rectangle
     *
     * @return
     */
    public float getAbsWidth() {
        return abs(this.getLeft_top_x() - this.getRight_bottom_x());
    }

    /**
     * Getting the absolute height of the rectangle
     *
     * @return
     */
    public float getAbsHeight() {
        return abs(this.getLeft_top_y() - this.getRight_bottom_y());
    }

    public float getMinDim() {
        if (this.getAbsHeight() > this.getAbsWidth()) return this.getAbsWidth();
        else return this.getAbsHeight();
    }

    /**
     * Method to return the smallest X position
     *
     * @return
     */
    public float getSmallestX() {
        if (this.getLeft_top_x() < this.getRight_bottom_x()) {
            return this.getLeft_top_x();
        } else {
            return this.getRight_bottom_x();
        }
    }

    /**
     * Method to return the smallest Y position
     *
     * @return
     */
    public float getSmallestY() {
        if (this.getLeft_top_y() < this.getRight_bottom_y()) {
            return this.getLeft_top_y();
        } else {
            return this.getRight_bottom_y();
        }
    }

    public void drawOnCanvas(SurfaceHolder holder) {
        try {
            /**
             * Locking the canvas
             */
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);

            /**
             * Preparing the paint brush for the rectangle
             */
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            if (this.getMinDim() > MIN_DIM)
                paint.setColor(Color.GREEN);
            else paint.setColor(Color.RED);
            paint.setStrokeWidth(3);

            /**
             * Drawing the rectangle
             */
            canvas.drawRect(this.getLeft_top_x()
                    , this.getLeft_top_y()
                    , this.getRight_bottom_x()
                    , this.getRight_bottom_y()
                    , paint);

            /**
             * Unlocking the canvas
             */
            holder.unlockCanvasAndPost(canvas);
        } catch (Throwable e) {
            Log.e(LOG_TAG, "Error in drawing");
        }
    }
}
