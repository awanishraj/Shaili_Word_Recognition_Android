package in.ac.iitm.shaili.Caffe;

import android.util.Log;

import com.sh1r0.caffe_android_lib.CaffeMobile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Awanish Raj on 08/05/16.
 */
public class CaffeManager {

    private static final String LOG_TAG = "CaffeManager";


    private static final String DEPLOY_PROTO = "/sdcard/caffe_mobile/deploy.prototxt";
    private static final String CAFFE_MODEL = "/sdcard/caffe_mobile/shaili_10000_96.caffemodel";
    private static final String CLASSES_LIST = "/sdcard/caffe_mobile/classes.txt";

    static {
        System.loadLibrary("caffe");
        System.loadLibrary("caffe_jni");
    }

    private static CaffeMobile caffeMobile;
    private static String[] IMAGENET_CLASSES;

    private static boolean checkFiles() {
        boolean success = true;
        if (!new File(DEPLOY_PROTO).exists()) {
            Log.e(LOG_TAG, "Deploy Prototxt missing");
            success = false;
        }
        if (!new File(CAFFE_MODEL).exists()) {
            Log.e(LOG_TAG, "Caffe Model missing");
            success = false;
        }
        if (!new File(CLASSES_LIST).exists()) {
            Log.e(LOG_TAG, "Classes file missing");
            success = false;
        }

        return success;
    }


    public static void init(String folder_name) {

        String deployProto = "/sdcard/caffe_mobile/" + folder_name + "/deploy.prototxt";
        String modelCaffe = "/sdcard/caffe_mobile/" + folder_name + "/model.caffemodel";
        String classesList = "/sdcard/caffe_mobile/" + folder_name + "/classes.txt";

//        if (!checkFiles()) {
//            Log.e(LOG_TAG, "Initialization Failed");
//            return;
//        }
        if (caffeMobile == null) {
            caffeMobile = new CaffeMobile();
            caffeMobile.setNumThreads(4);
            caffeMobile.loadModel(deployProto,
                    modelCaffe);

            caffeMobile.setScale((float) (1.0 / 255.0));

            try {
                InputStream is = new FileInputStream(classesList);
                Scanner sc = new Scanner(is);
                List<String> lines = new ArrayList<>();
                while (sc.hasNextLine()) {
                    final String temp = sc.nextLine();
                    lines.add(temp.substring(temp.indexOf(" ") + 1));
                }
                IMAGENET_CLASSES = lines.toArray(new String[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i(LOG_TAG, "Initialization successful");
        }
    }

//    public static String analyzeImage(String path) {
//        int result = caffeMobile.predictImage(path)[0];
//        return IMAGENET_CLASSES[result];
//    }

    public static CaffeResult getResult(String path) {
//        if(caffeMobile==null) init("android_sea");
        float[] confs = caffeMobile.getConfidenceScore(path);

//        float[][] features = caffeMobile.extractFeatures(path, "ip1");
//        Log.e(LOG_TAG, "Features dimensions: " + features.length + " x " + features[0].length);
//
//        for (float[] arr : features) {
//            String vector = "";
//            for (float val : arr) {
//                vector = vector + val + ",";
//            }
//            Log.e(LOG_TAG, "Features vector: " + vector);
//
//        }

        List<CaffeResult> results = new ArrayList<>();
        for (int i = 0; i < confs.length; i++) {
            results.add(new CaffeResult(i, IMAGENET_CLASSES[i], confs[i], path));
        }

        Collections.sort(results, new Comparator<CaffeResult>() {
            @Override
            public int compare(CaffeResult lhs, CaffeResult rhs) {
                return (int) (rhs.getConfidence() * 1000) - (int) (lhs.getConfidence() * 1000);
            }
        });

        return results.get(0);
    }
}
