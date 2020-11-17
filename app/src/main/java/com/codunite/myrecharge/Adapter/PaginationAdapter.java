package com.codunite.myrecharge.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.codunite.myrecharge.Models.HistoryModel;
import com.codunite.myrecharge.R;

import java.util.ArrayList;
import java.util.List;

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";

    private List<HistoryModel> movieHistoryModels;
    private Context context;

    private boolean isLoadingAdded = false;

    public PaginationAdapter(Context context) {
        this.context = context;
        movieHistoryModels = new ArrayList<>();
    }


    public List<HistoryModel> getMovies() {
        return movieHistoryModels;
    }

    public void setMovies(List<HistoryModel> movieHistoryModels) {
        this.movieHistoryModels = movieHistoryModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_list, parent, false);
        viewHolder = new MovieVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        HistoryModel result = movieHistoryModels.get(position); // Movie

        switch (getItemViewType(position)) {
            case ITEM:
                final MovieVH movieVH = (MovieVH) holder;

                movieVH.txt_Amount.setText(result.getAmount().toString());
                movieVH.txt_description.setText(result.getNarration());

                if(result.getAmount()>0) {
                    movieVH.txt_type.setText("Cr");
                    movieVH.txt_Amount.setTextColor(Color.parseColor("#0b892e"));
                } else {
                    movieVH.txt_type.setText("Dr");
                    movieVH.txt_Amount.setTextColor(Color.parseColor("#ed1b24"));
                }

                break;

            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return movieHistoryModels == null ? 0 : movieHistoryModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieHistoryModels.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(HistoryModel r) {
        movieHistoryModels.add(r);
        notifyItemInserted(movieHistoryModels.size() - 1);
    }

    public void addAll(List<HistoryModel> moveHistoryModels) {
        for (HistoryModel result : moveHistoryModels) {
            add(result);
        }
    }

    public void remove(HistoryModel r) {
        int position = movieHistoryModels.indexOf(r);
        if (position > -1) {
            movieHistoryModels.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new HistoryModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieHistoryModels.size() - 1;
        HistoryModel result = getItem(position);

        if (result != null) {
            movieHistoryModels.remove(position);
            notifyItemRemoved(position);
        }
    }

    public HistoryModel getItem(int position) {
        return movieHistoryModels.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class MovieVH extends RecyclerView.ViewHolder {
        private TextView txt_type;
        private TextView txt_id;
        private TextView txt_description,txt_Amount;
        private ImageView mPosterImg;
        private ProgressBar mProgress;

        public MovieVH(View itemView) {
            super(itemView);

            txt_type = (TextView) itemView.findViewById(R.id.txt_type);
            txt_id = (TextView) itemView.findViewById(R.id.txt_id);
            txt_description = (TextView) itemView.findViewById(R.id.txt_description);
            txt_Amount = (TextView) itemView.findViewById(R.id.txt_Amount);
            mPosterImg = (ImageView) itemView.findViewById(R.id.movie_poster);
            mProgress = (ProgressBar) itemView.findViewById(R.id.movie_progress);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}

