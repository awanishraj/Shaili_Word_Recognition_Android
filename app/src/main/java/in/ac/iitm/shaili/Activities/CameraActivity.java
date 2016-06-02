package in.ac.iitm.shaili.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.ac.iitm.shaili.Caffe.CaffeManager;
import in.ac.iitm.shaili.Camera.HVCamHost;
import in.ac.iitm.shaili.Camera.HVCamUtils;
import in.ac.iitm.shaili.Camera.HVCamView;
import in.ac.iitm.shaili.Helpers.BitmapWriter;
import in.ac.iitm.shaili.ImageProcessing.BitmapProcessor;
import in.ac.iitm.shaili.Objects.RectLocation;
import in.ac.iitm.shaili.R;

public class CameraActivity extends Activity implements View.OnClickListener {

    private SurfaceView transparentView;
    private SurfaceHolder holderTransparent;

    private HVCamView camView;
    RectLocation cropLocation = new RectLocation();
    FrameLayout preview;
    ImageView ivFlash;


    private static final String LOG_TAG = "CameraActivity";

    ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        GalleryActivity.start(this);
        if (false) {
            finish();
            return;
        }
        /**
         * Removing title bar
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        /**
         * Setting layout for the activity
         */
        setContentView(R.layout.activity_main);

        /**
         * A transparent surface view for showing the crop rectangle
         */
        transparentView = (SurfaceView) findViewById(R.id.TransparentView);
        transparentView.setZOrderOnTop(true);
        holderTransparent = transparentView.getHolder();
        holderTransparent.setFormat(PixelFormat.TRANSPARENT);
        holderTransparent.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        /**
         * Setting up the Camera preview
         */
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        camView = HVCamView.getInstance(this, mHost, false);
        preview.addView(camView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            camView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    ViewGroup.LayoutParams params = transparentView.getLayoutParams();
                    params.height = bottom - top;
                    params.width = right - left;
                    transparentView.setLayoutParams(params);
                    params = ((FrameLayout) transparentView.getParent()).getLayoutParams();
                    params.height = bottom - top;
                    params.width = right - left;
                    ((FrameLayout) transparentView.getParent()).setLayoutParams(params);

                }
            });
        }

        preview.setOnTouchListener(onTouchListener);


        /**
         * Initializing model
         */
        CaffeManager.init("android_hiwiki");


        /**
         * Setup Onclick listeners
         */
        findViewById(R.id.btGallery).setOnClickListener(this);
        ivFlash = (ImageView) findViewById(R.id.ivFlash);
        ivFlash.setOnClickListener(this);

    }


    /**
     * On touch listener for the camera preview. This gives coordinates for drawing the rectangle.
     */
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {

//        float RectLeft = 0, RectTop = 0, RectRight = 0, RectBottom = 0;


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    /**
                     * Setting the coordinates of the rectangle on initial touch
                     */
                    cropLocation.setLeft_top_x(event.getX());
                    cropLocation.setLeft_top_y(event.getY());
                    cropLocation.setRight_bottom_x(event.getX());
                    cropLocation.setRight_bottom_y(event.getY());

                    /**
                     * Attempting to focus the camera on initial touch
                     */
                    camView.autoFocusOnly();
                    break;

                case MotionEvent.ACTION_UP:
                    /**
                     * Attempt to capture image when touch is released
                     */
                    if (cropLocation.getMinDim() > RectLocation.MIN_DIM)
                        try {
                            camView.takePicture();
                        } catch (Throwable e) {
                            Log.e(LOG_TAG, "Capture failed");
                        }
                    else {
                        Toast.makeText(CameraActivity.this, "Crop too small. Try again!", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    /**
                     * Set the new right-bottom-x and right-bottom-y locations when touch is dragged
                     */
                    cropLocation.setRight_bottom_x(event.getX());
                    cropLocation.setRight_bottom_y(event.getY());
                    break;

            }
            /**
             * Call the drawing method for the rectangle
             */
            cropLocation.drawOnCanvas(holderTransparent);
            return true;
        }
    };


    private void extractRectFromImage(final byte[] data) {

        try {
            pd = ProgressDialog.show(CameraActivity.this, "Processing image", "Please wait...", true);
        } catch (Throwable ignored) {
        }
        new AsyncTask<Void, Void, Boolean>() {

            private RectLocation normLoc;
            private File pictureFile;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                normLoc = cropLocation.normalize(transparentView.getWidth(), transparentView.getHeight());
                Log.e(LOG_TAG, "Crop Location: " + cropLocation.getAbsWidth() + "x" + cropLocation.getAbsHeight());
                Log.e(LOG_TAG, "Norm Location: " + normLoc.getAbsWidth() + "x" + normLoc.getAbsHeight());
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                /**
                 * Decoding byte array to bitmap
                 */
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                Log.e(LOG_TAG, "Bitmap size: " + bmp.getWidth() + "x" + bmp.getHeight());

                /**
                 * Processing bitmaps and writing to file
                 */
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / 2, bmp.getHeight() / 2, true);
//                bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth() * 2, bmp.getHeight() * 2, true);
//                bmp = BitmapProcessor.process(bmp, normLoc, BitmapProcessor.TYPE_ADAPTIVE);
                bmp = BitmapProcessor.process(bmp, normLoc, BitmapProcessor.TYPE_NONE);

                /**
                 * Extract words and draw bounding boxes
                 */
//                clippedFiles = BlobDetection.clipBlobsFromBitmap(bmp);

                pictureFile = BitmapWriter.write(bmp, timeStamp, "BOX");

                HVCamUtils.scanMediaFile(CameraActivity.this, pictureFile);

                bmp.recycle();

                return pictureFile != null;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                try {
                    pd.dismiss();
                } catch (Throwable ignored) {
                }

                if (success) {
                    /**
                     * Passing on the cropped image path to the Result Activity
                     */
//                    CaffeResultActivity.start(CameraActivity.this, pictureFile.getAbsolutePath());
//                        finish();
                } else {
                    Toast.makeText(CameraActivity.this, "Failed to capture. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    private HVCamHost mHost = new HVCamHost() {
        @Override
        public void onCamerasFound(int count) {

        }

        @Override
        public int getAspectRatio() {
            return ASPECT_9_16;
        }

        @Override
        public float getPreviewMegapixels() {
            return 0.1f;
        }

        @Override
        public float getPictureMegapixels() {
            return 0.1f;
        }

        @Override
        public void onDataTaken(byte[] data) {
            extractRectFromImage(data);
        }

        @Override
        public void onFlashNull() {
            ivFlash.setImageResource(R.drawable.ic_flash_unavailable);
        }

        @Override
        public void onFlashOff() {
            ivFlash.setImageResource(R.drawable.ic_flash_off);
        }

        @Override
        public void onFlashOn() {
            ivFlash.setImageResource(R.drawable.ic_flash_on);

        }

        @Override
        public void onFlashAuto() {
            ivFlash.setImageResource(R.drawable.ic_flash_auto);

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btGallery:
                GalleryActivity.start(this);
                break;
            case R.id.ivFlash:
                camView.nextFlashMode();
                break;
        }
    }
}
