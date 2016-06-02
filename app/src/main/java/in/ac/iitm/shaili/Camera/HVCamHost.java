package in.ac.iitm.shaili.Camera;

/**
 * Created by Awanish Raj on 03/03/16.
 */
public abstract class HVCamHost {

    public abstract void onCamerasFound(int count);

//    public File getPhotoPath() {
//        File dir = getPhotoDirectory();
//        dir.mkdirs();
//        return (new File(dir, getPhotoFilename()));
//    }

//    public abstract File getPhotoDirectory();

//    public abstract String getPhotoFilename();

//    public abstract void onPictureTaken(File file);

    public void onFlashNull() {
    }

    public void onFlashOff() {
    }

    public void onFlashOn() {
    }

    public void onFlashAuto() {
    }

//    public abstract void onFilterMode(int filterMode, String filterName);

    public static final int ASPECT_4_3 = 1;
    public static final int ASPECT_16_9 = 2;
    public static final int ASPECT_9_16 = 3;
    public static final int ASPECT_3_4 = 4;


    public abstract int getAspectRatio();

    public abstract float getPreviewMegapixels();

    public abstract float getPictureMegapixels();

//    public abstract void onFaceDetection(Camera.Face[] faces);

    public abstract void onDataTaken(byte[] data);


}
