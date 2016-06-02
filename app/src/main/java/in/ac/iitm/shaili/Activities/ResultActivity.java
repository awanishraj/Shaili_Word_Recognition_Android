package in.ac.iitm.shaili.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.itextpdf.text.DocumentException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

import in.ac.iitm.shaili.Helpers.PDFHelper;
import in.ac.iitm.shaili.Helpers.ReportBuilder;
import in.ac.iitm.shaili.Network.MultipartPost;
import in.ac.iitm.shaili.R;
import in.ac.iitm.shaili.Utils.Constants;

/**
 * Created by Awanish Raj on 19/06/15.
 */
public class ResultActivity extends Activity {

    private static final String LOG_TAG = "ResultActivity";

    public static final String EXTRA_FILEPATH = "extra_filepath";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ImageView iv_result = (ImageView) findViewById(R.id.iv_result);

        /**
         * Getting the intent from the previous activity. The filePath from the original image comes here.
         */
        Intent i = getIntent();
        final String filePath = i.getStringExtra(EXTRA_FILEPATH);

        /**
         * Loading the image into the imageview
         */
        Glide.with(this).load(filePath)
                .into(iv_result);


        /**
         * The Asynctask for fetching translation from network
         */
        new AsyncTask<Void, Void, String>() {
            long oldtime1;

            @Override
            protected String doInBackground(Void... params) {

                oldtime1 = System.currentTimeMillis();
                try {
                    /**
                     * Executing the multipart post
                     */
                    MultipartPost multipartPost = new MultipartPost(Constants.URL_PROCESSING);
                    multipartPost.addFileEntity("image", "image/png", filePath);
                    JSONObject response = new JSONObject(multipartPost.executeRequest());
                    /**
                     * Decode the response from the server
                     */
                    String ocr = response.getString(Constants.RES_OCR);
                    String translation = response.getString(Constants.RES_TRANSLATION);
                    return ocr + ";shaili;" + translation;
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Unable to connect to the server";

                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error in server's response";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                /**
                 * Showing the result in the response box
                 */
                if (result.contains(";shaili;")) {
//                    tv_result.setText(result.replace(";shaili;", "\n"));
                    if(Constants.debugMode){
                    try {
                        ReportBuilder report = new ReportBuilder();
                        report.fileOrig = filePath.replace("ADAP", "NONE");
                        report.fileOtsu = filePath.replace("ADAP", "OTSU");
                        report.fileAdap = filePath;
                        report.ocr = result.split(";shaili;")[0];
                        report.translation = result.split(";shaili;")[1];

                        PDFHelper.write(report);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }}
                } else
//                    tv_result.setText(result);
                /**
                 * Toasting time taken in the network request
                 */
                Toast.makeText(ResultActivity.this, "Time taken: " + (System.currentTimeMillis() - oldtime1) / 1000.0 + "s", Toast.LENGTH_SHORT).show();
            }
        }.execute();

    }

    /**
     * Start the previous activity on back press
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
        finish();
    }
}
