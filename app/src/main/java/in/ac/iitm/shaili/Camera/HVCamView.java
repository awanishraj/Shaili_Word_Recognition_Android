package in.ac.iitm.shaili.Camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HVCamView extends GLSurfaceView {
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private int cameraId = 0;
    private Camera mCamera;
    private OnOrientationChange orientationChange;


    public static HVCamView getInstance(Activity activity, HVCamHost HVCamHost, boolean useFFC) {
        return new HVCamView(activity, HVCamHost, useFFC);
    }

    /**
     * Contructor for the CameraPreview
     *
     * @param activity
     * @param hvCamHost
     */
    private HVCamView(Activity activity, HVCamHost hvCamHost, boolean useFFC) {
        super(activity);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        this.hvCamHost = hvCamHost;

        if (hvCamHost.getAspectRatio() == hvCamHost.ASPECT_4_3) {
            mRatioWidth = 3;
            mRatioHeight = 4;
        } else if (hvCamHost.getAspectRatio() == hvCamHost.ASPECT_16_9) {
            mRatioWidth = 9;
            mRatioHeight = 16;
        } else if (hvCamHost.getAspectRatio() == hvCamHost.ASPECT_9_16) {
            mRatioWidth = 16;
            mRatioHeight = 9;
        } else if (hvCamHost.getAspectRatio() == hvCamHost.ASPECT_3_4) {
            mRatioWidth = 4;
            mRatioHeight = 3;
        }

        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        int num = Camera.getNumberOfCameras();
        hvCamHost.onCamerasFound(Camera.getNumberOfCameras());
        if (num > 1 && useFFC) {
            cameraId = 1;
        }


        orientationChange = new OnOrientationChange(activity);
        orientationChange.enable();
    }

    private static final List<String> supportedModes = new ArrayList<>();

    static {
        supportedModes.add(Camera.Parameters.FLASH_MODE_OFF);
        supportedModes.add(Camera.Parameters.FLASH_MODE_AUTO);
        supportedModes.add(Camera.Parameters.FLASH_MODE_ON);
    }

    private int mRatioWidth = 3;
    private int mRatioHeight = 4;

    private void initializeCameraAndPreview() {
        mCamera = HVCamUtils.getCameraInstance(cameraId);
        HVCamUtils.setCameraDisplayOrientation(cameraId, mCamera, Surface.ROTATION_90);
        HVCamUtils.setPictureOrientation(cameraId, mCamera, Surface.ROTATION_90);

        HVCamUtils.applyBestPreviewSize(mCamera, mRatioWidth, mRatioHeight, hvCamHost.getPreviewMegapixels());
        HVCamUtils.applyBestPictureSize(mCamera, mRatioWidth, mRatioHeight, hvCamHost.getPictureMegapixels());
        List<String> allFlashModes = mCamera.getParameters().getSupportedFlashModes();
        if (allFlashModes != null) {
            List<String> flashModes = new ArrayList<>();
            for (String flashMode : allFlashModes) {
                if (supportedModes.contains(flashMode))
                    flashModes.add(flashMode);
            }

            availableFlashmodes = flashModes;
        } else {
            availableFlashmodes = null;
        }
        currentMode = mCamera.getParameters().getFlashMode();
        updateCamHostFlashMode();
    }

    /**
     * Listener for orientation changes
     */
    private class OnOrientationChange extends OrientationEventListener {
        public OnOrientationChange(Context context) {
            super(context);
            disable();
        }

        @Override
        public void onOrientationChanged(int orientation) {
            Log.i(TAG, "Orientation changed : " + orientation);
            if (orientation == ORIENTATION_UNKNOWN) return;
            if (mCamera != null)
                HVCamUtils.setPictureOrientation(cameraId, mCamera, orientation);
        }

        @Override
        public void enable() {
            if (false)
                super.enable();
        }
    }


    /**
     * Fixes the preview dimensions
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);

//            if (width < height * mRatioWidth / mRatioHeight) {
//                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
//            } else {
//                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
//            }
        }
    }

    private HVCamHost hvCamHost;

    /**
     * Callback after picture is taken
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            hvCamHost.onDataTaken(data);
            previewCreated();
        }
    };

    public void takePicture() {
        try {
            mCamera.takePicture(null, null, mPicture);
        } catch (Throwable e) {
            Log.e(TAG, "Camera not ready for capture");
        }
    }

    private void resetCamera(int cameraId) {
        previewDestroyed();
        this.cameraId = cameraId;
        previewCreated();
    }

    public void rotateCamera() {
        if (Camera.getNumberOfCameras() > 1) {
            this.cameraId = 1 - this.cameraId;
            previewDestroyed();
            previewCreated();
        }
    }

    private void previewCreated() {
        try {
            if (mCamera == null) {
                initializeCameraAndPreview();
            }
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            orientationChange.enable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void previewDestroyed() {
        orientationChange.disable();
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        previewCreated();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        resetCamera(cameraId);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        previewDestroyed();
    }


    public void autoFocusOnly() {
        mCamera.autoFocus(null);
    }

    public void autoFocusAndCapture() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                takePicture();
            }
        });
    }

    String currentMode;

    public void setFlashMode(String flashMode) {
        currentMode = flashMode;
        Camera.Parameters params = mCamera.getParameters();
        params.setFlashMode(flashMode);
        mCamera.setParameters(params);
        updateCamHostFlashMode();
    }

    private void updateCamHostFlashMode() {
        if (currentMode == null) {
            hvCamHost.onFlashNull();
        } else {
            switch (currentMode) {
                case Camera.Parameters.FLASH_MODE_OFF:
                    hvCamHost.onFlashOff();
                    break;
                case Camera.Parameters.FLASH_MODE_AUTO:
                    hvCamHost.onFlashAuto();
                    break;
                case Camera.Parameters.FLASH_MODE_ON:
                    hvCamHost.onFlashOn();
                    break;
            }
        }
    }

    private List<String> availableFlashmodes;

    public void nextFlashMode() {
        if (availableFlashmodes != null) {
            int pos = availableFlashmodes.indexOf(currentMode);
            if (pos == availableFlashmodes.size() - 1) {
                pos = 0;
            } else {
                pos = pos + 1;
            }
            currentMode = availableFlashmodes.get(pos);
            setFlashMode(currentMode);
        }
    }

}