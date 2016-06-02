package in.ac.iitm.shaili.Views;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.ac.iitm.shaili.Caffe.CaffeResult;
import in.ac.iitm.shaili.R;

/**
 * Created by Awanish Raj on 22/05/16.
 */
public class ResultsRV extends RecyclerView {
    public ResultsRV(Context context) {
        super(context);
        init();
    }

    public ResultsRV(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResultsRV(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private ResultsAdapter mAdapter;

    private void init() {
        mAdapter = new ResultsAdapter();
        this.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));
        this.setAdapter(mAdapter);
    }

    private List<CaffeResult> results = new ArrayList<>();
    private long updateTimeStamp;

    public void updateResults(List<CaffeResult> results) {
        this.results.clear();
        this.results.addAll(results);
        updateTimeStamp = System.currentTimeMillis();
        mAdapter.notifyDataSetChanged();
    }


    private class ResultsAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ResultHolder(LayoutInflater.from(getContext()).inflate(R.layout.view_result, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CaffeResult result = results.get(position);
            ResultHolder rHolder = (ResultHolder) holder;
            rHolder.tvResult.setText(result.getClassName());
            rHolder.tvResult.setTextColor(Color.parseColor(result.getColor()));
//            Glide.with(getContext())
//                    .load(result.getImagePath())
//                    .fitCenter()
//                    .signature(new StringSignature(updateTimeStamp + result.getImagePath()))
//                    .into(rHolder.ivImage);
        }

        @Override
        public int getItemCount() {
            return results.size();
        }


        private class ResultHolder extends ViewHolder {

            private TextView tvResult;
//            private ImageView ivImage;

            public ResultHolder(View itemView) {
                super(itemView);
                tvResult = (TextView) itemView.findViewById(R.id.tvResult);
//                ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            }
        }
    }
}
