package in.ac.iitm.shaili.Camera;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Awanish Raj on 03/03/16.
 */
public class HVCamUtils {

    public static void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera, int rotation) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
//        int rotation = activity.getWindowManager().getDefaultDisplay()
//                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
//        camera.setDisplayOrientation(result);
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance(int id) {
        Camera c = null;
        try {
            c = Camera.open(id); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public static void sortSizes(List<Camera.Size> sizes) {
        Collections.sort(sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                return rhs.width * rhs.height - lhs.width * lhs.height;
            }
        });
    }

    public static void sortSizeByClosestArea(List<Camera.Size> sizes, final int area) {
        Collections.sort(sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                return Math.abs((lhs.width * lhs.height) - area) - Math.abs((rhs.width * rhs.height) - area);
            }
        });
    }

    private static final String LOG_TAG = "HVCamUtils";

    public static void applyBestPreviewSize(Camera mCamera, int mRatioWidth, int mRatioHeight, float megapixels) {
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size size = getBestPreviewSize(mCamera, mRatioWidth, mRatioHeight, megapixels);
        params.setPreviewSize(size.width, size.height);
        mCamera.setParameters(params);
    }


    public static void applyBestPictureSize(Camera mCamera, int mRatioWidth, int mRatioHeight, float megapixels) {
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size size = getBestPictureSize(mCamera, mRatioWidth, mRatioHeight, megapixels);
        params.setPictureSize(size.width, size.height);
        mCamera.setParameters(params);
    }


    public static Camera.Size getBestPreviewSize(Camera mCamera, int mRatioWidth, int mRatioHeight, float megapixels) {
        List<Camera.Size> previews = mCamera.getParameters().getSupportedPreviewSizes();
        Camera.Size previewSize = getBestSize(previews, megapixels, mRatioWidth, mRatioHeight);
        Log.e(LOG_TAG, "Preview size: " + previewSize.width + " x " + previewSize.height);
        return previewSize;
    }


    public static Camera.Size getBestPictureSize(Camera mCamera, int mRatioWidth, int mRatioHeight, float megapixels) {
        List<Camera.Size> captures = mCamera.getParameters().getSupportedPictureSizes();
        Camera.Size captureSize = getBestSize(captures, megapixels, mRatioWidth, mRatioHeight);
        Log.e(LOG_TAG, "Capture size: " + captureSize.width + " x " + captureSize.height);
        return captureSize;

    }

    private static Camera.Size getBestSize(List<Camera.Size> previews, float megapixels, int mRatioWidth, int mRatioHeight) {
        HVCamUtils.sortSizeByClosestArea(previews, (int) (megapixels * 1000000));
        int mx = mRatioWidth;
        int my = mRatioHeight;
        if (mx > my) {
            mx = mRatioHeight;
            my = mRatioWidth;
        }
        for (Camera.Size size : previews) {
            if (size.width * mx == size.height * my) {
                return size;
            }
        }
        Log.e(LOG_TAG, "Found no fitting size");
        return null;
    }

    public static int lastRotation = -1;

    public static void setPictureOrientation(int cameraId, Camera mCamera, int orientation) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        orientation = (orientation + 45) / 90 * 90;

        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {  // back-facing camera
            rotation = (info.orientation + orientation) % 360;
        }

        if (lastRotation != rotation) {
            Camera.Parameters params = mCamera.getParameters();
            if (params.get("rotation") == null || !params.get("rotation").equals("" + rotation)) {
                Log.e(LOG_TAG, "Setting capture rotation to: " + rotation + " from: " + params.get("rotation"));
                params.setRotation(rotation);
                mCamera.setParameters(params);
            }
            lastRotation = rotation;
        }
    }

    public static void scanMediaFile(Context context, String file) {
        scanMediaFile(context, new File(file));
    }

    public static void scanMediaFile(Context context, File file) {
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scanFileIntent);
    }

    public static boolean hasSupport_16_9(Camera camera) {
        List<Camera.Size> preview = camera.getParameters().getSupportedPreviewSizes();
        for (Camera.Size size : preview) {
            if (size.width * 9 == size.height * 16)
                return true;
        }
        return false;
    }

}
