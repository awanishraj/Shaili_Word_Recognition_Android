package in.ac.iitm.shaili.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import in.ac.iitm.shaili.R;
import in.ac.iitm.shaili.Views.CropView;

public class SkewCropActivity extends AppCompatActivity {

    private ImageView ivResult;

    public static void start(Context context, String filePath) {
        Intent i = new Intent(context, SkewCropActivity.class);
        i.putExtra("filepath", filePath);
        context.startActivity(i);
    }

    CropView cropView;
    Button btNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skew_crop);
        cropView = (CropView) findViewById(R.id.cropView);
        ivResult = (ImageView) findViewById(R.id.ivResult);
        btNext = (Button) findViewById(R.id.btNext);

        String filePath = getIntent().getStringExtra("filepath");
        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        cropView.setImageBitmap(bmp);
        cropView.attachImageView(ivResult);


        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    CaffeResultActivity.start(SkewCropActivity.this, cropView.getSkewedBitmap());
            }
        });
    }
}
