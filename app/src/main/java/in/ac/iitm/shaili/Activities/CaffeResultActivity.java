package in.ac.iitm.shaili.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.ac.iitm.shaili.Caffe.CaffeManager;
import in.ac.iitm.shaili.Caffe.CaffeResult;
import in.ac.iitm.shaili.ImageProcessing.BinarizeAdaptive;
import in.ac.iitm.shaili.ImageProcessing.BinarizeOtsu;
import in.ac.iitm.shaili.ImageProcessing.BlobDetection;
import in.ac.iitm.shaili.R;
import in.ac.iitm.shaili.Views.ResultsRV;

/**
 * Created by Awanish Raj on 19/06/15.
 */
public class CaffeResultActivity extends Activity {

    private static final String LOG_TAG = "ResultActivity";

    public static final String EXTRA_FILEPATH = "extra_filepath";
    private ImageView iv_result;
    private ResultsRV rvResult;

    private static Bitmap bmpOriginal;

    public static void start(Context context, String filePath) {
        Intent i = new Intent(context, CaffeResultActivity.class);
        i.putExtra(EXTRA_FILEPATH, filePath);
        context.startActivity(i);
    }

    public static void start(Context context, Bitmap bmp) {
        Intent i = new Intent(context, CaffeResultActivity.class);
        bmpOriginal = bmp;
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        iv_result = (ImageView) findViewById(R.id.iv_result);
        rvResult = (ResultsRV) findViewById(R.id.rvResult);

        /**
         * Getting the intent from the previous activity. The filePath from the original image comes here.
         */
        Intent i = getIntent();
        if (i.hasExtra(EXTRA_FILEPATH)) {
            final String filePath = i.getStringExtra(EXTRA_FILEPATH);
            BitmapFactory.Options mOpts = new BitmapFactory.Options();
            mOpts.inMutable = true;
            bmpOriginal = BitmapFactory.decodeFile(filePath, mOpts);
        }

        iv_result.setImageBitmap(bmpOriginal);
        processImage(bmpOriginal);
    }

    private void processImage(final Bitmap image) {

        new AsyncTask<Void, Void, Void>() {
            public Bitmap bmpThresh;
            public Bitmap bmpBinary;
            List<String> clippedFiles;
            long lastUpdate;
            long thresholdingTime;
            long blobDetectionTime;
            List<CaffeResult> results = new ArrayList<>();

            @Override
            protected Void doInBackground(Void... params) {

                lastUpdate = System.currentTimeMillis();

//                bmpThresh = BinarizeOtsu.thresh(image);
//                bmpThresh = BinarizeAdaptiveOld.thresh(image);
                bmpThresh = BinarizeAdaptive.thresh(image);
                bmpBinary = bmpThresh.copy(bmpThresh.getConfig(), false);


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        iv_result.setImageBitmap(bmpThresh);
                    }
                });

                thresholdingTime = System.currentTimeMillis() - lastUpdate;
                lastUpdate = System.currentTimeMillis();
                clippedFiles = BlobDetection.clipBlobsFromBitmap(bmpThresh, null);
                blobDetectionTime = System.currentTimeMillis() - lastUpdate;

                results.clear();
                for (String path : clippedFiles) {
                    CaffeResult result = CaffeManager.getResult(path);
                    results.add(result);
                }

                Log.e(LOG_TAG, "Recognition output: " + TextUtils.join(" ", results));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                iv_result.setImageBitmap(bmpThresh);
                rvResult.updateResults(results);
//                rvResult.setVisibility(View.GONE);

                Toast.makeText(CaffeResultActivity.this,
                        "Thresholding: " + thresholdingTime
                                + "\nLocalization: " + blobDetectionTime,
                        Toast.LENGTH_SHORT).show();
                iv_result.setOnClickListener(new View.OnClickListener() {
                    int count = 0;

                    @Override
                    public void onClick(View v) {
                        switch (count) {
                            case 0:
                                iv_result.setImageBitmap(bmpOriginal);
                                break;
                            case 1:
                                iv_result.setImageBitmap(bmpBinary);
                                break;
                            case 2:
                                iv_result.setImageBitmap(bmpThresh);
                                break;
                        }
                        count++;
                        if (count == 3) count = 0;
                    }
                });

            }
        }.execute();

    }


}
