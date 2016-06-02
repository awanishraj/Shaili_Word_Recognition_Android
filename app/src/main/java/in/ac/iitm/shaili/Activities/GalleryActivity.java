package in.ac.iitm.shaili.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.ac.iitm.shaili.R;
import in.ac.iitm.shaili.Utils.Constants;

/**
 * Created by Awanish Raj on 25/05/16.
 */
public class GalleryActivity extends Activity {

    private static final String LOG_TAG = "GalleryActivity";
    private RecyclerView rvGallery;


    public static void start(Context context) {
        Intent i = new Intent(context, GalleryActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        rvGallery = (RecyclerView) findViewById(R.id.rvGallery);
        rvGallery.setAdapter(new GalleryAdapter());
        rvGallery.setLayoutManager(new GridLayoutManager(GalleryActivity.this, 3));

    }

    private class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

        private List<File> files;

        public GalleryAdapter() {
            File gallery = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), Constants.CAPTURE_PATH);
            if (!gallery.exists()) {
                gallery.mkdirs();
            }
            files = Arrays.asList(gallery.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return (pathname.getName().contains(".png"));
                }
            }));
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return (int) (rhs.lastModified() / 1000 - lhs.lastModified() / 1000);
                }
            });
        }

        @Override
        public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(GalleryActivity.this).inflate(R.layout.item_gallery, parent, false));
        }

        @Override
        public void onBindViewHolder(GalleryAdapter.ViewHolder holder, int position) {
            Glide.with(GalleryActivity.this)
                    .load(files.get(position))
                    .asBitmap()
                    .dontAnimate()
                    .fitCenter()
//                    .into(new BitmapImageViewTarget(holder.ivImage) {
//                        @Override
//                        protected void setResource(Bitmap resource) {
//                            super.setResource(BinarizeAdaptive.thresh(resource));
//                        }
//                    });
                    .into(holder.ivImage);
        }

        @Override
        public int getItemCount() {
            return files.size();
        }

        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = rvGallery.getChildLayoutPosition(v);
                File clickedImage = files.get(position);
                SkewCropActivity.start(GalleryActivity.this, clickedImage.getAbsolutePath());

//                CaffeResultActivity.start(GalleryActivity.this, clickedImage.getAbsolutePath());
            }
        };

        private View.OnLongClickListener longListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = rvGallery.getChildLayoutPosition(v);
                File clickedImage = files.get(position);
                SkewCropActivity.start(GalleryActivity.this, clickedImage.getAbsolutePath());
                return false;
            }
        };

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivImage;

            public ViewHolder(View itemView) {
                super(itemView);
                ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
                itemView.setOnClickListener(clickListener);
                itemView.setOnLongClickListener(longListener);
            }
        }
    }

}
