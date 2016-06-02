package in.ac.iitm.shaili.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Awanish Raj on 26/05/16.
 */
public class CropView extends ImageView {

    private static final String LOG_TAG = "CropView";

    private static final int HEIGHT = 240;

    public CropView(Context context) {
        super(context);
        init();
    }

    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(LOG_TAG, "Touch event received");
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.e(LOG_TAG, "ActionDown received");
                box.blipPressed(event.getX() / this.getWidth(), event.getY() / this.getHeight());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(LOG_TAG, "ActionMove received");
                box.blipMoved(event.getX() / this.getWidth(), event.getY() / this.getHeight());
                break;
            case MotionEvent.ACTION_UP:
                Log.e(LOG_TAG, "ActionUp received");
                box.blipReleased(event.getX() / this.getWidth(), event.getY() / this.getHeight());
                break;
        }
        return true;
    }

    Box box = new Box();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        box.drawBox(canvas);
    }

    private void updateResult() {
        CropView.this.removeCallbacks(updateImageRunnable);
        CropView.this.post(updateImageRunnable);
    }

    private float getAspectRatio() {
        return bmp.getWidth() * 1.0f / bmp.getHeight();
    }

    private class Box {
        Coords[] coords = new Coords[4];

        public Box() {
            coords[0] = new Coords(0, 0);
            coords[1] = new Coords(1, 0);
            coords[2] = new Coords(1, 1);
            coords[3] = new Coords(0, 1);
//            coords[0] = new Coords(0.16764706f, 0.24655172f);
//            coords[1] = new Coords(0.57843137f, 0.1362069f);
//            coords[2] = new Coords(0.79019606f, 0.5827586f);
//            coords[3] = new Coords(0.422549f, 0.9189655f);
        }

        public void drawBox(Canvas canvas) {

            /**
             * Draw bounds
             */
            drawLine(canvas, coords[0], coords[1], 3);
            drawLine(canvas, coords[1], coords[2], 3);
            drawLine(canvas, coords[2], coords[3], 3);
            drawLine(canvas, coords[3], coords[0], 3);

            /**
             * Draw grid
             */
            drawGridLines(canvas);

            /**
             * Draw blips
             */
            coords[0].drawBlip(canvas, "0");
            coords[1].drawBlip(canvas, "1");
            coords[2].drawBlip(canvas, "2");
            coords[3].drawBlip(canvas, "3");
        }

        private void drawGridLines(Canvas canvas) {
            int numPoints = 10;

            for (int i = 0; i < numPoints; i++) {
                float x0 = coords[0].x + ((i + 1) * (coords[1].x - coords[0].x) / (numPoints + 1));
                float y0 = coords[0].y + ((i + 1) * (coords[1].y - coords[0].y) / (numPoints + 1));
                float x1 = coords[3].x + ((i + 1) * (coords[2].x - coords[3].x) / (numPoints + 1));
                float y1 = coords[3].y + ((i + 1) * (coords[2].y - coords[3].y) / (numPoints + 1));
                drawLine(canvas, new Coords(x0, y0), new Coords(x1, y1), 1);
            }

            for (int i = 0; i < numPoints; i++) {
                float x0 = coords[0].x + ((i + 1) * (coords[3].x - coords[0].x) / (numPoints + 1));
                float y0 = coords[0].y + ((i + 1) * (coords[3].y - coords[0].y) / (numPoints + 1));
                float x1 = coords[1].x + ((i + 1) * (coords[2].x - coords[1].x) / (numPoints + 1));
                float y1 = coords[1].y + ((i + 1) * (coords[2].y - coords[1].y) / (numPoints + 1));
                drawLine(canvas, new Coords(x0, y0), new Coords(x1, y1), 1);
            }
        }


        private void drawLine(Canvas canvas, Coords a, Coords b, float width) {
            paint.setStrokeWidth(width);
            paint.setColor(Color.WHITE);
            canvas.drawLine(a.x * canvas.getWidth(),
                    a.y * canvas.getHeight(),
                    b.x * canvas.getWidth(),
                    b.y * canvas.getHeight(),
                    paint);
//            paint.setStrokeWidth(3);
//            paint.setColor(Color.BLUE);
//            canvas.drawLine(a.x * canvas.getWidth(),
//                    a.y * canvas.getHeight(),
//                    b.x * canvas.getWidth(),
//                    b.y * canvas.getHeight(),
//                    paint);
        }


        private int pressedBlip = -1;

        public void blipPressed(float x, float y) {
            Log.e(LOG_TAG, "Pressed : X: " + x + "Y: " + y);
            for (int i = 0; i < coords.length; i++) {
                if (coords[i].isInRegion(x, y)) {
                    pressedBlip = i;
                    break;
                }
            }
        }

        public void blipMoved(float x, float y) {
            Log.e(LOG_TAG, "Moved : X: " + x + "Y: " + y);

            if (pressedBlip != -1) {
                if (x >= 0f && x <= 1.0f)
                    coords[pressedBlip].x = x;
                if (y >= 0f && y <= 1.0f)
                    coords[pressedBlip].y = y;
            }
            invalidate();

            updateResult();
        }

        public void blipReleased(float x, float y) {
            Log.e(LOG_TAG, "Released : X: " + x + "Y: " + y);

            blipMoved(x, y);
            pressedBlip = -1;
            invalidate();
        }

        public int getOriginX() {
            return (int) (coords[0].x * bmp.getWidth());
        }

        public int getOriginY() {
            return (int) (coords[0].y * bmp.getHeight());
        }

        public float[] getSrcPoints() {

            float[] srcPoints = {
                    coords[0].x * bmp.getWidth(), coords[0].y * bmp.getHeight(),
                    coords[1].x * bmp.getWidth(), coords[1].y * bmp.getHeight(),
                    coords[2].x * bmp.getWidth(), coords[2].y * bmp.getHeight(),
                    coords[3].x * bmp.getWidth(), coords[3].y * bmp.getHeight()
            };
            return srcPoints;
        }

        public float[] getCornerPoints() {
            float[] cornerPoints = {
                    0 * bmp.getWidth(), 0 * bmp.getHeight(),
                    1 * bmp.getWidth(), 0 * bmp.getHeight(),
                    1 * bmp.getWidth(), 1 * bmp.getHeight(),
                    0 * bmp.getWidth(), 1 * bmp.getHeight()
            };
            return cornerPoints;
        }

        public float[] getAdjustedCorners() {
            float minX = coords[0].x;
            float minY = coords[0].y;
            float maxX = coords[0].x;
            float maxY = coords[0].y;

            for (int i = 1; i < 4; i++) {
                if (minX > coords[i].x) minX = coords[i].x;
                if (minY > coords[i].y) minY = coords[i].y;
                if (maxX < coords[i].x) maxX = coords[i].x;
                if (maxY < coords[i].y) maxY = coords[i].y;
            }

            float[] cornerPoints = {
                    minX * bmp.getWidth(), minY * bmp.getHeight(),
                    maxX * bmp.getWidth(), minY * bmp.getHeight(),
                    maxX * bmp.getWidth(), maxY * bmp.getHeight(),
                    minX * bmp.getWidth(), maxY * bmp.getHeight()
            };
            return cornerPoints;
        }


        public float[] getDstPoints() {
//            float[] dstPoints = {
//                    0 * bmp.getWidth(), 0 * bmp.getHeight(),
//                    1 * bmp.getWidth(), 0 * bmp.getHeight(),
//                    1 * bmp.getWidth(), 1 * bmp.getHeight(),
//                    0 * bmp.getWidth(), 1 * bmp.getHeight()
//            };

            float[] dstPoints = {
                    0, 0,
                    HEIGHT * getAspectRatio(), 0,
                    HEIGHT * getAspectRatio(), HEIGHT,
                    0, HEIGHT
            };
            return dstPoints;
        }

    }

//    private int getMaxDim

    private Runnable updateImageRunnable = new Runnable() {
        @Override
        public void run() {
            if (ivImage != null) {
                try {
                    ivImage.setImageBitmap(getSkewedBitmap());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Paint paint;

    private class Coords {
        public float x = 0.0f;
        public float y = 0.0f;

        public Coords(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void drawBlip(Canvas canvas, String label) {
            paint.setColor(Color.WHITE);
            canvas.drawCircle(x * canvas.getWidth(), y * canvas.getHeight(), 30, paint);
            paint.setColor(Color.BLUE);
            canvas.drawCircle(x * canvas.getWidth(), y * canvas.getHeight(), 25, paint);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(40);
            canvas.drawText(label, x * canvas.getWidth(), y * canvas.getHeight() - (paint.descent() + paint.ascent()) / 2, paint);
        }

        public boolean isInRegion(float x, float y) {
            if (Math.sqrt(Math.pow((this.x - x) * getWidth(), 2) + Math.pow((this.y - y) * getHeight(), 2)) <= 50)
                return true;
            else {
                return false;
            }
        }

    }


    private ImageView ivImage;

    public void attachImageView(ImageView ivImage) {
        this.ivImage = ivImage;
        this.updateResult();
    }

    private Bitmap bmp;

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.bmp = bm;
    }

    public Bitmap getSkewedBitmap() throws OutOfMemoryError, NullPointerException {
//        Matrix mat = new Matrix();

        Matrix imageMatrix = new Matrix();

        imageMatrix.setPolyToPoly(box.getSrcPoints(), 0, box.getDstPoints(), 0, 4);

//        float[] corners = box.getSrcPoints();
//        float oldMinX = corners[0];
//        float oldMinY = corners[1];
//
//        float oldMaxX = corners[0];
//        float oldMaxY = corners[1];
//
//        for (int i = 1; i < 4; i++) {
//            if (oldMinX > corners[2 * i]) oldMinX = corners[2 * i];
//            if (oldMaxX < corners[2 * i]) oldMaxX = corners[2 * i];
//            if (oldMinY > corners[2 * i + 1]) oldMinY = corners[2 * i + 1];
//            if (oldMaxY < corners[2 * i + 1]) oldMaxY = corners[2 * i + 1];
//        }

        float[] corners = box.getCornerPoints();
        printArray("Before: ", corners);
        imageMatrix.mapPoints(corners);
        printArray("After: ", corners);


        float minX = corners[0];
        float minY = corners[1];

        for (int i = 1; i < 4; i++) {
            if (minX > corners[2 * i]) minX = corners[2 * i];
            if (minY > corners[2 * i + 1]) minY = corners[2 * i + 1];
        }

        /**
         * Corrects orientation of image
         */


        Bitmap temp = Bitmap.createBitmap(
                bmp,
//                (int) oldMinX, (int) oldMinY,
                (int) 0, (int) 0,
//                (int) (oldMaxX - oldMinX), (int) (oldMaxY - oldMinY),
                (int) (bmp.getWidth()), (int) (bmp.getHeight()),
//                200, 200,
                imageMatrix, true);

        /**
         * Cropping desired location
         */
        temp = Bitmap.createBitmap(temp, (int) (-minX), (int) (-minY), (int) (HEIGHT * getAspectRatio()), HEIGHT);

        return temp;
    }

    private void printArray(String tag, float[] array) {
        String main = tag + " ";
        for (float val : array) {
            main = main + val + ", ";
        }
        Log.e(LOG_TAG, main);
    }
}
