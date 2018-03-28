package com.amargodigits.movies.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.amargodigits.movies.DetailActivity;
import com.amargodigits.movies.R;
import com.amargodigits.movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.amargodigits.movies.MainActivity.LOG_TAG;
import static com.amargodigits.movies.MainActivity.mSharedPref;

/**
 *
 * MovieAdapter is used to display the content in Recycler View
 * Includes ViewHolder and Grid Item count function.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private ArrayList <Movie> mDataset;
    private Context mContext;
    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        ViewHolder(ImageView v) {
            super(v);
            mImage = v;
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public MovieAdapter(Context myContext, ArrayList <Movie> myDataset) {
        mDataset = myDataset;
        mContext = myContext;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mImage.setContentDescription(mDataset.get(holder.getAdapterPosition()).getOriginalTitle());
        holder.mImage.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putInt("MoviePosition", holder.getAdapterPosition());
                editor.apply();
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                mContext.startActivity(intent);}
                catch (Exception e) {
                Log.i(LOG_TAG, "Exception = " + e.toString());}
            }
        });

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w185")
                .appendPath(mDataset.get(holder.getAdapterPosition()).getPosterPath());

        Picasso.with(mContext)
                .load(builder.build().toString())
                .placeholder(android.R.drawable.stat_sys_download)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.mImage);
 //       Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + mDataset[position].getPosterPath()).into(holder.mImage);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

   //
   // The example for this function is taken from stackOverflow
   // https://stackoverflow.com/a/38472370/8796408
   //
    public static int gridColumnsNumber(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }
}

