package com.amargodigits.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.amargodigits.movies.model.Movie;
import com.amargodigits.movies.utils.EndlessRecyclerViewScrollListener;
import com.amargodigits.movies.utils.MovieAdapter;
import com.amargodigits.movies.utils.NetworkUtils.LoadDataTask;

import java.util.ArrayList;

import static com.amargodigits.movies.DetailActivity.mContext;
import static com.amargodigits.movies.utils.MovieAdapter.gridColumnsNumber;
import static com.amargodigits.movies.utils.NetworkUtils.isOnline;

public class MainActivity extends AppCompatActivity {
    public static String LOG_TAG = "Movies Log";
    public static ArrayList <Movie> movieList = new ArrayList<>();
    public static RecyclerView mRecyclerView;
    //    public static TextView mPopularItem;
//    public static TextView mTopratedItem;
    public String sortOrder;
    public static SharedPreferences mSharedPref;
    static MovieAdapter mAdapter;
    public static RecyclerView.LayoutManager mLayoutManager;
    static Toolbar mainToolbar;
    Menu mMenu;
    private static EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.movie_rv);
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        sortOrder = mSharedPref.getString("SORT", "popular");
        Log.i(LOG_TAG, "MainActivity onCreate sortOrder = " + sortOrder);
        if (!( sortOrder.contains("liked") || sortOrder.contains("top_rated") || sortOrder.contains("popular") )) {
            sortOrder = "top_rated";
        }
        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        mainToolbar.setTitle(makeTitle(sortOrder));
        setSupportActionBar(mainToolbar);

        if (sortOrder.contains("liked")) {
            LoadDataTask mLikedAsyncTask = new LoadDataTask(getApplicationContext());
            mLikedAsyncTask.execute("liked","0");
        } else {
            if (isOnline(getApplicationContext())) {
                try {
                    LoadDataTask mAsyncTasc = new LoadDataTask(getApplicationContext());
                    mAsyncTasc.execute(sortOrder, "1");
                } catch (Exception e) {
                    Log.i(LOG_TAG, "onCreateException " + e.toString());
                }
            } else {
                Toast.makeText(this, "Network connection required", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void doRecView(Context tContext, String sortOrder, String pageN) {
        Log.i(LOG_TAG, "MainActivity doRecView pageN="+pageN);
        if (sortOrder.contains("liked")){
            mAdapter = new MovieAdapter(tContext, movieList);
            mRecyclerView.setHasFixedSize(true);
            GridLayoutManager mLayoutManager = new GridLayoutManager(tContext, gridColumnsNumber(tContext));
            mainToolbar.setTitle(makeTitle(sortOrder));
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
            return;
        }
        if (pageN=="1") {
            mAdapter = new MovieAdapter(tContext, movieList);
            mRecyclerView.setHasFixedSize(true);
            GridLayoutManager mLayoutManager = new GridLayoutManager(tContext, gridColumnsNumber(tContext));
            mainToolbar.setTitle(makeTitle(sortOrder));
            mRecyclerView.setLayoutManager(mLayoutManager);
                // Retain an instance so that you can call `resetState()` for fresh searches
                scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        Log.i(LOG_TAG, "MainActivity onLoadMore");
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        loadNextDataFromApi(page);
                    }
                };
                // Adds the scroll listener to RecyclerView
                mRecyclerView.addOnScrollListener(scrollListener);
                mRecyclerView.setAdapter(mAdapter);
    } else {

            mAdapter.notifyItemInserted(mAdapter.getItemCount()+1);
            mAdapter.notifyItemInserted(mAdapter.getItemCount()+2);
            mAdapter.notifyItemInserted(mAdapter.getItemCount()+3);
            mAdapter.notifyItemInserted(mAdapter.getItemCount()+4);

    //       mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), movieList.length );

        //    mAdapter = new MovieAdapter(tContext, movieList);
            Log.i(LOG_TAG, "movieList.length=" + movieList.size());
            Log.i(LOG_TAG, "mAdapter.getItemCount()=" +mAdapter.getItemCount());

//           mRecyclerView.setAdapter(mAdapter);
//           mRecyclerView.smoothScrollToPosition(movieList.length);

        }
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public static void loadNextDataFromApi(int offset) {
        Log.i(LOG_TAG, "MainActivity loadNextDataFromApi offset=" + offset);
        LoadDataTask mAsyncTasc = new LoadDataTask(mContext);
        String pageN = offset + "";
        Log.i(LOG_TAG, "MainActivity loadNextDataFromApi offset pageN=" + pageN);
        try {
            mAsyncTasc.execute("top_rated", pageN);
        }catch (Exception e) {
            Log.i(LOG_TAG, "MainActivity loadNextDataFromApi Exception "+ e.toString());
        }
        mAdapter.notifyDataSetChanged();
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        mMenu = menu;
        sortOrder = mSharedPref.getString("SORT", "popular");
        try {
            colorMenu(sortOrder);
        } catch (Exception e) {
            Log.i(LOG_TAG, "MainActivity onCreateOptionsMenu colorMenu Exception " + e.toString());
        }
        return true;
    }

    public void colorMenu(String sortOrder) {
//        mSharedPref = getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = mSharedPref.edit();
//        editor.putString("SORT", sortOrder);
//        editor.apply();
        Drawable likedDrawable = mMenu.findItem(R.id.sort_liked).getIcon();
        Drawable popularDrawable = mMenu.findItem(R.id.sort_popular).getIcon();
        Drawable top_ratedDrawable = mMenu.findItem(R.id.sort_toprated).getIcon();
        switch (sortOrder) {
            case "popular": {
                popularDrawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                top_ratedDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                likedDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                break;
            }
            case "top_rated": {
                popularDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                top_ratedDrawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                likedDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                break;
            }
            case "liked": {
                popularDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                top_ratedDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                likedDrawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                break;
            }
        }
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     *
     * @param item The menu item that was selected by the user
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPref.edit();
         movieList.clear();
        switch (id) {
            case R.id.sort_popular: {
                colorMenu("popular");
                editor.putString("SORT", "popular");
                editor.apply();
                LoadDataTask mAsyncTasc = new LoadDataTask(getApplicationContext());
                mAsyncTasc.execute("popular", "1");
                return true;
            }
            case R.id.sort_toprated: {
                colorMenu("top_rated");
                editor.putString("SORT", "top_rated");
                editor.apply();
                LoadDataTask mAsyncTasc = new LoadDataTask(getApplicationContext());
                mAsyncTasc.execute("top_rated", "1");
                return true;
            }
            case R.id.sort_liked: {
                colorMenu("liked");
                editor.putString("SORT", "liked");
                editor.apply();
                LoadDataTask mAsyncTask = new LoadDataTask(getApplicationContext());
                mAsyncTask.execute("liked", "1");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private static String makeTitle(String sortOrder) {
        sortOrder = sortOrder.replace("_", " ");
        return sortOrder.substring(0, 1).toUpperCase() + sortOrder.substring(1) + " movies";
    }
}